package config;

import log.Log;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

public abstract class ConfigInterpreterBase<T, Q> implements ConfigInterpreter<T, Q> {
    protected String fileName;
    protected final String DELIMITER = "=";

    /**
     * @param fileName - name of input configuration file
     *                 constructor for abstract class ConfigInterpreterBase
     */
    protected ConfigInterpreterBase(String fileName) {
        this.fileName = fileName;
    }

    /**
     * @param configMap - container for putting in (key, value) pair
     * @param key       string containing key value
     * @param value     puts (key, value) pair in configMap
     */
    protected abstract int addConfiguration(Map<T, Q> configMap, Object key, Object value);

    /**
     * reads configuration from config file, saves it to Map parametrised with <T, Q> classes
     * @param configMap Container for config file data read & storage
     * @return int errorValue - (if != 0) error occurred
     */
    @Override
    public int readConfiguration(Map<T, Q> configMap) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if ((line = line.replaceAll("\\s", "")).isEmpty())
                    continue;
                String[] set = line.split(DELIMITER);
                if (set.length != 2) {
                    Log.logReport("Invalid configuration syntax in file: " + fileName);
                    return -1;
                }
                addConfiguration(configMap,  set[0], set[1]);
            }
        } catch (IOException e) {
            Log.logReport("Error while reading config file: " + fileName);
            return -1;
        }
        return 0;
    }
}
