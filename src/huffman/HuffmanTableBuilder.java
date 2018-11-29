package huffman;

import log.Log;

import java.io.*;
import java.util.*;

public class HuffmanTableBuilder {
    private Map<Byte, Integer> frequencyTable = new HashMap<>();
    private String inputFileName;

    public HuffmanTableBuilder(String inputFileName) {
        this.inputFileName = inputFileName;
    }

    /**
     * @return Map <Byte, String> huffman table - each Byte value in table matches some unique prefix binary code
     * represented by String
     * <p>
     * Method reads input file via BufferedInputStream reader,
     * counts frequency of bytes(builds frequency table),
     * builds huffman tree represented by Node class using priority queue,
     * builds huffman table
     */
    public Map<Byte, String> buildHuffmanTable() {
        try {
            FileInputStream inStream = new FileInputStream(inputFileName);
            BufferedInputStream reader = new BufferedInputStream(inStream);
            int c;
            byte b;
            while ((c = reader.read()) != -1) {
                b = (byte) c;
                if (!frequencyTable.containsKey(b)) {
                    frequencyTable.put(b, 0);
                }
                int value = frequencyTable.get(b);
                frequencyTable.put(b, value + 1);
            }
            Queue<Node> queue = new PriorityQueue<>();
            for (byte ch : frequencyTable.keySet()) {
                Node n = new Node(ch, frequencyTable.get(ch), null, null);
                queue.add(n);
            }
            while (queue.size() > 1) {
                Node first = queue.poll();
                Node second = queue.poll();
                assert second != null;
                queue.add(new Node(null, first.getPriority() + second.getPriority(), first, second));
            }
            Node tree = queue.poll();

            return buildHuffmanTableInner(tree);
        } catch (IOException e) {
            Log.logReport("Error while building huffman table occurred.\n");
            return null;
        }
    }

    /**
     * @param tree huffman tree
     * @return Map <Byte, String> huffman table
     * <p>
     * Method traverses huffman tree,
     * builds huffman table for each unique byte in source text (in huffman tree leaves)
     */
    private HashMap<Byte, String> buildHuffmanTableInner(Node tree) {
        HashMap<Byte, String> map = new HashMap<>();
        traverse(tree, "", map);
        return map;
    }

    /**
     * @param tree huffman tree (Node tree is a root of huffman tree)
     * @param code Temporary string accumulating char representation of bits in new bit sequence for current byte
     * @param map  container for pairs (byte value, matching code)
     *             <p>
     *             traverses tree via DFS algorithm, accumulating code for byte value in tree's leaf
     */
    private void traverse(Node tree, String code, Map<Byte, String> map) {
        if (tree == null) {
            return;
        }
        if (tree.isLeaf()) {
            if (code.isEmpty()) {
                map.put(tree.getByte(), "0");
            } else {
                map.put(tree.getByte(), code);
            }
            return;
        }
        traverse(tree.left, code + "0", map);
        traverse(tree.right, code + "1", map);
    }
}
