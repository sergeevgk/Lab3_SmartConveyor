package huffman;

import java.nio.ByteBuffer;
import java.util.ArrayList;

public class DataConverterImpl implements DataConverter {
    //private static DataConverterImpl ourInstance = new DataConverterImpl();

 /*   public static DataConverterImpl getInstance() {
        return ourInstance;
    }*/

    public  DataConverterImpl() {
    }

    public Byte[] wrapArray(byte[] source){
        int i = 0;
        Byte[] out = new Byte[source.length];
        for (byte b: source)
            out[i++] = b;
        return out;
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

    public byte[] convertToPrimitiveArray(Byte[] a) {
        byte[] array = new byte[a.length];
        int index = 0;
        for (Byte B : a) {
            array[index] = B;
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
            if (len < 8) {
                byte[] lastByte = {0, 0, 0, 0, 0, 0, 0, 0};
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

    public Byte[] convertIntegerToByte(Integer[] source) {
        ArrayList<Byte> byteArray = new ArrayList<>();
        for (Integer it : source) {
            Integer i = it;
            for (int j = 0; j < Integer.BYTES; j += 1) {
                byte b = ((Integer) (i % 256)).byteValue();
                i /= 256;
                byteArray.add(b);
            }
        }
        return (Byte[]) byteArray.toArray();
    }

    public Byte[] convertDoubleToByte(Double[] source) {
        ArrayList<Byte> byteArray = new ArrayList<>();
        //body
        for (Double it: source){
            ByteBuffer byteBuffer = ByteBuffer.allocate(Double.BYTES);
            byteBuffer.putDouble(it);
            for (byte b: byteBuffer.array()){
                byteArray.add(b);
            }
        }
        return (Byte[]) byteArray.toArray();
    }

    public Byte[] convertCharToByte(Character[] source) {
        ArrayList<Byte> byteArray = new ArrayList<>();
        //body
        for (Character it : source) {
            Character i = it;
            for (int j = 0; j < Character.BYTES; j += 1) {
                byte b = ((Integer) (i % 256)).byteValue();
                i = (char)(i / 256);
                byteArray.add(b);
            }
        }
        return (Byte[]) byteArray.toArray();
    }

    public Integer[] convertByteToInteger(Byte[] source) {
        ArrayList<Integer> intArray = new ArrayList<>();
        int counter = 0;
        int intValue = 0;
        byte[] bytes = new byte[Integer.BYTES];
        for (Byte it : source) {
            bytes[Integer.BYTES - 1 - counter] = it;
            counter += 1;
            if (counter == Integer.BYTES) {
                intArray.add(ByteBuffer.wrap(bytes).getInt());
                counter = 0;
            }
        }
        return (Integer[]) intArray.toArray();
    }

    public Double[] convertByteToDouble(Byte[] source) {//??
        ArrayList<Double> doubleArray = new ArrayList<>();
        for (int i = 0; i < source.length; i += 8) {
            ArrayList<Byte> byteArray = new ArrayList<>();
            int num = Double.BYTES;
            for (Byte it : source) {
                byteArray.add(it);
                num -= 1;
                if (num == 0)
                    break;
            }
            //body
            ByteBuffer byteBuffer = ByteBuffer.allocate(Double.BYTES);
            byteBuffer.put(convertToPrimitive(byteArray));
            byteBuffer.flip();
            byteBuffer.getDouble(0);
        }
        return (Double[]) doubleArray.toArray();
    }

    public Character[] convertByteToChar(Byte[] source) {
        ArrayList<Character> charArray = new ArrayList<>();
        //body
        int counter = 0;
        int intValue = 0;
        byte[] bytes = new byte[Character.BYTES];
        for (Byte it : source) {
            bytes[3 - counter] = it;
            counter += 1;
            if (counter == 4) {
                charArray.add(ByteBuffer.wrap(bytes).getChar());
                counter = 0;
            }
        }
        return (Character[]) charArray.toArray();
    }
}
