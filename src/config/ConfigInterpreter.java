package config;

import java.util.Map;

/**
 * ConfigInterpreter
 *
 * @param <T> key class parameter of configMap
 * @param <Q> value class parameter of configMap
 *            Interface for classes ConfigInterpreter
 */

public interface ConfigInterpreter<T, Q> {
    /**
     * readConfiguration
     *
     * @param configMap Container for config file data read & storage
     *                  Reads data from config files and writes to configMap
     */
    int readConfiguration(Map<T, Q> configMap);
}