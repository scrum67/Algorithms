package huffman;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.PriorityQueue;

/**
 *
 * @author shanecrumlish
 */
public class HuffmanEncoder {

    /**
     *
     * @param map, a map of characters to Nodes that the encoding will use
     * @param str, the input string from LZ encoding
     * @return an ArrayList of Bytes to output to a file
     */
    public ArrayList<Byte> huffmanEncode(Map<Character, Node> map, ArrayList<Character> str) {

        // Create EOF character and add it to the map
        //  Node temp = new Node(1, '\u0000');
        Node temp = new Node(1, 'Z');

        //  map.put('\u0000', temp);

        // Add nodes into a priority queue, sorted by frequency then ascii value
        PriorityQueue<Node> nodes = new PriorityQueue<>(map.size(), new Comparator<Node>() {
            @Override
            public int compare(Node node1, Node node2) {
                // Frequency sort first
                if(node1.getFrequency() != node2.getFrequency()) {
                    return node1.getFrequency() - node2.getFrequency();
                } else {
                    // If equal frequecies, sort by ascii value
                    return (int) node1.getChar() - (int) node2.getChar();
                }
            }
        });
        // Add map nodes to priority queue, will automatically be sorted as defined by comparator
        nodes.addAll(map.values());
        nodes.add(temp);


        // Create tree

        // Loop ends when there is one node containing all other nodes as children
        while(nodes.size() > 1) {
            // Make the first two elements left and right children of a new node
            Node tempNode1 = nodes.poll();
            Node tempNode2 = nodes.poll();
            // New node's frequency is equal to the two children's freqs combined
            Node newNode = new Node(tempNode1, tempNode2);
            // Add new node to priority queue
            nodes.add(newNode);
        }

        // Do canonical ordering here

        // Get base nodes with their bit lengths (depth of tree), nodes.poll gets
        // rid off top of tree, or the node that contains all the other nodes
        getBaseNodes(nodes, nodes.poll(), 0);

        // Sort the nodes by depth, just changing the comparator and re-adding to new pq
        PriorityQueue<Node> nodesByDepth = new PriorityQueue<>(nodes.size(), new Comparator<Node>() {
            // New comparator
            @Override
            public int compare(Node node1, Node node2) {
                if(node1.getDepth() != node2.getDepth()) {
                    return node1.getDepth() - node2.getDepth();
                } else {
                    return (int) node1.getChar() - (int) node2.getChar();
                }
            }
        });
        // Add to new priorityQueue
        nodesByDepth.addAll(nodes);

        // Now that nodes have been ordered by depth, assign them their canonical bits

        int code = 0;

        // Add nodes to an arrayList in order to do bit shift later, since priority queues have no order
        ArrayList<Node> bitNodes = new ArrayList<>();
        int si = nodesByDepth.size();
        for(int i = 0; i < si; i++) {
            bitNodes.add(nodesByDepth.poll());
        }

        // Loop through the nodes and assign them a new bit representation based on their depth
        for(int i = 0; i < bitNodes.size(); i++) {
            String stringCode = Integer.toBinaryString(code);
            // Give each node bits equivalent to depth in tree
            while(stringCode.length() < bitNodes.get(i).getDepth()) {
                stringCode = "0" + stringCode;
            }
            // Check that you are not on the last node (most frequent), and bit shift left
            if(i != bitNodes.size() - 1) {
                // If the next node's depth is greater, code + 1 will be shifted by the difference
                code = (code + 1) << (bitNodes.get(i + 1).getDepth() - bitNodes.get(i).getDepth());
            }
            // Assign the bit representation to a node
            bitNodes.get(i).setBits(stringCode);
        }


        // Create byte array to contain bytes that will be written to the compressed file
        ArrayList<Byte> bytes = new ArrayList<>();

        // Create header to be placed at beginning of new file

        // Header Format is as follows:
        // 8 bits representing k, the number of characters in the alphabet
        // Followed by k pairs of bytes, in the following format:
        // First byte: the character value
        // Second byte: the length of the codeword for the value


        // The first element in the header, 8 bits for num of chars
        bytes.add((byte) bitNodes.size());

        // Initialize a hashmap to be used for searching
        Map<Character, Node> charMap = new HashMap<>();

        // Fill in the rest of the header and insert values of charMap
        for(Node n : bitNodes) {
            bytes.add(((Integer) n.getChar()).byteValue());
            bytes.add(((Integer) n.getDepth()).byteValue());
            // Put values into charMap for quick access later
            charMap.put((char) n.getChar(), n);
        }

        String eightBit = "";
        for(Iterator<Character> it = str.iterator(); it.hasNext();) {
            char c = it.next();
            eightBit += charMap.get(c).getBits();
            System.out.println("C: " + c + ", Assoc bits: " + charMap.get(c).getBits());
            System.out.println("C: " + c + ", Assoc freq: " + charMap.get(c).getFrequency());

        }
        System.out.println("BITS: " + eightBit);

        // put null at the end of eightBit string
        //  eightBit += charMap.get('\u0000').getBits();
        eightBit += charMap.get('Z').getBits();

        System.out.print("BITS: \n");

        // Break up eightbit (currently contains all bits for the file in a row) into bytes of 8 bits
        while(eightBit.length() >= 8) {
            String string = "";
            string = eightBit.substring(0, 8);
            // Cut bits just seperated out of eightBit
            eightBit = eightBit.substring(8);
            System.out.println(string);

            // Convert the string into integer and integer into a byte
            Integer binaryInt = Integer.parseInt(string, 2);
            bytes.add(binaryInt.byteValue());
        }
        // }

        System.out.println("\nREMAINDER: " + eightBit);
        System.out.println("EIGHTBIT: " + eightBit);

        // Add padding 0's if the last chunk of bytes is not divisable by 8
        if(eightBit.length() != 0) {
            int len = 8 - eightBit.length();
            String s = "";
            for(int i = 0; i < len; i++) {
                s += "0";
            }
            eightBit = eightBit + s;

            // Convert the last eight bits to int and then to a byte, and add to array
            Integer binaryInt = Integer.parseInt(eightBit, 2);
            bytes.add(binaryInt.byteValue());
        }

        /* } catch(Exception e) {
         System.out.println("Error: " + e);
         }*/
        return bytes;
    }

    /**
     * Function to get the nodes at the bottom of a Huffman tree. It also
     * assigns depths to the nodes based off their position in the tree.
     *
     * @param nodes, the nodes to add to
     * @param node, the start node
     * @param depth, also the length of the bit string
     */
    public void getBaseNodes(PriorityQueue<Node> nodes, Node node, int depth) {
        if(node.bottom()) {
            node.setDepth(depth);
            nodes.add(node);
        } else {
            getBaseNodes(nodes, node.getLeft(), depth + 1);
            getBaseNodes(nodes, node.getRight(), depth + 1);
        }
    }
}
