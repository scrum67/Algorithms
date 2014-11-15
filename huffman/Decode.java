package huffman;

import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.util.ArrayList;

/**
 * Class to decode a file using Huffman encoding.
 *
 * @author shanecrumlish
 */
public class Decode {

    public static void main(String[] args) throws FileNotFoundException {

        ArrayList<Node> nodes = new ArrayList<>();

        try(DataInputStream input = new DataInputStream(new FileInputStream(args[0]))) {
            // Get key from first k bits
            Byte firstByte = input.readByte();
            // First byte is number of characters in alphabet, will loop that many times to assign depths
            int loop = firstByte.intValue();

            // Deal with the header, which contains chars and their depths
            ArrayList<Node> tempNodes = new ArrayList<>();

            for(int i = 0; i < loop; i++) {
                // Create a node for each char
                Node node = new Node();
                char c = (char) input.readByte();
                System.out.println("char***: " + c);

                int length = (int) input.readByte();
                System.out.println("len***: " + length);

                // Set the node's char and depth vars accordingly and add to priority queue
                node.setChar(c);
                node.setDepth(length);
                tempNodes.add(node);
            }
            nodes = tempNodes;
        } catch(EOFException e) {
        } catch(Exception e) {
            System.out.println("Error: " + e);
        }

        HuffmanDecoder huffman = new HuffmanDecoder();
        String in = huffman.huffmanDecode(nodes, args[0]);
        System.out.println("input***: " + in);


        /* FOR ONLY LZ DECODE

         try(DataInputStream in = new DataInputStream(new FileInputStream(args[0]))) {
         ArrayList<Byte> bytes = new ArrayList();

         // for(int i = 0; i < loop; i++) {
         while(true) {
         byte by = (byte) in.readByte();
         bytes.add(by);
         }
         } catch(EOFException e) {
         } catch(Exception e) {
         System.out.println("Error: " + e);
         }
         */




















        LZ77Decode lz = new LZ77Decode();
        //String output = lz.lzDecode(in);

        // Write output string to a file
        try(BufferedWriter outputFile = new BufferedWriter(new FileWriter(args[1]))) {
            //    outputFile.write(output);
        } catch(EOFException e) {
        } catch(Exception e) {
            System.out.println("Error: " + e);
        }
    }
}
