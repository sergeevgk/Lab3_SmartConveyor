package huffman;

import config.ConfigInterpreter;
import config.ConfigInterpreterHuffmanTable;
import log.Log;

import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

public class HuffmanTableReader {
    private String fileName;
    private ConfigInterpreter reader;
    private static HuffmanTableReader instance;

    public static HuffmanTableReader getInstance() {
        if (instance == null) {
            instance = new HuffmanTableReader();
        }
        return instance;
    }

    private HuffmanTableReader() {

    }

    public void init(String fileName) {
        this.fileName = fileName;
        reader = new ConfigInterpreterHuffmanTable(fileName);
    }

    public void readHuffmanTable(Map<Byte, String> huffmanTable) {
        reader.readConfiguration(huffmanTable);
    }
}
