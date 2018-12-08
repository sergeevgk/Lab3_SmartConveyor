package huffman;

import java.util.ArrayList;

public interface DataConverter {

    Byte[] wrapArray(byte[] source);

    byte[] convertToPrimitive(ArrayList<Byte> list);

    byte[] convertToPrimitiveArray(Byte[] a);

    byte[] convertBitArrayToBytes(byte[] source);

    Byte[] convertIntegerToByte(Integer[] source);

    Byte[] convertDoubleToByte(Double[] source);

    Byte[] convertCharToByte(Character[] source);

    Integer[] convertByteToInteger(Byte[] source);

    Double[] convertByteToDouble(Byte[] source);

    Character[] convertByteToChar(Byte[] source);
}
