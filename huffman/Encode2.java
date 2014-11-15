package huffman;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * Class to encode a file using Huffman encoding.
 *
 * @author shanecrumlish
 */
public class Encode2 {

    /**
     * Main method to encode a file
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException, IOException {
        String line;
        Map<Character, Node> map = new HashMap<>();
        boolean firstIteration = true;

        try(BufferedReader br = new BufferedReader(new FileReader(args[0]))) {
            while((line = br.readLine()) != null) {
                if(!firstIteration) {
                    line = '\n' + line;
                } else {
                    firstIteration = false;
                }
                for(char c : line.toCharArray()) {
                    if(map.containsKey(c)) {
                        map.get(c).increment();
                    } else {
                        Node node = new Node(1, c);
                        map.put(c, node);
                    }
                }
            }
        } catch(Exception e) {
            System.out.println("Error: " + e);
        }
        Node temp = new Node(1, '\u0000');
        map.put('\u0000', temp);



        // sort by frequency then ascii value
        ArrayList<Node> nodes = new ArrayList<>(map.values());

        System.out.println("PRINT:" + nodes);

        Collections.sort(nodes, new Comparator<Node>() {
            @Override
            public int compare(Node node1, Node node2) {
                if(node1.getFrequency() != node2.getFrequency()) {
                    return node1.getFrequency() - node2.getFrequency();
                } else {
                    return (int) node1.getChar() - (int) node2.getChar();
                }
            }
        });

        // Create tree
        while(nodes.size() > 1) {
            Node newNode = new Node(nodes.get(0), nodes.get(1));
            nodes.remove(0);
            nodes.remove(0);
            nodes.add(0, newNode);
            Collections.sort(nodes, new Comparator<Node>() {
                @Override
                public int compare(Node node1, Node node2) {
                    return node1.getFrequency() - node2.getFrequency();
                }
            });
        }

        // Do canonical ordering here
        // get base nodes with their bit lengths (depth of tree)
        getBaseNodes(nodes, nodes.get(0), 0);

        // get rid of first element of nodes since it's top of the tree, not a base node
        nodes.remove(0);

        Collections.sort(nodes, new Comparator<Node>() {
            @Override
            public int compare(Node node1, Node node2) {
                if(node1.getDepth() != node2.getDepth()) {
                    return node1.getDepth() - node2.getDepth();
                } else {
                    return (int) node1.getChar() - (int) node2.getChar();
                }
            }
        });

        int code = 0;
        for(int i = 0; i < nodes.size(); i++) {
            String stringCode = Integer.toBinaryString(code);
            while(stringCode.length() < nodes.get(i).getDepth()) {
                stringCode = "0" + stringCode;
            }
            if(i != nodes.size() - 1) {
                code = (code + 1) << (nodes.get(i + 1).getDepth() - nodes.get(i).getDepth());
            } else {
                code++;
            }
            nodes.get(i).setBits(stringCode);



            System.out.println("Char: " + nodes.get(i).getChar() + ", bits: " + stringCode);
        }

        // create byte array for the characters and their associated lengths
        ArrayList<Byte> bytes = new ArrayList<>();
        bytes.add(((byte) nodes.size()));
        // Fill in the rest of the key byte array, start at position 1
        for(Node n : nodes) {
            bytes.add(((Integer) n.getChar()).byteValue());
            bytes.add(((Integer) n.getDepth()).byteValue());
        }

        String line2;
        firstIteration = true;
        String eightBit = "";
        try(BufferedReader br2 = new BufferedReader(new FileReader(args[0]))) {
            while((line2 = br2.readLine()) != null) {
                if(!firstIteration) {
                    line2 = '\n' + line2;
                } else {
                    firstIteration = false;
                }
                for(char c : line2.toCharArray()) {
                    for(Node n : nodes) {
                        if(c == n.getChar()) {
                            // TODO: USE A HASHMAP HERE, WAY EASIER TO FIND STUFF
                            eightBit += n.getBits();
                            System.out.println("C: " + c + ", Assoc bits: " + n.getBits());
                        }
                    }
                }
                System.out.println("BITS: " + eightBit);

                // put null at the end of eightBit string
                for(Node n : nodes) {
                    if(n.getChar() == '\u0000') {
                        eightBit += n.getBits();
                    }
                }


                System.out.print("BITS: ");
                while(eightBit.length() >= 8) {
                    String string = "";

                    string = eightBit.substring(0, 8);
                    eightBit = eightBit.substring(8);
                    System.out.println(string);
                    Integer binaryInt = Integer.parseInt(string, 2);
                    bytes.add(binaryInt.byteValue());
                }
            }
            System.out.println();
            System.out.println("REMAINDER: " + eightBit);


            System.out.println("EIGHTBIT: " + eightBit);

            if(eightBit.length() != 0) {
                int len = 8 - eightBit.length();
                String s = "";
                for(int i = 0; i < len; i++) {
                    s += "0";
                }

                // arraylist -> priority queue
                // comment better
                // understand code better

                eightBit = eightBit + s;
            }
            System.out.println("EIGHTBIT: " + eightBit);
            Integer binaryInt = Integer.parseInt(eightBit, 2);
            System.out.println("after eightbit: " + binaryInt);
            bytes.add(binaryInt.byteValue());
            eightBit = "";

        } catch(Exception e) {
            System.out.println("Error: " + e);
        }

        // Write to a file
        try(BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(args[1]))) {
            for(byte by : bytes) {
                System.out.println("BY: " + by);
                output.write(by);
                output.flush();
            }
        }
    }

    /**
     * Function to get the nodes at the bottom of a Huffman tree.
     *
     * @param nodes, the nodes to add to
     * @param node, the start node
     * @param depth, also the length of the bit string
     */
    public static void getBaseNodes(ArrayList<Node> nodes, Node node, int depth) {
        if(node.bottom()) {
            node.setDepth(depth);
            nodes.add(node);
        } else {
            getBaseNodes(nodes, node.getLeft(), depth + 1);
            getBaseNodes(nodes, node.getRight(), depth + 1);
        }
    }
}
