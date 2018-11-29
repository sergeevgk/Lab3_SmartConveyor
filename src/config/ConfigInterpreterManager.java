package config;

import log.Log;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

public class ConfigInterpreterManager extends ConfigInterpreterBase<GrammarManager, String> {
    public ConfigInterpreterManager(String fileName) {
        super(fileName);
    }

    /**
     * @param configMap - container for putting in (key, value) pair
     * @param key       string containing GrammarManager enum value
     * @param value     puts (key, value) pair in configMap
     *                  realisation for conveyor.Manager configuration file
     */
    @Override
    protected int addConfiguration(Map<GrammarManager, String> configMap, Object key, Object value) {
        try {
            GrammarManager g = GrammarManager.valueOf((String)key);
            configMap.put(g, (String)value);
        } catch (IllegalArgumentException e) {
            Log.logReport("Unexpected key in conveyor.Manager config file: " + fileName);
            return -1;
        }
        return 0;
    }
}
