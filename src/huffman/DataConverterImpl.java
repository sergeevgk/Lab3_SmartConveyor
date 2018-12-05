package huffman;

import java.util.ArrayList;

public class DataConverterImpl implements DataConverter{
    private static DataConverterImpl ourInstance = new DataConverterImpl();

    public static DataConverterImpl getInstance() {
        return ourInstance;
    }

    private DataConverterImpl() {
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
            if (len < 8){
                byte[] lastByte = {0,0,0,0,0,0,0,0};
                System.arraycopy(temp, 0, lastByte, 0, len);
                for (byte c : lastByte) {
                    value += Math.pow(2, k) * (c - 48);
                    k += 1;
                }
                byteArray.add((byte) value);
                break;
            }
            for (byte c : temp) {
                value += Math.pow(2, k) * (c - 48);
                k += 1;
            }
            byteArray.add((byte) value);
            i += 8;
        }
        return convertToPrimitive(byteArray);

    }

    public byte[] convertIntArrayToBytes(Integer[] source){
        ArrayList<Byte> byteArray = new ArrayList<>();
        //body
        return convertToPrimitive(byteArray);
    }

    public byte[] convertStringToBytes(String source){
        ArrayList<Byte> byteArray = new ArrayList<>();
        //body
        return convertToPrimitive(byteArray);
    }
    public byte[] convertCharArrayToBytes(Character[] source){
        ArrayList<Byte> byteArray = new ArrayList<>();
        //body
        return convertToPrimitive(byteArray);
    }
    public byte[] convertByteArrayToIntegerArray(Byte[] source){
        ArrayList<Integer> intArray = new ArrayList<>();
        //body
        return null;
    }

}
