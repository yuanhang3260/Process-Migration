package ProcessManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import MigratableProcess.MigratableProcess;

public class Slave implements Runnable {

/**************************** Private Field ***********************************/
	private int listenPort = 9090;
    private String name = null;
    private ConcurrentHashMap<Integer, MigratableProcess> processTable = null;
    private int num;

    // Slave Constructor
    public Slave(String name, int listenPort) throws UnknownHostException 
    {
        this.listenPort = listenPort;
        this.name = name;
        this.processTable = new ConcurrentHashMap<Integer, MigratableProcess>();
        num = 0;
    }
    
    private static void usage() {
        System.err.println("Usage:");
        System.err.println("run classname [arg1] [arg2] ...");
        System.err.println("migrate classname destination");
    }
    
    
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String cmdLine = null;
        
        while (true) {
            System.out.println("Please enter your command, God Hang");
            
            cmdLine = br.readLine();
            String[] tokens = cmdLine.split(" ");
            
            if (tokens.length < 2) {
                System.err.println("At least 2 arguments are required!");
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
                    
                    Thread thread = new Thread(process);
                    thread.start();
                    
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
                // TODO Call migration method 
            } else {
                usage();
            }
        }
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        
    }
}


