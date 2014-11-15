package huffman;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Class to decode a file using Huffman encoding.
 *
 * @author shanecrumlish
 */
public class Decode2 {

    public static void main(String[] args) throws FileNotFoundException {

        ArrayList<Node> nodes = new ArrayList<>();
        try(DataInputStream input = new DataInputStream(new FileInputStream(args[0]))) {
            // Get key from first k bits
            Byte firstByte = input.readByte();

            int loop = firstByte.intValue();
            for(int i = 0; i < loop; i++) {
                Node node = new Node();
                nodes.add(node);
                char c = (char) input.readByte();
                int length = (int) input.readByte();
                nodes.get(i).setChar(c);
                nodes.get(i).setDepth(length);
            }
        } catch(EOFException e) {
        } catch(Exception e) {
            System.out.println("Error: " + e);
        }


        // create canonical huffman codes
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
        }





        // Now we have the bits as a string, their associated chars, and bit lengths
        // Just convert the rest of the bytes in file to chars as you output
        try(DataOutputStream output = new DataOutputStream(new FileOutputStream(args[1]))) {
            try(DataInputStream input2 = new DataInputStream(new FileInputStream(args[0]))) {
                Byte firstByte = input2.readByte();
                int loop = firstByte.intValue();
                for(int i = 0; i < loop; i++) {
                    // get rid of stuff I already dealt with
                    char c = (char) input2.readByte();
                    int length = (int) input2.readByte();
                }
                int remaining = input2.available();

                boolean eof = false;
                String storeBitString = "";
                while(!eof) {
                    byte b = input2.readByte();
                    System.out.println("B: " + b);
                    String bitToAccept = "";
                    String bitString = String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0');
                    System.out.println(bitString);
                    if(!storeBitString.equals("")) {
                        bitString = storeBitString + bitString;
                        storeBitString = "";
                    }




                    // TREEWALK - build the tree and traverse it depending on input bitString
                    // Also comment decode better


                    // Check each individial bitString with each bit of each Node
                    for(int i = 0; i < bitString.length(); i++) {

                        bitToAccept += bitString.charAt(i);

                        for(Node n : nodes) {
                            // if the bits are the same, store the associated character
                            if(bitToAccept.equals(n.getBits())) {
                                // once it hits the EOF, stop adding characters
                                if(n.getChar() == '\u0000') {
                                    System.out.println("HERE");
                                    eof = true;
                                }
                                // Stop writing once EOF is hit (ignore padding chars)
                                if(!eof) {
                                    output.writeByte(n.getChar());
                                    bitToAccept = "";

                                }
                            }
                        }
                    }
                    if(!bitToAccept.isEmpty()) {
                        storeBitString += bitToAccept;
                    }
                    System.out.println("end: " + bitString);

                }
            } catch(EOFException e) {
            } catch(Exception e) {
                System.out.println("Error: " + e);
            }
        } catch(EOFException e) {
        } catch(Exception e) {
            System.out.println("Error: " + e);
        }

    }
}
