package huffman;

import java.util.ArrayList;

public class BitByteConverter {
    private static huffman.BitByteConverter ourInstance = new huffman.BitByteConverter();

    public static huffman.BitByteConverter getInstance() {
        return ourInstance;
    }

    private BitByteConverter() {
    }

    public byte[] convertToPrimitive(ArrayList<Byte> list) {
        byte[] array = new byte[list.size()];
        Byte[] Array = list.toArray(new Byte[list.size()]);
        int index = 0;
        for (Byte B : Array) {
            array[index] = Array[index];
            index += 1;
        }
        return array;
    }

    /**
     * @return
     */
    public byte[] convertBitArrayToBytes(byte[] source) {
        ArrayList<Byte> byteArray = new ArrayList<>();
        int i = 0;
        int value;
        while (i < source.length) {
            int len = 8 < source.length - i ? 8 : source.length - i;
            byte[] temp = new byte[len];
            System.arraycopy(source, i, temp, 0, len);
            value = 0;
            int k = 0;
            for (byte c : temp) {
                value += Math.pow(2, k) * (c - 48);
                k += 1;
            }
            byteArray.add((byte) value);
            i += 8;
        }
        return convertToPrimitive(byteArray);

    }

    public byte[] convertByteToBitArray(byte[] source, int bitLength) {
        ArrayList<Byte> bitArray = new ArrayList<>();
        int tempLength = bitLength;
        byte[] bits = {1, 2, 4, 8, 16, 32, 64, -128};
        for (byte b : source) {
            for (byte bit: bits){
                if (tempLength == 0)
                    break;
                if ((b & bit) == bit){
                    bitArray.add((byte) (49));
                } else {
                    bitArray.add((byte) (48));
                }
                tempLength -= 1;
            }
            /*System.arraycopy(temp, 0, array, 0, array.length);
            for (i = array.length - 1; i >= 0; i -= 1) {
                byteArray.add((byte) (array[i] + 48));
            }
            for (i = 0; i < temp.length; i += 1) {
                bitArray.add((byte) (temp[i] + 48));
            }*/
        }
        return convertToPrimitive(bitArray);
    }
}
