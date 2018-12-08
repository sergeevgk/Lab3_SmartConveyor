package conveyor;

import config.ConfigInterpreterWorkerParameters;
import config.GrammarWorker;
import huffman.*;
import log.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.*;

public class ExecutorImpl implements Executor {
    public Map<GrammarWorker, String> configWorker;
    private String inputFileName;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;
    private Map<Byte, String> huffmanTable;

    private Object dataStorageTemp;//?
    private Object dataStorage;
    private int dataStoragePos;

    private Map<Executor, APPROPRIATE_TYPES> providers;//?
    private Map<Executor, Integer[]> consumerMap;

    private Object tempAdapter;
    private Map<Executor, Object> adapters;
    public APPROPRIATE_TYPES currentType;
    private APPROPRIATE_TYPES[] availableTypes;


    ExecutorImpl(String inputFileName) {
        this.inputFileName = inputFileName;
        configWorker = new EnumMap<>(GrammarWorker.class);
        consumerMap = new HashMap<>();
        this.availableTypes = new APPROPRIATE_TYPES[3];
        adapters = new HashMap<>();
    }

    public int setConfig(String config) {
        ConfigInterpreterWorkerParameters workerInterp = new ConfigInterpreterWorkerParameters(config);
        if (workerInterp.readConfiguration(configWorker) != 0) {
            Log.logReport("Error occurred during worker ctor process.");
            return -1;
        }
        //TODO set available types
        return 0;
    }

    public void setInput(DataInputStream inputStream) {
        this.inputStream = inputStream;
    }

    public void setOutput(DataOutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public APPROPRIATE_TYPES[] getConsumedTypes() {
        return this.availableTypes;
    }

    public int setConsumer(Executor consumer) {
        this.consumerMap.put(consumer, new Integer[2]);
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
                    consumer.setAdapter(this, tempAdapter, currentType);
                    return 0;
                }
            }
        }

        return -1;
    }

    public void setAdapter(Executor provider, Object adapter, APPROPRIATE_TYPES type) {
        this.adapters.put(provider, adapter);
        //this.providers.put(provider, type);
        this.currentType = type;
        int startPos = Integer.parseInt(this.configWorker.get(GrammarWorker.START_POS));
        int length = Integer.parseInt(this.configWorker.get(GrammarWorker.REQUESTED_LENGTH));
        provider.subscribe(this, startPos, length);
    }

    public void subscribe(Executor consumer, int startPos, int length) {
        Integer[] pair = new Integer[2];
        pair[0] = startPos;
        pair[1] = length;
        this.consumerMap.put(consumer, pair);
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
        int bufferSize = Integer.parseInt(configWorker.get(GrammarWorker.BUFFER_SIZE));
        byte[] buffer;
        while ((buffer = fileReader.readInputFile(bufferSize)) != null) {
            res = algorithm.startProcess(buffer, 0);
            if (res == null) {
                return -1;
            }
            fileWriter.writeOutputFile(res.getResult());
        }
        return 0;
    }

    private Comparator<Executor> getCompByStartPos() {
        return new Comparator<Executor>() {
            @Override
            public int compare(Executor s1, Executor s2) {
                return (ExecutorImpl.this.consumerMap.get(s1)[0] - ExecutorImpl.this.consumerMap.get(s2)[0]);
            }
        };
    }

    //completed
    private int processFromFile(HuffmanAlgorithm algorithm) {
        MyFileReader fileReader = new MyFileReader(configWorker, inputStream);
        byte[] buffer;
        HuffmanAlgorithmResult res;
        DataConverter converter = new DataConverterImpl();
        ArrayList<Executor> consumerList = new ArrayList<>(consumerMap.keySet());
        consumerList.sort(getCompByStartPos());
        for (Executor sub : consumerList) {
            int bufferSize = consumerMap.get(sub)[1];

            while ((buffer = fileReader.readInputFile(bufferSize)) != null) {
                res = algorithm.startProcess(buffer, 0);
                if (res == null) {
                    return -1;
                }
                dataStorage = res.getResult();//byte[]
                dataStorage = converter.wrapArray((byte[]) dataStorage);
                sub.put(this);
                sub.run();
            }
        }
        return 0;
    }

    private int processToFile(HuffmanAlgorithm algorithm) {//просто напечатать
        int bufferSize = Integer.parseInt(configWorker.get(GrammarWorker.BUFFER_SIZE));
        MyFileWriter fileWriter = new MyFileWriter(configWorker, outputStream);
        for (int writtenLen = 0; writtenLen < ((byte[]) this.dataStorage).length; writtenLen += bufferSize) {
            byte[] output = new byte[bufferSize];
            System.arraycopy((byte[]) this.dataStorage, writtenLen, output, 0, bufferSize);
            fileWriter.writeOutputFile(output);
        }
        //fileWriter.writeOutputFile((byte[])this.dataStorage);
        return 0;
    }

    private int processProviderConsumer(HuffmanAlgorithm algorithm) {
        int length = 0;
        int selfBufferSize = Integer.parseInt(configWorker.get(GrammarWorker.BUFFER_SIZE));
        ArrayList<Executor> consumerList = new ArrayList<>(consumerMap.keySet());
        consumerList.sort(getCompByStartPos());
        DataConverter converter = new DataConverterImpl();
        for (Executor sub : consumerList) {
            int bufferSize = consumerMap.get(sub)[1];
            int startPos = consumerMap.get(sub)[0];
            for (int i = startPos; i < bufferSize; i += (selfBufferSize - length)) {//pos in result
                int size = bufferSize < selfBufferSize ? bufferSize : selfBufferSize;
                byte[] buffer = new byte[size];
                System.arraycopy((byte[]) dataStorage, i, buffer, 0, size);
                HuffmanAlgorithmResult result = algorithm.startProcess(buffer, Integer.parseInt(configWorker.get(GrammarWorker.REQUESTED_LENGTH)));
                length = result.getExtra().length;
                dataStorage = result.getResult();
                dataStorage = converter.wrapArray((byte[]) dataStorage);
                sub.put(this);
                sub.run();
            }
        }
        return 0;
    }

    public int put(Executor provider) {//this.result = this;
        int counter;
        Object adapter = this.adapters.get(provider);
        DataConverter converter = new DataConverterImpl();
        switch (currentType) {
            case BYTE:
                ArrayList<Byte> byteArrayList = new ArrayList<>();
                for (counter = 0; counter < ((Byte[]) this.dataStorage).length; counter += 1) {
                    byteArrayList.add(((ByteTransfer) adapter).getNextByte());
                }
                dataStorage = converter.convertToPrimitive(byteArrayList);
                break;
            case DOUBLE:
                for (counter = 0; counter < ((Double[]) this.dataStorage).length; counter += 1) {
                    ((Double[]) this.dataStorage)[counter] = ((DoubleTransfer) adapter).getNextDouble();
                }
                this.dataStorage = converter.convertDoubleToByte((Double[]) dataStorage);
                dataStorage = converter.convertToPrimitiveArray((Byte[]) dataStorage);
                break;
            case CHAR:
                for (counter = 0; counter < ((Character[]) this.dataStorage).length; counter += 1) {
                    ((Character[]) this.dataStorage)[counter] = ((CharTransfer) adapter).getNextChar();
                }
                this.dataStorage = converter.convertCharToByte((Character[]) dataStorage);
                dataStorage = converter.convertToPrimitiveArray((Byte[]) dataStorage);
                break;
            default:
                return -1; //error
            //Log.logReport();
        }
        return 0;
    }

    class ByteTransfer implements InterfaceByteTransfer {
        public Byte getNextByte() {
            assert (dataStoragePos < ((Byte[]) dataStorage).length);
            return ((Byte[]) dataStorage)[dataStoragePos++];
        }
    }

    class DoubleTransfer implements InterfaceDoubleTransfer {
        public Double getNextDouble() {
            assert (dataStoragePos < ((Double[]) dataStorage).length);
            return ((Double[]) dataStorage)[dataStoragePos++];
        }
    }

    class CharTransfer implements InterfaceCharTransfer {
        public Character getNextChar() {
            assert (dataStoragePos < ((Character[]) dataStorage).length);
            return ((Character[]) dataStorage)[dataStoragePos++];
        }
    }
}
