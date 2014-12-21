package huffman;

import java.util.ArrayList;

/**
 *
 * @author shanecrumlish
 */
public class LZ77Decode {

    public StringBuffer lzDecode(ArrayList<Byte> input) {

        // window contains already processed strings
        StringBuffer output = new StringBuffer();
        //ArrayList<Character> output = new ArrayList<Character>();

        int windowIndex;
        int charsLength;
        byte windowIndexString;
        byte charsLengthString;

        //System.out.println("SIZE " + input.size());

        for(int i = 0; i < input.size(); i++) {
            //  System.out.println("AT " + i);

            // System.out.println("window " + output);

            windowIndexString = 0;
            charsLengthString = 0;

            // Get the position backwards from the current position for patterns
            // System.out.println("input: " + input);
            windowIndexString = input.get(i);
            i++;

            //windowIndex = Integer.parseInt(windowIndexString & 0xff);
            windowIndex = windowIndexString & 0xff;
            //System.out.println(windowIndex);

            // Get the character length to process for patterns
            if(i < input.size()) {
                charsLengthString = input.get(i);
            }
            i++;

            //charsLength = Integer.parseInt(charsLengthString);

            charsLength = charsLengthString & 0xff;
            //System.out.println(charsLength);

            //System.out.println("window index " + windowIndex + " chars length " + charsLength);

            // Exclude any single entries
            if(charsLength != 0 && windowIndex != 0) {
                // Set position back from thed end of the window windowIndex chars
                int pos = output.length() - windowIndex;
                /*  System.out.println("outsize " + output.length());
                 System.out.println("WindowIndex " + windowIndex);
                 System.out.println("CL " + charsLength);*/

                // Go through go from position until the char length, adding to output as you go
                for(int j = pos; j < pos + charsLength; j++) {
                    // System.out.println("j inside0: " + j);
                    //System.out.println("max: " + pos + charsLength);
                    //System.out.println("j inside: " + output.charAt(j));
                    output.append(output.charAt(j));
                }
            }
            //  System.out.println(i);
            // At this point, the next character from input is a single char
            if(i < input.size()) {
                output.append((char) (input.get(i) & 0xFF));
            }
        }
        return output;
    }
}
