package huffman;

import java.io.*;

public class Generate {

    public static void main(String[] args) {
        String filename = args[0];
        int numK = Integer.parseInt(args[1]);
        int numBytes = ((int) Math.pow(2.0, 10)) * numK;
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
            char c;
            for(int i = 0; i < numBytes; i++) {
                //c = (char) ((int) (Math.random() * 95) + 32); // wide range [32,127)
                c = (char) ((int) (Math.random() * 20) + 97); // narrow range [97,123] (lower case letters)
                writer.write(c);
            }
            writer.close();
        } catch(Exception ex) {
        }
    }
}
