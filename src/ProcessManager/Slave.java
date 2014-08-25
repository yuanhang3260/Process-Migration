package ProcessManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ConcurrentHashMap;

import MigratableProcess.MigratableProcess;

public class Slave {

/********************************* Fields *************************************/
	private int listenPort = 9090;
    private String name = null;
    private ConcurrentHashMap<Integer, MigratableProcess> processTable = null;
    private int num;

/******************************** Methods *************************************/
    /** @brief Slave Constructor */
    public Slave(String name, int listenPort) throws UnknownHostException 
    {
        this.listenPort = listenPort;
        this.name = name;
        this.processTable = new ConcurrentHashMap<Integer, MigratableProcess>();
        this.num = 0;
    }
    
    /** @brief Usage */
    private void usage() {
        System.err.println("Usage:");
        System.err.println("(1) run classname [arg1] [arg2] ...");
        System.err.println("(2) migrate pid dest_IP, dest_port");
        System.err.println("(3) exit");
    }
    
    /** @brief Run the slave */
    public void runSlave() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String cmdLine = null;
        
        /* start the running process receiver thread */
        Thread listen_thread = new Thread(new MigrationReceiver(this.listenPort, this));
        listen_thread.start();

        while (true) {
            System.out.print(name + "Slave> ");
            
            cmdLine = br.readLine();
            String[] tokens = cmdLine.split(" ");

            if (tokens[0].equals("exit")) {
                //TODO: close the listening thread and all other threads? */
                System.exit(0);
            }
            
            if (tokens.length < 2) {
                System.err.println("Error: Wrong number of arguments!");
                usage();
                continue;
            }
                
            String command = tokens[0];
            String className = tokens[1];
            
            /* Run a process */
            if (command.equals("run")) {
                try {
                    Class<?> aClass = Class.forName(className);
                    MigratableProcess process = null;
                    
                    /* Constructor with arguments */
                    if (tokens.length > 2) {
                        String[] parameters = new String[tokens.length - 2];
                        
                        for (int i = 0; i < parameters.length; i++) {
                            parameters[i] = tokens[i + 2];
                        }
                        
                        Constructor<?> constructor = aClass.getConstructor(new Class[]{String[].class});
                        process = (MigratableProcess) constructor.newInstance((Object)parameters);
                        
                    /* Constructor without arguments */
                    }  else {
                        Constructor<?>[] constructors = aClass.getConstructors();
                        process = (MigratableProcess) constructors[0].newInstance();
                    }

                    Thread process_thread = new Thread(process);
                    process_thread.start();

                    this.num++;
                    this.processTable.put(this.num, process);
                    
                } catch (ClassNotFoundException e) {
                    System.err.println("Class " + className + " is not found!");
                } catch (NoSuchMethodException e) {
                    System.err.println("No valid constructor is found in class " + className);
                } catch (IllegalArgumentException e) {
                    System.err.println("Illegal argument for the constructor of class " + className);
                } catch (InstantiationException e) {
                    System.out.println("Instantiation of class " + className + " failed. Please check if it is an abstract class");
                } catch (IllegalAccessException e) {
                    System.out.println("No access to the constructor of class " + className);
                } catch (InvocationTargetException e) {
                    System.out.println("The constructor of class " + className + " threw an exception");
                }
            
            /* Migrate a Process */
            } else if (command.equals("migrate")) {
                if (tokens.length != 4) {
                    System.err.println("\"migrate\" Usage: migrate <pid> <dest_IP>, <dest_port>\n");
                    continue;
                }

                int pid, dest_port;
                try {
                     pid = Integer.parseInt(tokens[1]);
                     dest_port = Integer.parseInt(tokens[3]);
                }
                catch (NumberFormatException nfe) {
                    System.err.println("Error: Please input valid pid and dest Node port");
                    continue;
                }
                String dest_IP = tokens[2];
                
                migrateProcess(pid, dest_IP, dest_port);

            } else {
                usage();
            }
        }
    }


    /** @brief migrate a process 
     *  @param pid the process ID
     *  @param dest_ip dest Node IP address
     *  @param dest_port dest Node listening port
     */
    private void migrateProcess(int pid, String dest_IP, int dest_port) {
        MigratableProcess processToMigrate = this.processTable.get(pid);
        if (processToMigrate == null) {
            System.err.printf("Error: can't find process with pid %d\n", pid);
        }  

        Socket socket = null;

        try {
            socket = new Socket(dest_IP, dest_port);
        } catch (IOException e) {
            System.err.println("runSlave(): IOException when instantiating Socket.");
        }
        
        ObjectOutputStream objOutput = null;
        try {
            objOutput = new ObjectOutputStream(socket.getOutputStream());
            processToMigrate.suspend();
            objOutput.writeObject(processToMigrate);
            objOutput.close();
            this.processTable.remove(pid);
            System.out.println("Process is transferred");
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return;
    }


/**************************** MigrationReceiver ***********************************/
    public class MigrationReceiver implements Runnable {
        private int listenPort;
        private volatile boolean running;
        private volatile boolean suspending;
        private volatile boolean intialized;
        private Slave slave;
        
        public MigrationReceiver(int port, Slave slave) {
            this.listenPort  = port;
            this.running = true;
            this.suspending = false;
            this.intialized = false;
            this.slave = slave;
        }
        
        private void kill () {
            this.suspending = true;
            while (suspending && running) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        
        @Override
        public void run() {
            ServerSocket listenSocket = null;
            try {
                listenSocket = new ServerSocket();
                listenSocket.bind(new InetSocketAddress(this.listenPort));
                listenSocket.setSoTimeout(1000);
            } catch (IOException e) {
                System.out.println("Error: MigrationReceiver.run() opening server socket failed.");
                this.running = false;
                return;
            }

            this.intialized = true;
            while (!this.suspending) {
                /* try accept new connection from other slaves */
                Socket socket = null;
                try {
                    socket = listenSocket.accept();
                    if (socket == null) {
                        continue;
                    }
                } catch (IOException e) {
                    continue;
                }
                
                /* receive MigratableProcess object */
                ObjectInputStream objInput = null;

                try {
                    objInput = new ObjectInputStream(socket.getInputStream());
                } catch (IOException e) {
                    System.out.println("MigrationReceiver.run():IOException when creating In/Output stream");
                    e.printStackTrace();
                    continue;
                }

                MigratableProcess migratedProcess = null;
                    
                try {
                    migratedProcess = (MigratableProcess)objInput.readObject();
                } catch (IOException e) {
                    System.out.println("MigrationHandler.run(): IOException when reading process.");
                    continue;
                } catch (ClassNotFoundException e) {
                    System.out.println("MigrationHandler.run(): ClassNotFoundException when reading process.");
                    continue;
                }
                if (migratedProcess == null) {
                    continue;
                }

                Thread newThread = new Thread(migratedProcess);
                newThread.start();
                System.out.println("Recevied thread restarted");

                slave.num++;
                slave.processTable.put(slave.num, migratedProcess);
                
                try {
                    socket.close();
                    objInput.close();
                } catch (IOException e) {
                    System.out.println("MigrationReceiver.run(): IOException when receiver closing sokcet.");
                }
            }
        }
        
    }
}


