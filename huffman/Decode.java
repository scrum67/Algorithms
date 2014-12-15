package huffman;

import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Class to decode a file using Huffman encoding.
 *
 * @author shanecrumlish
 */
public class Decode {

    public static void main(String[] args) throws FileNotFoundException {
        /*
         * ********* FOR LZ77 AND HUFFMAN ************
         */

        long begin = System.currentTimeMillis();










        ArrayList<Node> nodes = new ArrayList<>();

        try(DataInputStream input = new DataInputStream(new FileInputStream(args[0]))) {
            // Get key from first k bits
            Byte firstByte = input.readByte();
            // First byte is number of characters in alphabet, will loop that many times to assign depths
            int loop = firstByte.intValue() & (0xff);

            // Deal with the header, which contains chars and their depths
            ArrayList<Node> tempNodes = new ArrayList<>();
            // System.out.println(loop);
            for(int i = 0; i < loop; i++) {
                // Create a node for each char
                Node node = new Node();
                char c = (char) input.readByte();
                //   System.out.println("char***: " + c);

                int length = (int) input.readByte();
                // System.out.println("len***: " + length);

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







        System.out.println("Enter Huffman Decode");

        HuffmanDecoder huffman = new HuffmanDecoder();
        ArrayList<Byte> in = huffman.huffmanDecode(nodes, args[0]);
        //System.out.println("input***: " + in);


        System.out.println("Huffman Decode Complete");

        System.out.println("Enter LZ Decode");
        for(int x = 0; x < 10; x++) {
            System.out.println("Byte: " + (char) (in.get(x) & 0xff));

        }

        // System.out.println("Byte: " + b);


        LZ77Decode lz = new LZ77Decode();
        StringBuffer output = lz.lzDecode(in);

        System.out.println("LZ Decode Complete");
        System.out.println("Writing to File");

        // Write output string to a file
        try(BufferedWriter outputFile = new BufferedWriter(new FileWriter(args[1]))) {
            outputFile.append(output);

        } catch(EOFException e) {
        } catch(Exception e) {
            System.out.println("Error: " + e);
        }
        long end = System.currentTimeMillis();
        long millis = end - begin;
        System.out.println(String.format("%d min, %d sec",
                TimeUnit.MILLISECONDS.toMinutes(millis),
                TimeUnit.MILLISECONDS.toSeconds(millis)
                - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))));

        /*
         * ********* END LZ77 AND HUFFMAN ************
         */
        /*
         * ********* HUFFMAN ONLY ************
         */
        /*
         ArrayList<Node> nodes = new ArrayList<>();

         try(DataInputStream input = new DataInputStream(new FileInputStream(args[0]))) {
         // Get key from first k bits
         Byte firstByte = input.readByte();
         // First byte is number of characters in alphabet, will loop that many times to assign depths
         int loop = firstByte.intValue() & (0xff);

         // Deal with the header, which contains chars and their depths
         ArrayList<Node> tempNodes = new ArrayList<>();
         // System.out.println(loop);
         for(int i = 0; i < loop; i++) {
         // Create a node for each char
         Node node = new Node();
         char c = (char) input.readByte();
         //   System.out.println("char***: " + c);

         int length = (int) input.readByte();
         // System.out.println("len***: " + length);

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
         //  System.out.println("huf: " + nodes);

         HuffmanDecoder huffman = new HuffmanDecoder();
         ArrayList<Byte> in = huffman.huffmanDecode(nodes, args[0]);
         //System.out.println("input***: " + in);

         System.out.println("Huffman Completed, just output to file remaining");
         StringBuffer output = new StringBuffer();
         for(byte b : in) {
         output.append((char) b);
         }
         // Write output string to a file
         try(BufferedWriter outputFile = new BufferedWriter(new FileWriter(args[1]))) {
         outputFile.append(output);
         } catch(EOFException e) {
         } catch(Exception e) {
         System.out.println("Error: " + e);
         }


         /*
         * ********* END HUFFMAN ONLY ************
         */
        /*
         * ********* LZ77 ONLY ************
         */
        /*
         ArrayList<Byte> bytes = new ArrayList<Byte>();

         try(DataInputStream in = new DataInputStream(new FileInputStream(args[0]))) {

         // for(int i = 0; i < loop; i++) {
         while(true) {
         byte by = (byte) in.readByte();
         bytes.add(by);
         }
         } catch(EOFException e) {
         } catch(Exception e) {
         System.out.println("Error: " + e);
         }


         LZ77Decode lz = new LZ77Decode();
         StringBuffer output = lz.lzDecode(bytes);

         //ArrayList<Character> output = lz.lzDecode(bytes);


         // Write output string to a file
         try(BufferedWriter outputFile = new BufferedWriter(new FileWriter(args[1]))) {
         outputFile.append(output);

         } catch(EOFException e) {
         } catch(Exception e) {
         System.out.println("Error: " + e);
         }

         /*
         * ********* END LZ77 ONLY ************
         */
    }
}
