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
    
    public TransactionalFileInputStream(String filename) throws FileNotFoundException {
        this.filename = filename;
        this.offset = 0L;
    }

    @Override
    public int read() throws IOException {
        RandomAccessFile file = new RandomAccessFile(filename, "r");        
        
        file.seek(offset);
        int data = file.read();
        if (data != -1) {
            offset++;
        }
        
        file.close();
        
        return data;
    }
}
