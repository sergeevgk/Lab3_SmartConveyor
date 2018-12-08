package huffman;

import java.util.*;
import config.GrammarWorker;
import log.Log;
//TODO GrammarWorker ENCODE

public class HuffmanAlgorithm {
    private Map<GrammarWorker, String> configWorker;
    private Map<Byte, String> huffmanTable;
    private final String ENCODE = "ENCODE";
    private final String DECODE = "DECODE";
    public HuffmanAlgorithm(Map<GrammarWorker, String> configWorker, Map<Byte, String> huffmanTable) {
        this.configWorker = configWorker;
        this.huffmanTable = huffmanTable;
    }

    /**
     * @param source input array of bytes (byte[]), contains symbols for encoding/decoding
     * @return HuffmanAlgorithmResult class contains byte[] result - array of encoded/decoded bytes,
     * Map<Byte, String> huffmanTable and byte[] extraSymbols - symbols which were not decoded/encoded
     * <p>
     * Method starts process of encoding/decoding input array values using huffman table
     */
    public HuffmanAlgorithmResult startProcess(byte[] source, int symbolNum) {
        String codeMode = configWorker.get(GrammarWorker.CODE_MODE);
        return processCoder(source, codeMode, symbolNum);
    }

    /**
     * @param s    sequence of bytes to encode/decode
     * @param mode flag to choose between encoding and decoding
     * @return HuffmanAlgorithmResult class contains byte[] result - array of encoded/decoded bytes,
     * Map<Byte, String> huffmanTable and byte[] extraSymbols - symbols which were not decoded/encoded
     * <p>
     * Method decides whether to encode or decode input sequence
     */
    private HuffmanAlgorithmResult processCoder(byte[] s, String mode, int symbolNum) {
        if (mode.equals(ENCODE)) {
            return encode(s);
        } else if (mode.equals(DECODE)) {
            return decode(s, symbolNum);
        }
        return null;
    }

    /**
     * @param source byte sequence to encode
     * @return HuffmanAlgorithmResult with empty extraSymbols field - algorithm encodes all given values in sequence
     */
    private HuffmanAlgorithmResult encode(byte[] source) {
        DataConverterImpl conv = new DataConverterImpl();
        byte[] extra = {};
        byte[] tempRes = toHuffman(source, huffmanTable).getBytes();
        byte[] res = conv.convertBitArrayToBytes(tempRes);
        return new HuffmanAlgorithmResult(res, extra, tempRes.length);
    }

    /**
     * @param source       byte sequence to encode
     * @param huffmanTable map of pairs (byte - string equals to byte'setConsumer new representation as bit sequence)
     * @return string representation of bit sequence matching with byte value
     * <p>
     * Method encodes byte sequence into sequence of "0" and "1" equals to string representation of bit sequence
     */
    private String toHuffman(byte[] source, Map<Byte, String> huffmanTable) {
        StringBuilder s = new StringBuilder();
        for (byte c : source) {
            s.append(huffmanTable.get(c));
        }
        //System.out.println(setConsumer);
        return s.toString();
    }

    /**
     * @param source byte sequence to decode, need to be converted to bit sequence
     * @return HuffmanAlgorithmResult class contains byte[] result - array of encoded/decoded bytes,
     * Map<Byte, String> huffmanTable and byte[] extraSymbols - symbols which were not decoded/encoded
     * <p>
     * Method decodes encoded sequence, represented by byte array
     */
    private HuffmanAlgorithmResult decode(byte[] source, int symbolNum) {
        Map<String, Byte> codeToCharMap = new HashMap<>();
        String temp;
        int maxBufferSize = Integer.parseInt(configWorker.get(GrammarWorker.BUFFER_SIZE));
        ArrayList<Byte> exSym = new ArrayList<>();
        ArrayList<Byte> decSym = new ArrayList<>();
        StringBuilder tempString = new StringBuilder();
        int maxCodeLength = 1;
        for (byte key : huffmanTable.keySet()) {
            codeToCharMap.put(temp = huffmanTable.get(key), key);
            if (maxCodeLength < temp.length())
                maxCodeLength = temp.length();
        }
        int currentIndex = 0;
        for (byte ch : source) {
            tempString.append((char) ch);
            currentIndex += 1;
            if (codeToCharMap.containsKey(tempString.toString())) {
                if (!decSym.add(codeToCharMap.get(tempString.toString()))) {
                    Log.logReport("Can't resolve symbol to encode.\n");
                    return null;
                }
                tempString = new StringBuilder();
                if (source.length == maxBufferSize && source.length - currentIndex < maxCodeLength && source.length - currentIndex > 0) {
                    while (source.length - currentIndex < maxCodeLength && source.length - currentIndex > 0) {
                        if (!exSym.add(source[currentIndex])) {
                            Log.logReport("Can't resolve symbol to encode.\n");
                            return null;
                        }
                        currentIndex += 1;
                    }
                    break;
                }
            }
        }
        byte[] extraSymbols = convertToPrimitive(exSym);
        byte[] decodedSymbols = convertToPrimitive(decSym);

        return new HuffmanAlgorithmResult(decodedSymbols, extraSymbols, source.length);
    }

    /**
     * @param list ArrayList of Bytes
     * @return byte[] array - converted ArrayList(Byte[])
     * <p>
     * Method converts Byte ArrayList to byte[] array
     */
    private byte[] convertToPrimitive(ArrayList<Byte> list) {
        byte[] array = new byte[list.size()];
        Byte[] Array = list.toArray(new Byte[list.size()]);
        int index = 0;
        for (Byte B : Array) {
            array[index] = Array[index];
            index += 1;
        }
        return array;
    }
}

