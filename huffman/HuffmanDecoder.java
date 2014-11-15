package huffman;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 *
 * @author shanecrumlish
 */
public class HuffmanDecoder {

    /**
     * Function to decode a file that has been encoded using Huffman encoding.
     *
     * @param nodes, the nodes to use for decoding, contain a character and a
     * length
     * @param inputFile, the input file to decode
     * @param outputFile, the output decoded file
     */
    public ArrayList<Byte> huffmanDecode(ArrayList<Node> nodes, String inputFile) {

        Collections.sort(nodes, new Comparator<Node>() {
            @Override
            public int compare(Node node1, Node node2) {
                if(node1.getDepth() != node2.getDepth()) {
                    return node2.getDepth() - node1.getDepth();
                } else {
                    return (int) node2.getChar() - (int) node1.getChar();
                }
            }
        });

        for(int i = 0; i < nodes.size(); i++) {
            System.out.println("NODES: " + nodes.get(i).getChar());
        }

        // Create tree
        while(nodes.size() > 1) {
            Node newNode = new Node(nodes.get(0), nodes.get(1));
            System.out.println("0: " + (char) nodes.get(0).getChar() + " , 1: " + (char) nodes.get(1).getChar());
            newNode.setDepth(nodes.get(0).getDepth() - 1);

            nodes.remove(0);
            nodes.remove(0);
            newNode.setInternal(1);
            nodes.add(newNode);

            Collections.sort(nodes, new Comparator<Node>() {
                @Override
                public int compare(Node node1, Node node2) {
                    if(node1.getDepth() != node2.getDepth()) {
                        return node2.getDepth() - node1.getDepth();
                    } else if(node1.getInternal() != node2.getInternal()) {
                        return node2.getInternal() - node1.getInternal();
                    } else {
                        return (int) node2.getChar() - (int) node1.getChar();
                    }
                }
            });
        }

//        System.out.println("bitstring left: " + (char) treeNodes.get(0).getLeft().getLeft().getRight().getRight().getChar());

        System.out.println("bitstring left: " + (char) nodes.get(0).getRight().getChar());
        System.out.println("bitstring left: " + (char) nodes.get(0).getLeft().getRight().getChar());
        //   System.out.println("bitstring left: " + (char) nodes.get(0).getLeft().getLeft().getRight().getChar());


        ArrayList<Byte> output = new ArrayList<Byte>();

        // Now we have the bits as a string, their associated chars, and bit lengths
        // Just convert the rest of the bytes in file to chars as you output
        try(DataInputStream input = new DataInputStream(new FileInputStream(inputFile))) {
            Byte firstByte = input.readByte();
            int loop = firstByte.intValue();
            for(int i = 0; i < loop; i++) {
                // Ignore header since it's already been dealt with
                input.readByte();
                input.readByte();
            }

            // Initialize currentNode to the top of the tree
            Node currentNode = nodes.get(0);
            boolean eof = false;

            while(!eof) {
                byte b = input.readByte();
                System.out.println("B: " + b);

                // Convert byte into 1's and 0's
                String bitString = String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0');
                System.out.println(bitString);

                // Check each individial bitString with the tree
                for(int i = 0; i < bitString.length(); i++) {
                    // Left side of tree if a 1 is found
                    if(bitString.charAt(i) == '1') {
                        if(currentNode.getLeft() != null && currentNode.getRight() != null) {
                            currentNode = currentNode.getLeft();
                        } else {
                            if(!eof) {
                                // Found character, so output it
                                output.add((byte) currentNode.getChar());

                                // if(currentNode.getChar() == '\u0000') {
                                if(currentNode.getChar() == 'Z') {
                                    System.out.println("Thinks 0 is EOF first");
                                    output.remove(output.size() - 1);

                                    eof = true;
                                }
                                // Reset currentNode to top of tree
                                currentNode = nodes.get(0);

                                // Go back one becase we used up an i to get here
                                i--;
                            }
                        }
                        // Right side of tree if a 0 is found
                    } else if(bitString.charAt(i) == '0') {
                        if(currentNode.getLeft() != null && currentNode.getRight() != null) {
                            currentNode = currentNode.getRight();
                        } else {
                            if(!eof) {
                                // Found character, so output it
                                output.add((byte) currentNode.getChar());

                                // if(currentNode.getChar() == '\u0000') {
                                if(currentNode.getChar() == 'Z') {
                                    System.out.println("Thinks 0 is EOF second");
                                    output.remove(output.size() - 1);

                                    eof = true;
                                }
                                // Reset currentNode to top of tree
                                currentNode = nodes.get(0);

                                // Go back one becase we used up an i to get here
                                i--;
                            }
                        }
                    }
                }
            }
        } catch(EOFException e) {
        } catch(Exception e) {
            System.out.println("Error: " + e);
        }
        return output;
    }
}
