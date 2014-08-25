package Tests;

import MigratableProcess.MigratableProcess;

public class Bar implements MigratableProcess {

    private static final long serialVersionUID = -4061023427883449135L;
    private int lucky;
    
    public Bar(String[] hehe) {
        lucky = Integer.parseInt(hehe[0]);
    }

    @Override
    public void run() {
        System.out.println("Bar is running");
        System.out.println("Lucky ball " + lucky);
        
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
