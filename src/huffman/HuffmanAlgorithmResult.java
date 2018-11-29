package huffman;

import java.util.Map;

public class HuffmanAlgorithmResult {
    private byte[] result;
    private Map<Byte, String> huffmanTable;
    private byte[] extraSymbols;
    private int uncodedLength;

    public HuffmanAlgorithmResult(byte[] result, Map<Byte, String> huffmanTree, byte[] extraSymbols, int length) {
        this.result = result;
        this.huffmanTable = huffmanTree;
        this.extraSymbols = extraSymbols;
        this.uncodedLength = length;
    }

    public Map<Byte, String> getHuffmanTable() {
        return huffmanTable;
    }

    public byte[] getResult() {
        return result;
    }

    public byte[] getExtra() {
        return extraSymbols;
    }

    public int getUncodedLength(){ return uncodedLength;}
}
