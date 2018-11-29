package conveyor;

import config.GrammarWorker;
import log.Log;

import java.io.*;
import java.util.ArrayList;
import java.util.Map;

public class MyFileReader {
    private BufferedInputStream reader;
    private Integer bufferSize;
    //private Integer codeMode;

    public MyFileReader(Map<GrammarWorker, String> configWorker, BufferedInputStream inputStream) {
        this.reader = inputStream;
        this.bufferSize = Integer.parseInt(configWorker.get(GrammarWorker.BUFFER_SIZE));
    }

    /*public conveyor.MyFileReader(Options options) throws IOException {
            FileInputStream inStream = new FileInputStream(options.configMain.get(GrammarMain.IN));
            this.reader = new BufferedInputStream(inStream);
            this.bufferSize = Integer.parseInt(options.configOptions.get(GrammarOptions.BUFFER_SIZE));
            //this.codeMode = Integer.parseInt(options.configOptions.get(GrammarOptions.CODE_MODE));
    }*/

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

   /* private byte[] convertToPrimitive(ArrayList<Byte> list) {
        byte[] array = new byte[list.size()];
        Byte[] Array = list.toArray(new Byte[list.size()]);
        int index = 0;
        for (Byte B : Array) {
            array[index] = Array[index];
            index += 1;
        }
        return array;
    }*/

}
