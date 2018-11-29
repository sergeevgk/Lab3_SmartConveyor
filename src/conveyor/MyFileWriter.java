package conveyor;

import config.GrammarWorker;
import huffman.HuffmanAlgorithmResult;
import log.Log;

import java.io.*;
import java.util.Map;

public class MyFileWriter {
    private BufferedOutputStream writerResult;
    //private BufferedOutputStream writerTable;
    // private boolean tableWritten;
    private String codeMode;

    public MyFileWriter(Map<GrammarWorker, String> configWorker, BufferedOutputStream outputStream) {
        this.writerResult = outputStream;
        this.codeMode = configWorker.get(GrammarWorker.CODE_MODE);
    }
   /* public conveyor.MyFileWriter(Options options) throws IOException {
        FileOutputStream outStreamResult = new FileOutputStream(options.configMain.get(GrammarMain.OUT));
        this.writerResult = new BufferedOutputStream(outStreamResult);
        codeMode = options.configOptions.get(GrammarOptions.CODE_MODE);
        if (options.configOptions.get(GrammarOptions.HUFFMAN_TABLE) != null && codeMode.equals("0")) {
            FileOutputStream outStreamTable = new FileOutputStream(options.configOptions.get(GrammarOptions.HUFFMAN_TABLE));
            this.writerTable = new BufferedOutputStream(outStreamTable);
        }

        tableWritten = false;
    }*/

    /**
     * @param result HuffmanAlgorithmResult instance contains byte[] res - result of huffman algorithm work
     *               <p>
     *               smethod writes byte[] res to output buffered stream
     */
    public final void writeOutputFile(HuffmanAlgorithmResult result) { //writeToFile
        try {
            writerResult.write(result.getResult());//////////////
            writerResult.flush();
           /* if (codeMode.equals("0"))
                writeHuffmanTable(result.getHuffmanTable());*/
        } catch (IOException e) {
            Log.logReport("Writing output file error.");
            return;
        }
    }

    /*public void writeHuffmanTable(Map<Byte, String> huffmanTable) throws IOException {
        if (tableWritten)
            return;
        for (byte c : huffmanTable.keySet()) {
            writerTable.write(c);
            writerTable.write(("=" + huffmanTable.get(c) + System.lineSeparator()).getBytes());
        }
        tableWritten = true;
        writerTable.flush();
    }*/

    /**
     * closes streams
     */
    public final void close() {
        try {
            writerResult.close();
           /* if (codeMode.equals("0"))
                writerTable.close();*/
        } catch (IOException e) {

        }
    }
}
