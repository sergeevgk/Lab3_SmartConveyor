package huffman;

public class Node implements Comparable<Node> {
    Node left, right;
    private Byte character;
    private int priority;

    public int getPriority() {
        return priority;
    }

    public Byte getByte() {
        return character;
    }

    public Node(Byte character, int priority, Node left, Node right) {
        this.priority = priority;
        this.character = character;
        this.left = left;
        this.right = right;
    }

    public boolean isLeaf() {
        return this.character != null;
    }

    @Override
    public int compareTo(Node o) {
        return this.priority - o.priority;
    }
}


