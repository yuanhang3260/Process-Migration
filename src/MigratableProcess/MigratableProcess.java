package MigratableProcess;

import java.lang.Runnable;
import java.io.Serializable;

public interface MigratableProcess extends Runnable, Serializable {
	
	void suspend();

	String toString();
	
	int getStatus();

}
