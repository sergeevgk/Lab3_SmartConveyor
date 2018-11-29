package conveyor;

import config.*;
import conveyor.Executor;
import conveyor.ExecutorImpl;
import huffman.HuffmanTableReader;
import huffman.HuffmanTableWriter;
import log.Log;

import java.io.*;
import java.util.*;

public class Manager {
    private Map<GrammarManager, String> configManager;
    private BufferedInputStream inputStream;
    private BufferedOutputStream outputStream;
    private ArrayList<Executor> workers;

    public Manager(String fileName) {
        ConfigInterpreter config = new ConfigInterpreterManager(fileName);
        configManager = new EnumMap<>(GrammarManager.class);
        this.workers = new ArrayList<>();
        if (config.readConfiguration(configManager) == -1) {
            Log.logReport("Can't create manager");
        }
        HuffmanTableWriter.getInstance().init(configManager.get(GrammarManager.HUFFMAN_TABLE));
        HuffmanTableReader.getInstance().init(configManager.get(GrammarManager.HUFFMAN_TABLE));
    }

    /**
     * @return 0 if streams opened successfully, -1 otherwise
     * <p>
     * Method open streams for input and output data for workers
     */
    public int openStreams() {
        try {
            FileInputStream inStream = new FileInputStream(configManager.get(GrammarManager.IN));
            this.inputStream = new BufferedInputStream(inStream);
            FileOutputStream outStream = new FileOutputStream(configManager.get(GrammarManager.OUT));
            this.outputStream = new BufferedOutputStream(outStream);
        } catch (NullPointerException e) {
            Log.logReport("Exception thrown during stream open process.\n");
            return -1;
        } catch (IOException e) {
            Log.logReport("Exception thrown during stream open process.\n");
            return -1;
        }
        return 0;
    }

    /**
     * @return 0 if workers created successfully, -1 otherwise
     * <p>
     * Method creates workers and puts them to ArrayList()
     */
    public int createWorkers() {
        Map<String, String> configWorkerListFileNames = new HashMap<>();
        ConfigInterpreterWorkers configWorkerListReader;
        configWorkerListReader = new ConfigInterpreterWorkers(configManager.get(GrammarManager.WORKER_LIST));
        configWorkerListReader.readConfiguration(configWorkerListFileNames);
        for (String workerFileName : configWorkerListFileNames.keySet()) {
            Executor newWorker = new ExecutorImpl(configWorkerListFileNames.get(workerFileName), configManager.get(GrammarManager.IN));
            this.workers.add(newWorker);
        }
        return 0;
    }
//TODO remake for adapters

    /**
     * manager introduces workers with each other according to their link list
     *
     * @return int errorValue - (if != 0) error occurred
     */
    public int introduceWorkers() {
        int i;
        //conveyor.Executor provider, consumer;
        ArrayList<Integer> consumersList;
        Map<Integer, ArrayList<Integer>> schedule = new HashMap<>();
        new ConfigInterpreterWorkerSchedule(configManager.get(GrammarManager.WORKERS_SCHEDULE)).readConfiguration(schedule);
        for (i = 0; i < workers.size(); i += 1) {
            if ((consumersList = schedule.get(i + 1)) == null) {
                Log.logReport("Invalid worker introduce.\n");
                return -1;
            }
            if (consumersList.size() > 0) {
                for (int j : consumersList) {
                    if (workers.get(i) == null) {
                        Log.logReport("Invalid worker in schedule.\n");
                        return -1;
                    }
                    if (workers.get(i) == this) {
                        Log.logReport("Invalid worker in schedule.\n");
                        return -1;
                    }
                    workers.get(i).addSubscriber(new AdapterImpl(workers.get(j - 1)));
                    //workers.get(i).SetConsumer(workers.get(j - 1));
                }
            }
        }
        return 0;
    }

    /**
     * @return 0 if conveyor worked without errors, -1 otherwise
     * <p>
     * Starts encode-decode conveyor with at least two workers
     */
    public int StartConveyor() {
        Executor first = workers.get(Integer.parseInt(configManager.get(GrammarManager.START)) - 1);
        first.SetInput(inputStream);
        Executor last = workers.get(Integer.parseInt(configManager.get(GrammarManager.END)) - 1);
        last.SetOutput(outputStream);
        first.run();
        return 0;
    }

    /**
     * @return 0 if streams closed successfully, -1 otherwise
     * <p>
     * Method closes streams in manager
     */
    public int closeStreams() {
        try {
            inputStream.close();
            outputStream.close();
        } catch (IOException e) {
            Log.logReport("Exception thrown during stream close process.\n");
            return -1;
        }
        return 0;
    }
}

