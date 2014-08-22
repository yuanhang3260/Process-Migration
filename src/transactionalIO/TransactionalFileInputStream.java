package transactionalIO;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.io.Serializable;

public class TransactionalFileInputStream extends InputStream implements Serializable{
    private static final long serialVersionUID = -1535337478478140577L;
    private String filename;
    private long offset;
    private RandomAccessFile file;
    private boolean migrated;
    
    public TransactionalFileInputStream(String filename) throws FileNotFoundException {
        this.filename = filename;
        this.offset = 0L;
        this.file = new RandomAccessFile(filename, "r");
        this.migrated = false;
    }

    @Override
    public int read() throws IOException {
        if (migrated) {
            file = new RandomAccessFile(filename, "r");
            migrated = false;
        }
        
        file.seek(offset);
        int data = file.read();
        if (data != -1) {
            offset++;
        }
        
        return data;
    }
    
    @Override
    public void close() throws IOException {
        migrated = true;
        file.close();
    }
}
