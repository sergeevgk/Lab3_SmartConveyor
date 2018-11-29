package config;

import log.Log;

import java.util.Map;

public class ConfigInterpreterWorkerParameters extends ConfigInterpreterBase<GrammarWorker, String> {
    public ConfigInterpreterWorkerParameters(String fileName) {
        super(fileName);
    }
    /**
     * @param configMap - container for putting in (key, value) pair
     * @param key       string containing GrammarWorker key value
     * @param value     puts (key, value) pair in configMap
     *                  realisation for conveyor.Executor(worker) configuration file
     */
    @Override
    protected int addConfiguration(Map<GrammarWorker, String> configMap, Object key, Object value) {
        try {
            GrammarWorker g = GrammarWorker.valueOf((String)key);
            configMap.put(g, (String) value);
        } catch (IllegalArgumentException e) {
            Log.logReport("Unexpected key in options config file: " + fileName);
            return -1;
        }
        return 0;
    }
}
