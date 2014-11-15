package huffman;

import java.util.ArrayList;

/**
 *
 * @author shanecrumlish
 */
public class LZ77Decode {

    public String lzDecode(ArrayList<Byte> input) {

        // window contains already processed strings
        String output = "";

        int windowIndex;
        int charsLength;
        String windowIndexString;
        String charsLengthString;

        for(int i = 0; i < input.size(); i++) {
            System.out.println("window " + output);

            windowIndexString = "";
            charsLengthString = "";

            // Get the position backwards from the current position for patterns
            System.out.println("input: " + input);
            windowIndexString += input.get(i);
            i++;

            windowIndex = Integer.parseInt(windowIndexString);

            // Get the character length to process for patterns
            charsLengthString += input.get(i);
            i++;

            charsLength = Integer.parseInt(charsLengthString);

            System.out.println("window index " + windowIndex + " chars length " + charsLength);

            // Exclude any single entries
            if(charsLength != 0) {
                // Set position back from thed end of the window windowIndex chars
                int pos = output.length() - windowIndex;

                // Go through go from position until the char length, adding to output as you go
                for(int j = pos; j < pos + charsLength; j++) {
                    output += output.charAt(j);
                }
            }

            // At this point, the next character from input is a single char
            output += (char) (input.get(i) & 0xFF);
        }
        return output;
    }
}
