package huffman;

import java.util.BitSet;

/**
 * Class to represent a node in a Huffman tree. Contains a character, frequency,
 * depth (aka bit length), bit string, and left and right nodes.
 *
 * @author shanecrumlish
 */
public class Node {

    private int frequency;
    private char c;
    private Node left;
    private Node right;
    private int depth;
    private Integer bits;
    private int internal = 0;

    public Node() {
    }

    public Node(int newInt, char newChar) {
        this.frequency = newInt;
        this.c = newChar;
    }

    public Node(Node toLeft, Node toRight) {
        this.left = toLeft;
        this.right = toRight;
        this.frequency = left.getFrequency() + right.getFrequency();
    }

    public void increment() {
        frequency++;
    }

    public int getFrequency() {
        return frequency;
    }

    public int getDepth() {
        return depth;
    }

    public int getChar() {
        return c;
    }

    public Integer getBits() {
        return bits;
    }

    public void setFrequency(int newFreq) {
        frequency = newFreq;
    }

    public void setDepth(int newDepth) {
        depth = newDepth;
    }

    public void setChar(char newChar) {
        c = newChar;
    }

    public void setBits(Integer newBits) {
        bits = newBits;
    }

    public Node getLeft() {
        return left;
    }

    public Node getRight() {
        return right;
    }

    public boolean bottom() {
        if(left == null && right == null) {
            return true;
        } else {
            return false;
        }
    }

    public int getInternal() {
        return internal;
    }

    public void setInternal(int newInternal) {
        internal = newInternal;
    }
}
