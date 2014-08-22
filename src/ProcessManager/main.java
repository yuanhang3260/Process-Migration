package ProcessManager;

import java.io.IOException;
import java.net.UnknownHostException;

import ProcessManager.Slave;

public class main {
    public static void main(String[] args)  throws UnknownHostException, IOException {
        if (args.length != 1) {
            System.err.println("Usage: main <port>");
            return;
        }

        int port = 0;
        try {
            port = Integer.parseInt(args[0]);
        }
        catch (NumberFormatException nfe) {
            System.err.println("Error: Please input valid listen port number");
            return;
        }
        Slave slave = new Slave("God Yang", port);
        slave.runSlave();
    }
}