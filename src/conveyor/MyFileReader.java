package conveyor;

import config.GrammarWorker;
import log.Log;

import java.io.*;
import java.util.Map;

public class MyFileReader {
    private DataInputStream reader;
    private Integer bufferSize;
    //private Integer codeMode;

    public MyFileReader(Map<GrammarWorker, String> configWorker, DataInputStream inputStream) {
        this.reader = inputStream;
        this.bufferSize = Integer.parseInt(configWorker.get(GrammarWorker.BUFFER_SIZE));
    }

    /**
     * @return byte[] array - sequence of read bytes
     * <p>
     * reads input file using buffered input stream
     */
    public final byte[] readInputFile() { //readToBuffer
        byte[] buf = new byte[bufferSize];
        byte[] res;
        int realBufSize;
        try {
            if ((realBufSize = reader.read(buf)) == -1)
                return null;
        } catch (IOException e) {
            Log.logReport("Reading input file error.");
            return null;
        }
        int index = 0;
        res = new byte[realBufSize];
        for (byte c : buf) {
            res[index] = c;
            index += 1;
            if (index >= realBufSize)
                break;
        }
        return res;
    }

    public final void close() {
        try {
            reader.close();
        } catch (IOException e) {

        }
    }

}
