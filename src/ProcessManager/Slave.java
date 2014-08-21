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

import process.MigratableProcess;

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
    
    

}


