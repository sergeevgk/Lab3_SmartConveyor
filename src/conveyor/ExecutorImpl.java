package conveyor;

import config.ConfigInterpreterWorkerParameters;
import config.GrammarWorker;
import huffman.*;
import log.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class ExecutorImpl implements Executor {
    public Map<GrammarWorker, String> configWorker;
    private String inputFileName;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;
    private Map<Byte, String> huffmanTable;

    private Object dataStorage;
    private int dataStoragePos;

    private Map<Executor, APPROPRIATE_TYPES> providers;
    private Map<Executor, Integer[]> consumerList;

    private Object tempAdapter;
    private Map<Executor, Object> adapters;
    public APPROPRIATE_TYPES currentType;
    private ArrayList<APPROPRIATE_TYPES> availableTypes;

    ExecutorImpl(String fileName, String inputFileName) {
        this.inputFileName = inputFileName;
        configWorker = new EnumMap<>(GrammarWorker.class);
        consumerList = new HashMap<>();
        this.availableTypes = new ArrayList<>();
        this.availableTypes.add(APPROPRIATE_TYPES.DOUBLE);
        this.availableTypes.add(APPROPRIATE_TYPES.BYTE);
        this.availableTypes.add(APPROPRIATE_TYPES.CHAR);
        adapters = new HashMap<>();
    }

    public int setConfig(String config) {
        ConfigInterpreterWorkerParameters workerInterp = new ConfigInterpreterWorkerParameters(config);
        if (workerInterp.readConfiguration(configWorker) != 0) {
            Log.logReport("Error occurred during worker ctor process.");
            return -1;
        }
        return 0;
    }

    public void setInput(DataInputStream inputStream) {
        this.inputStream = inputStream;
    }

    public void setOutput(DataOutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public APPROPRIATE_TYPES[] getConsumedTypes() {
        return ((APPROPRIATE_TYPES[]) this.availableTypes.toArray());
    }

    public int setConsumer(Executor consumer) {
        this.consumerList.put(consumer, new Integer[2]);
        for (APPROPRIATE_TYPES type : consumer.getConsumedTypes()) {
            for (APPROPRIATE_TYPES thisType : this.availableTypes) {
                if (type == thisType) {
                    this.currentType = type;
                    if (currentType == APPROPRIATE_TYPES.BYTE) {
                        //adapters.put(this, new ByteTransfer());
                        this.tempAdapter = new ByteTransfer();
                    } else if (currentType == APPROPRIATE_TYPES.DOUBLE) {
                        this.tempAdapter = new DoubleTransfer();
                        //adapters.put(this, new DoubleTransfer());
                    } else if (currentType == APPROPRIATE_TYPES.CHAR) {
                        this.tempAdapter = new CharTransfer();
                        //adapters.put(this, new CharTransfer());
                    } else {
                        this.tempAdapter = null;
                        //adapters.put(this, null);
                    }
                }
                consumer.setAdapter(this, tempAdapter, currentType);
                return 0;
            }
        }

        return -1;
    }

    public void setAdapter(Executor provider, Object adapter, APPROPRIATE_TYPES type) {
        this.adapters.put(provider, adapter);
        this.providers.put(provider, type);
        this.currentType = type;
        int startPos = Integer.parseInt(this.configWorker.get(GrammarWorker.START_POS));
        int length = Integer.parseInt(this.configWorker.get(GrammarWorker.REQUESTED_LENGTH));
        provider.subscribe(this, startPos, length);
    }

    public void subscribe(Executor consumer, int startPos, int length){
        Integer[] pair = new Integer[2];
        pair[0] = startPos;
        pair[1] = length;
        this.consumerList.put(consumer, pair);
    }

    private void buildHuffmanTable() {
        Map<Byte, String> huffmanTable;
        HuffmanTableBuilder huffmanTableBuilder = new HuffmanTableBuilder(this.inputFileName);
        if ((huffmanTable = huffmanTableBuilder.buildHuffmanTable()) == null) {
            return;
        }
        HuffmanTableWriter.getInstance().writeHuffmanTable(huffmanTable);
        this.huffmanTable = huffmanTable;
    }

    private void readHuffmanTable() {
        this.huffmanTable = new HashMap<>();
        HuffmanTableReader.getInstance().readHuffmanTable(this.huffmanTable);
    }

    public int run() {
        HuffmanAlgorithm algorithm;
        if (this.inputStream != null && this.outputStream != null) {
            buildHuffmanTable();
            algorithm = new HuffmanAlgorithm(configWorker, huffmanTable);
            processFromToFile(algorithm);
        } else if (this.inputStream != null) {
            buildHuffmanTable();
            algorithm = new HuffmanAlgorithm(configWorker, huffmanTable);
            processFromFile(algorithm);
        } else if (this.outputStream != null) {
            readHuffmanTable();
            algorithm = new HuffmanAlgorithm(configWorker, huffmanTable);
            processToFile(algorithm);
        } else {
            readHuffmanTable();
            algorithm = new HuffmanAlgorithm(configWorker, huffmanTable);
            processProviderConsumer(algorithm);
        }
        return 0;
    }

    private int processFromToFile(HuffmanAlgorithm algorithm) {
        MyFileReader fileReader = new MyFileReader(configWorker, inputStream);
        MyFileWriter fileWriter = new MyFileWriter(configWorker, outputStream);
        HuffmanAlgorithmResult res = null;
        byte[] buffer;
        while ((buffer = fileReader.readInputFile()) != null) {
            res = algorithm.startProcess(buffer, 0);
            if (res == null) {
                return -1;
            }
            fileWriter.writeOutputFile(res);
        }
        return 0;
    }

    private int processFromFile(HuffmanAlgorithm algorithm) {
        MyFileReader fileReader = new MyFileReader(configWorker, inputStream);
        byte[] buffer;
        HuffmanAlgorithmResult res;
        while ((buffer = fileReader.readInputFile()) != null) {
            res = algorithm.startProcess(buffer, 0);
            if (res == null) {
                return -1;
            }
//            consumer.put(res);
//            consumer.run();
            for (Executor sub : consumerList.keySet()) {
                //notify; each subscriber gets his *configOptions* part of data
            }

        }
        return 0;
    }

    private int processToFile(HuffmanAlgorithm algorithm) {//просто напечатать
        int bufferSize = Integer.parseInt(configWorker.get(GrammarWorker.BUFFER_SIZE));
        //byte[] bitInput = DataConverterImpl.getInstance().convertByteToBitArray(result.getResult(), result.getUncodedLength());
        int extraLength = 0;
        byte[] byteInput = DataConverterImpl.getInstance().c(this.dataStorage);
        MyFileWriter fileWriter = new MyFileWriter(configWorker, outputStream);
        for (int i = 0; i < .length;
        i += (bufferSize - extraLength)){//pos in result
            int size = bufferSize < bitInput.length - i ? bufferSize : bitInput.length - i;
            byte[] source = new byte[size];
            System.arraycopy(bitInput, i, source, 0, size);
            HuffmanAlgorithmResult res = algorithm.startProcess(source, Integer.parseInt(configWorker.get(GrammarWorker.REQUESTED_LENGTH)));
            fileWriter.writeOutputFile(res);
            extraLength = res.getExtra().length;
        }
        return 0;
    }

    private int processProviderConsumer(HuffmanAlgorithm algorithm) {
        int bufferSize = Integer.parseInt(configWorker.get(GrammarWorker.BUFFER_SIZE));
        int length;
        byte[] source = result.getResult();
        for (int i = 0; i < source.length; i += (bufferSize - length)) {//pos in result
            int size = bufferSize < source.length - i ? bufferSize : source.length - i;
            byte[] buffer = new byte[size];
            System.arraycopy(source, i, buffer, 0, size);
            HuffmanAlgorithmResult result = algorithm.startProcess(buffer, Integer.parseInt(configWorker.get(GrammarWorker.REQUESTED_LENGTH)));
//            consumer.put(result.getResult());
//            consumer.run();
            for (Executor sub : consumerList) {
                //each adapter gets his *configOptions* part of data
            }
            length = result.getExtra().length;
        }
        return 0;
    }

    public int put(Executor provider) {//this.result = this;
        int counter;
        Object adapter = this.adapters.get(provider);
        switch (currentType) {
            case BYTE:
                for (counter = 0; counter < ((Byte[]) this.dataStorage).length; counter += 1) {
                    ((Byte[]) this.dataStorage)[counter] = ((ByteTransfer) adapter).getNextByte();
                }
                //?? cast to byte[] ??
                break;
            case DOUBLE:
                for (counter = 0; counter < ((Double[]) this.dataStorage).length; counter += 1) {
                    ((Double[]) this.dataStorage)[counter] = ((DoubleTransfer) adapter).getNextDouble();
                }
                //?? cast to byte[] ??
                break;
            case CHAR:
                for (counter = 0; counter < ((Character[]) this.dataStorage).length; counter += 1) {
                    ((Character[]) this.dataStorage)[counter] = ((CharTransfer) adapter).getNextChar();
                }
                //?? cast to byte[] ??
                break;
            default:
                return -1; //error
                //Log.logReport();
        }
        return 0;
    }

    class ByteTransfer implements InterfaceByteTransfer {
        public Byte getNextByte() {
            //ExecutorImpl provider = (ExecutorImpl) ExecutorImpl.this.provider;
            assert (ExecutorImpl.this.dataStoragePos < ((Byte[]) ExecutorImpl.this.dataStorage).length);
            return ((Byte[]) ExecutorImpl.this.dataStorage)[ExecutorImpl.this.dataStoragePos++];
        }
    }

    class DoubleTransfer implements InterfaceDoubleTransfer {
        public Double getNextDouble() {
            assert (ExecutorImpl.this.dataStoragePos < ((Double[]) ExecutorImpl.this.dataStorage).length);
            return ((Double[]) ExecutorImpl.this.dataStorage)[ExecutorImpl.this.dataStoragePos++];
        }
    }

    class CharTransfer implements InterfaceCharTransfer {
        public Character getNextChar() {
            assert (ExecutorImpl.this.dataStoragePos < ((Character[]) ExecutorImpl.this.dataStorage).length);
            return ((Character[]) ExecutorImpl.this.dataStorage)[ExecutorImpl.this.dataStoragePos++];
        }
    }
}
