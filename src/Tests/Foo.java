package Tests;

import MigratableProcess.MigratableProcess;

public class Foo implements MigratableProcess {

    private static final long serialVersionUID = 6380583947994579888L;
    private boolean suspend = false;

    @Override
    public void run() {
        suspend = false;
        while (!suspend) {
            System.out.println("Foo is running");
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                System.out.println("Foo.run(): InterruptedException caught while thread is sleeping.");
            }
        }    
    }

    @Override
    public void suspend() {
        suspend = true;
        
    }

    @Override
    public int getStatus() {
        // TODO Auto-generated method stub
        return 0;
    }

}
