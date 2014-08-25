package transactionalIO;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.Serializable;

public class TransactionalFileOutputStream extends OutputStream implements Serializable{
    private static final long serialVersionUID = -1905489069486936058L;
    private String filename;
    private long offset;
    
    public TransactionalFileOutputStream(String filename) throws FileNotFoundException {
        this.filename = filename;
        this.offset = 0L;
    }

    @Override
    public void write(int b) throws IOException {
        RandomAccessFile file = new RandomAccessFile(filename, "rw");
        
        file.seek(offset);
        file.write(b);
        offset++;
        
        file.close();
    }
}
