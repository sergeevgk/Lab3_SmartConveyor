package config;

import log.Log;
import sun.awt.image.IntegerInterleavedRaster;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Map;

public class ConfigInterpreterWorkerSchedule extends ConfigInterpreterBase<Integer, ArrayList<Integer>> {
    private final String DELIMITER_SCHEDULE = ",";
    private final String DELIMITER = ":";

    public ConfigInterpreterWorkerSchedule(String fileName) {
        super(fileName);
    }

    @Override
    protected int addConfiguration(Map<Integer, ArrayList<Integer>> configMap, Object key, Object value) {
        try {
            configMap.put((Integer) key, (ArrayList<Integer>) value);
        } catch (IllegalArgumentException e) {
            Log.logReport("Unexpected key in conveyor.Manager config file: " + fileName);
            return -1;
        }
        return 0;
    }

    @Override
    public int readConfiguration(Map<Integer, ArrayList<Integer>> configMap) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if ((line = line.replaceAll("\\s", "")).isEmpty())
                    continue;
                String[] set = line.split(DELIMITER);
                /*if (set.length != 2) {
                    Log.logReport("Invalid configuration syntax in file: " + fileName);
                    return -1;
                }*/
                ArrayList<Integer> workerLinks = new ArrayList<>();
                if (set.length > 1) {
                    String[] options = set[1].split(DELIMITER_SCHEDULE);
                    for (String link : options) {
                        workerLinks.add(Integer.parseInt(link));
                    }
                }
                addConfiguration(configMap, Integer.parseInt(set[0]), workerLinks);
            }
        } catch (IOException e) {
            Log.logReport("Error while reading config file: " + fileName);
            return -1;
        }
        return 0;
    }
}
