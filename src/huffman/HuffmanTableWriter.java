package huffman;

import log.Log;

import java.io.BufferedOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

public class HuffmanTableWriter {
    private String fileName;

    private static FileWriter writer;

    private static HuffmanTableWriter instance;

    public static HuffmanTableWriter getInstance() {
        if (instance == null) {
            instance = new HuffmanTableWriter();
        }
        return instance;
    }

    private HuffmanTableWriter() {

    }

    public void init(String fileName) {
        this.fileName = fileName;
        try {
            writer = new FileWriter(fileName);
        } catch (IOException e) {
            Log.logReport("Error during huffman table writer initialisation.\n");
        }
    }

    public void writeHuffmanTable(Map<Byte, String> huffmanTable) {
        try {
            Integer k = 0;
            for (Byte key : huffmanTable.keySet()) {
                writer.write(key.toString() +"=" + huffmanTable.get(key) + "\n");
            }
            writer.flush();
        } catch (IOException e) {
            Log.logReport("Error while writing huffman table.\n");
        }
    }
}
