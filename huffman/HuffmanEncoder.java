package huffman;

import java.util.ArrayList;
import java.util.BitSet;
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
        Node temp = new Node(1, '~');

        // map.put('~', temp);

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
        System.out.println("Add to PQ");

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

        System.out.println("Tree Created");

        // Do canonical ordering here

        // Get base nodes with their bit lengths (depth of tree), nodes.poll gets
        // rid off top of tree, or the node that contains all the other nodes
        getBaseNodes(nodes, nodes.poll(), 0);

        System.out.println("After recusive call");

        // Sort the nodes by depth, just changing the comparator and re-adding to new pq
        PriorityQueue<Node> nodesByDepth = new PriorityQueue<>(nodes.size(), new Comparator<Node>() {
            // New comparator
            @Override
            public int compare(Node node1, Node node2) {
                if(node1.getDepth() != node2.getDepth()) {
                    return node1.getDepth() - node2.getDepth();
                } else if(node1.getChar() == '~') {
                    return (int) node2.getChar() - 0;
                } else if(node2.getChar() == '~') {
                    return 0 - (int) node1.getChar();
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

        System.out.println("ArrayList for bitshift");

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

            Integer tempCode = Integer.parseInt(stringCode, 2);

            // Assign the bit representation to a node
            bitNodes.get(i).setBits(tempCode);
        }

        System.out.println("After bitshift");

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
            System.out.println("Char" + ((Integer) n.getChar()).byteValue());
            bytes.add(((Integer) n.getDepth()).byteValue());
            System.out.println("depth" + ((Integer) n.getDepth()).byteValue());
            // Put values into charMap for quick access later
            charMap.put((char) n.getChar(), n);
        }

        System.out.println("Header created");

        System.out.println("File Length = " + str.size());
        System.out.println("Map Size = " + charMap.size());

        // BitSet eightBit = new BitSet();
        // firstBits needs its initial bitset
        // BitSet firstBits = new BitSet();
        Integer firstBit = 0;
        int totalLength = 0;
        int firstBitLength = 0;

        str.add('~');
        System.out.println("INPUT LAST CHAR: " + str.get(str.size() - 1));

        for(Iterator<Character> it = str.iterator(); it.hasNext();) {
            char c = it.next();


            //Integer firstBit = 5;
            //int firstBitLength = 3;

            Integer tempB = charMap.get(c).getBits();
            int tempBLength = charMap.get(c).getDepth();

            // System.out.println("firstBit: " + Integer.toBinaryString(firstBit));
            // System.out.println("tempB: " + Integer.toBinaryString(tempB));

            Integer combinedBits = firstBit << tempBLength;
            // System.out.println("combined: " + Integer.toBinaryString(combinedBits));

            combinedBits = combinedBits | tempB;
            Byte byt = combinedBits.byteValue();
            // System.out.println("combined: " + Integer.toBinaryString(combinedBits));
            // System.out.println("combined norm: " + combinedBits);

            //  System.out.println("combined byt: " + byt);
            totalLength = firstBitLength + tempBLength;

            while(totalLength > 8) {
                // chop up by bits
                Integer eightBit;
                Integer leftoverBits;
                int shift = totalLength - 8;
                eightBit = combinedBits >> shift;
                byte bitByte = (byte) (eightBit.byteValue() & 0xff);

                int subtr = 0;

                for(int i = 0; i < shift; i++) {
                    subtr += 1 * Math.pow(2, i);
                    //      System.out.println("inside: " + subtr);
                }

                Integer newBits = combinedBits & subtr;
                //  System.out.println("newBits: " + newBits);
                //byte newBits2 = (byte) (newBits.byteValue() & 0xff);

                //  String s1 = String.format("%8s", Integer.toBinaryString(newBits2 & 0xff)).replace(' ', '0');
                //  System.out.println("s1: " + s1);
                //  System.out.println("C: " + Integer.toBinaryString(combinedBits));

                //  System.out.println("E: " + Integer.toBinaryString(eightBit));

                // then subtract by 8
                totalLength = totalLength - 8;
                //  System.out.println(totalLength);
                bytes.add(bitByte);
                combinedBits = newBits;
            }
            firstBitLength = totalLength;
            firstBit = combinedBits;

        }



        if(totalLength != 0) {
            int padLength = 8 - totalLength;
            Integer eightBit = firstBit << padLength;
            byte bitByte = (byte) (eightBit.byteValue() & 0xff);
            bytes.add(bitByte);
        }














        /* while(totalLength > 8) {
         }
         Integer endBits = charMap.get('~').getBits();
         byte endByte = (byte) (endBits.byteValue() & 0xff);
         System.out.println("endbyte " + String.format("%8s", Integer.toBinaryString(endByte & 0xFF)).replace(' ', '0'));

         bytes.add(endByte);*/

        //String eightBit = "";
        //String string = "";
        /*
         for(Iterator<Character> it = str.iterator(); it.hasNext();) {
         char c = it.next();
         Integer tempB = charMap.get(c).getBits();

         Integer combinedBits = firstBit << charMap.get(c).getDepth();
         combinedBits = combinedBits & tempB;
         System.out.println("Important Part: " + combinedBits.byteValue());

         //  BitSet combinedBits = new BitSet(firstBits.length() + charMap.get(c).getBits().length());
         // Shift to make room for the new bits
         // Integer x = (Integer)combinedBits >> firstBits.length();
         //Integer x = 0;
         //x.
         // allocate a bitset with length of previous + new, eightBit.length + charMap.get(c).getBits()
         // pad with 0s
         // Logical AND with charMap.get(c).getBits()
         // inside a loop, do a bitshift right >> or >>> idk which yet
         // loop for eightBit.length
         // now we have 11 and 0011111111
         // logical AND to get 1111111111
         // how to do substring? new BitSet (1 or 2 new bitsets idk which right now)

         eightBit.addAll(charMap.get(c).getBits());
         if(eightBit.size() > 8) {
         ArrayList<Byte> tempBit = new ArrayList<Byte>(eightBit.subList(0, 8));
         // Convert the string into integer and integer into a byte
         Integer binaryInt = Integer.parseInt(string, 2);
         bytes.add(binaryInt.byteValue());
         string = "";
         }
         // System.out.println("C: " + c + ", Assoc bits: " + charMap.get(c).getBits());
         //  System.out.println("C: " + c + ", Assoc freq: " + charMap.get(c).getFrequency());
         *
         *
         // stats: number of chars, number of LZ chars, number of leaf nodes in the huffman tree
         *
         *
         //
         }*/ //     System.out.println("BITS: " + eightBit);
        /*
         // put null at the end of eightBit string
         //  eightBit += charMap.get('\u0000').getBits();
         eightBit += charMap.get('~').getBits();
         if(eightBit.length() > 8) {
         string = eightBit.substring(0, 8);
         // Convert the string into integer and integer into a byte
         Integer binaryInt = Integer.parseInt(string, 2);
         bytes.add(binaryInt.byteValue());
         string = "";
         }*/

        //   System.out.print("BITS: \n");
/*
         // Break up eightbit (currently contains all bits for the file in a row) into bytes of 8 bits
         while(eightBit.length() >= 8) {
         String string = "";
         string = eightBit.substring(0, 8);
         // Cut bits just seperated out of eightBit
         eightBit = eightBit.substring(8);
         //      System.out.println(string);

         // Convert the string into integer and integer into a byte
         Integer binaryInt = Integer.parseInt(string, 2);
         bytes.add(binaryInt.byteValue());
         }

         //    System.out.println("\nREMAINDER: " + eightBit);
         //  System.out.println("EIGHTBIT: " + eightBit);

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
         */
        /* } catch(Exception e) {
         System.out.println("Error: " + e);
         }*/
        System.out.println("END");
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
