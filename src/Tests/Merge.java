package Tests;

import java.io.IOException;

import transactionalIO.TransactionalFileInputStream;
import transactionalIO.TransactionalFileOutputStream;
import MigratableProcess.MigratableProcess;

public class Merge implements MigratableProcess{

    private static final long serialVersionUID = -7853009719707155323L;
    private volatile boolean finished;
    private volatile boolean suspending;
    private TransactionalFileInputStream aInput;
    private TransactionalFileInputStream bInput;
    private TransactionalFileOutputStream output;
    private char aChar;
    private char bChar;
    
    public Merge(String[] args) throws IOException {
        if (args.length != 3) {
            System.out.println("usage: MergeSort <inputFile1> <inputFile2> <outputFile>");
            System.exit(-1);
        }
        
        finished = false;
        suspending = false;
        aInput = new TransactionalFileInputStream(args[0]);
        bInput = new TransactionalFileInputStream(args[1]);
        output = new TransactionalFileOutputStream(args[2]);
        aChar = (char) aInput.read();
        bChar = (char) bInput.read();
    }

    @Override
    public void run() {
        suspending = false;
        
        while (!suspending && !finished) {
            try {
                if (aChar == -1 && bChar == -1) {
                    finished = true;
                } else {
                    if (aChar == -1 || aChar >= bChar) {
                        output.write(bChar);
                        bChar = (char) bInput.read();
                    } else {
                        output.write(aChar);
                        aChar = (char) aInput.read();
                    }
                    
                    Thread.sleep(1000);
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        
        try {
            aInput.close();
            bInput.close();
            output.close();
        } catch (IOException e) {
            // TODO: handle exception
        }
    }

    @Override
    public void suspend() {
        suspending = true;
    }

    @Override
    public int getStatus() {
        if (finished) {
            return 1;
        } else if (suspending) {
            return 0;
        } else {
            return -1;
        }
    }

}