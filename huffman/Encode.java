package huffman;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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
        Map<Character, Node> map = new HashMap<>();




        LZ77Encode lz = new LZ77Encode();

        ArrayList<Byte> out = lz.lzEncode(args[0]);

        /**
         * ********* FOR ONLY LZ77 ************
         */
        /*
         try(BufferedOutputStream outputFile = new BufferedOutputStream(new FileOutputStream(args[1]))) {
         for(byte by : out) {
         outputFile.write(by);
         outputFile.flush();
         }
         }*/
        /**
         * ******* END ONLY LZ77 ***********
         */
        /**
         * ********** FOR ONLY HUFFMAN *************
         */
        /*
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
         /**
         * ********** END ONLY HUFFMAN *************
         */
        ArrayList<Character> chars = new ArrayList<Character>();

        for(byte c : out) {
            char ch = (char) (c & 0xFF);
            chars.add(ch);
            System.out.println(ch);
            if(map.containsKey(ch)) {
                map.get(ch).increment();
            } else {
                Node node = new Node(1, ch);
                map.put(ch, node);
            }
        }
        System.out.println("MAP******" + map);


        /*
         Iterator it = map.entrySet().iterator();
         while(it.hasNext()) {
         Map.Entry pairs = (Map.Entry) it.next();
         System.out.println(pairs.getKey() + " = " + pairs.getValue());

         it.remove(); // avoids a ConcurrentModificationException
         }*/


        System.out.println("STR******" + map.size());





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
    }
}
