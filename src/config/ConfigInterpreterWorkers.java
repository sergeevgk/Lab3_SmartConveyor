package config;

import log.Log;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

public class ConfigInterpreterWorkers extends ConfigInterpreterBase<String, String> {
    public ConfigInterpreterWorkers(String fileName) {
        super(fileName);
    }

    /**
     * @param configMap - container for putting in (key, value) pair
     * @param key       string containing key value
     * @param value     puts (key, value) pair in configMap
     *                  realisation for conveyor.Executor(worker) configuration file
     */
    @Override
    protected int addConfiguration(Map<String, String> configMap, Object key, Object value) {
        try {
            configMap.put((String) key, (String) value);
        } catch (IllegalArgumentException e) {
            Log.logReport("Unexpected key in options config file: " + fileName);
            return -1;
        }
        return 0;
    }
}

