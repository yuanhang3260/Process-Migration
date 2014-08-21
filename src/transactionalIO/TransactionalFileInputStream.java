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
    
    public TransactionalFileInputStream(String filename) {
        this.filename = filename;
        this.offset = 0L;
        try {
            this.file = new RandomAccessFile(filename, "r");
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public int read() throws IOException {
        // TODO Auto-generated method stub
        return 0;
    }

}
