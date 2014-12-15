package huffman;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Class to encode a file using Huffman encoding.
 *
 * @author shanecrumlish
 */
public class Encode {

    /**
     * Main method to encode a file
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException, IOException {

        /*
         * ********* FOR LZ77 AND HUFFMAN ************
         */

        long begin = System.currentTimeMillis();

        System.out.println("Enter LZ Encode");
        LZ77Encode lz = new LZ77Encode();

        ArrayList<Byte> out = lz.lzEncode(args[0]);








        Map<Character, Node> map = new HashMap<>();




        ArrayList<Character> chars = new ArrayList<Character>();
        for(byte c : out) {
            char ch = (char) (c & 0xff);
            chars.add(ch);
            //System.out.print(ch);
            if(map.containsKey(ch)) {
                map.get(ch).increment();
            } else {
                Node node = new Node(1, ch);
                map.put(ch, node);
            }
        }
        //   System.out.println("MAP******" + map);


        //        System.out.println("STR******" + map.size());

        System.out.println("LZ Encode Complete");
        System.out.println("Enter Huffman Encode");

        ArrayList<Byte> bytes;
        HuffmanEncoder huffman = new HuffmanEncoder();
        bytes = huffman.huffmanEncode(map, chars);

        System.out.println("Huffman Encode Complete");
        System.out.println("Writing to File");

        // Write bytes to a file
        try(BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(args[1]))) {
            for(byte by : bytes) {
                output.write(by);
                output.flush();
            }
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
         * ********* FOR ONLY LZ77 ************
         */
        /*
         LZ77Encode lz = new LZ77Encode();

         ArrayList<Byte> out = lz.lzEncode(args[0]);

         try(BufferedOutputStream outputFile = new BufferedOutputStream(new FileOutputStream(args[1]))) {
         for(byte by : out) {
         outputFile.write(by);
         outputFile.flush();
         }
         }
         /*
         * ********* END ONLY LZ77 ************
         */
        /*
         * ********* FOR ONLY HUFFMAN ************
         */
        /*
         Map<Character, Node> map = new HashMap<>();

         String str = "";
         String line = "";
         boolean firstIteration = true;
         try(BufferedReader br = new BufferedReader(new FileReader(args[0]))) {
         while((line = br.readLine()) != null) {
         // Append new line chars after the first line
         if(!firstIteration) {
         line = '\n' + line;
         } else {
         firstIteration = false;
         }
         str += line;
         }

         } catch(Exception e) {
         System.out.println("Error: " + e);
         }

         ArrayList<Character> chars = new ArrayList<Character>();
         for(char c : str.toCharArray()) {
         chars.add(c);
         //  System.out.println(c);
         if(map.containsKey(c)) {
         map.get(c).increment();
         } else {
         Node node = new Node(1, c);
         map.put(c, node);
         }
         }

         ArrayList<Byte> bytes;
         HuffmanEncoder huffman = new HuffmanEncoder();
         bytes = huffman.huffmanEncode(map, chars);
         // Write bytes to a file
         try(BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(args[1]))) {
         for(byte by : bytes) {
         output.write(by);
         output.flush();
         }
         }
         /*
         * ********** END ONLY HUFFMAN *************
         */
    }
}
