package huffman;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

/**
 *
 * @author shanecrumlish
 */
public class LZ77Encode {

    public ArrayList<Byte> lzEncode(String inputFile) {
        // Read in the input file
        String line;
        String input = "";
        boolean firstIteration = true;
        try(BufferedReader br = new BufferedReader(new FileReader(inputFile))) {
            while((line = br.readLine()) != null) {
                // Append new line chars after the first line
                if(!firstIteration) {
                    line = '\n' + line;
                } else {
                    firstIteration = false;
                }
                // input is a single string with all the lines of the input file seperated by newlines
                input += line;
            }
        } catch(Exception e) {
            System.out.println("Error: " + e);
        }

        ArrayList<Byte> output = new ArrayList<Byte>();
        String longestMatch = "";
        int saveWDist = 0;
        int saveI = 0;

        // Loop through the input to do encoding
        for(int i = 0; i < input.length(); i++) {
            String window = "";
            longestMatch = "";
            int windowDist = 1;
            int newI = 0;
            saveWDist = 0;
            boolean match = true;
            char tempMatch;

            // Set the first index of the window to be 1 back from our current character
            int windowIndex = i - 1;

            // TODO: Limit the size of the window by a parameter input from command line

            // Check each character of the window with characters from the current position going forward
            while(windowIndex >= 0) {

                // System.out.println("WindowIndex " + windowIndex);
                tempMatch = input.charAt(windowIndex);
                if(tempMatch == input.charAt(i)) {
                    window += tempMatch;
                    // j checks the match from the current location
                    int j = i + 1;
                    // wIndexCount checks the match from the beginning of the window
                    int wIndexCount = windowIndex + 1;

                    // j will hit the end first, so use that
                    while(match && j < input.length()) {
                        System.out.println("Char at J: " + input.charAt(j) + ", Other char: " + input.charAt(wIndexCount));
                        // Check the equivalence and add to the window
                        if(input.charAt(j) == input.charAt(wIndexCount)) {
                            window += input.charAt(wIndexCount);
                            j++;
                            wIndexCount++;
                            System.out.println("INSIDE:  J is " + input.charAt(j) + " other is " + input.charAt(wIndexCount));
                        } else {
                            match = false;
                        }
                    }
                    // store the end of the match in case it's the longest
                    newI = j;
                }
                System.out.println("WINDOW: " + window);

                // Check if this match is the longest match, and if so, save it and the position
                if(longestMatch.length() <= window.length()) {
                    longestMatch = window;
                    System.out.println("i: " + i + " windowIndex: " + windowIndex);

                    saveWDist = i - windowIndex; //+ windowIndex;
                    System.out.println("i2: " + i + " windowIndex2: " + windowIndex);

                    saveI = newI;
                    window = "";
                    match = true;
                }
                // Increment the distance from the processed character
                windowDist++;
                // move back one
                windowIndex--;
            }

            // Check to see there is a match

            // This is for the last match if the end of the input string is a match
            if(longestMatch.length() != 0 && saveI == input.length()) {
                // Update i to the end of the match
                i = saveI;
                // Add to the output in the format: starting index,lengh of string,character that doesn't match
                if(longestMatch.length() - 1 == 0) {
                    output.add((byte) 0);
                    output.add((byte) 0);
                    output.add((byte) input.charAt(input.length() - 1));
                } else {
                    output.add((byte) saveWDist);
                    output.add((byte) (longestMatch.length() - 1));
                    output.add((byte) input.charAt(input.length() - 1));
                }

                // For all other matches, go here.
            } else if(longestMatch.length() != 0) {
                // Update i to the end of the match
                i = saveI;
                // Add to the output in the format: starting index,lengh of string,character that doesn't match
                System.out.println("saveWDist: " + saveWDist);

                output.add((byte) saveWDist);
                output.add((byte) longestMatch.length());
                output.add((byte) input.charAt(i));
            } else {
                // If length is 0, make the distance also 0 for encoding
                saveWDist = 0;

                // Add to the output in the format: starting index,lengh of string,character that doesn't match
                System.out.println("SAVEWDISTTTTT" + saveWDist);
                output.add((byte) saveWDist);
                output.add((byte) longestMatch.length());
                output.add((byte) input.charAt(i));
            }

            // Add to the output in the format: starting index,lengh of string,character that doesn't match
            System.out.println("output: " + output);
        }
        return output;
    }
}
