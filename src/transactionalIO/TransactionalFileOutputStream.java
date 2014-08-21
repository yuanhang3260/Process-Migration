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
    private RandomAccessFile file;
    private boolean migrated;
    
    public TransactionalFileOutputStream(String filename) throws FileNotFoundException {
        this.filename = filename;
        this.offset = 0L;
        this.file = new RandomAccessFile(filename, "rw");
        this.migrated = false;
    }

    @Override
    public void write(int b) throws IOException {
        if (migrated) {
            file = new RandomAccessFile(filename, "rw");
            migrated = false;
        }
        
        file.seek(offset);
        file.write(b);
        offset++;
    }
    
    @Override
    public void close() throws IOException {
        migrated = true;
        super.close();
    }

}
