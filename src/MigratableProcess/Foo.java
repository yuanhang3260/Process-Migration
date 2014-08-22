package MigratableProcess;

public class Foo implements MigratableProcess {

    @Override
    public void run() {
        System.out.println("Foo is running");
        
    }

    @Override
    public void suspend() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public int getStatus() {
        // TODO Auto-generated method stub
        return 0;
    }

}
