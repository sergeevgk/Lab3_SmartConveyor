package config;


import log.Log;

import java.io.*;
import java.util.Map;

public class ConfigInterpreterHuffmanTable extends ConfigInterpreterBase<Byte, String> {
    private String DELIMITER = "=";

    public ConfigInterpreterHuffmanTable(String fileName) {
        super(fileName);
    }

    /**
     * @param configMap - container for putting in (key, value) pair
     * @param key       string containing decimal byte value
     * @param value     realisation for huffmanTable Map(Byte, String) configuration file
     */
    @Override
    protected int addConfiguration(Map<Byte, String> configMap, Object key, Object value) {
        try {
            Byte charKey = Byte.parseByte((String) key);
            configMap.put(charKey, (String) value);
        } catch (NumberFormatException e) {
            Log.logReport("Unexpected key in table config file: " + fileName);
            return -1;
        }
        return 0;
    }

    /**
     *
     * @param configMap
     * @return
     */
    @Override
    public int readConfiguration(Map<Byte, String> configMap) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));
             BufferedInputStream stream = new BufferedInputStream(new FileInputStream(fileName))) {
            String line;
            int c;
            byte b;
            while (((line = reader.readLine()) != null)) {
                String[] set = line.split(DELIMITER);
                b = (byte) Integer.parseInt(set[0]);
                if (stream.skip(line.length()) == 0) {
                    Log.logReport("Error while reading Huffman table file: " + fileName + ".\n");
                    return -1;
                }
                addConfiguration(configMap, set[0], set[1]);
            }
        } catch (IOException e) {
            Log.logReport("Error while reading Huffman table file: " + fileName + ".\n");
            return -1;
        } catch (IndexOutOfBoundsException e) {
            Log.logReport("Array index out of bounds.\n");
            return -1;
        }

        return 0;
    }
}
