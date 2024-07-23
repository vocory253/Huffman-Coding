// Cory Vo
// TA: Niyati Trivedi
// CSE 123
// 2-26-2024
// P3: Huffman

import java.util.*;
import java.io.*;

// Class Description: HuffmanCode compresses and decompresses data based on the Huffman algorithm.
public class HuffmanCode {
    
    private Queue<HuffmanNode> pq;
    private HuffmanNode overallRoot;

    // Behavior: Takes in the total amount of different characters that appear in some data and
    // creates new bit pairings in accordance to the Huffman algorithm in order to use less space.
    // Whats ends up happening is that characters that appear more often will now require less
    // data to encode and vice versa.
    // Parameter: int[] frequencies tells us how many times different characters are present.
    public HuffmanCode(int[] frequencies){
        pq = new PriorityQueue<HuffmanNode>();
        populate(frequencies);
        overallRoot = huffmanFrequencyHelper(overallRoot);
    }

    // Behavior: Takes care of organizing the information of how often different characters appear
    // so we can begin implementing the Huffman algorithm, where characters that appear more often
    // will now require less data to encode.
    // Parameter: int[] frequencies tells us how many times different characters are present.
    private void populate(int[] frequencies){
        for(int i = 0; i < frequencies.length; i++){
            if(frequencies[i] > 0){
                pq.add(new HuffmanNode((char) i + "/" + frequencies[i]));
            }
        }
    }

    // Behavior: Takes the organized frequency values and begins to use the Huffman algorithm.
    // It now creates new bit values for the different characters in the original data.
    // Return: Gives back the newly found bit values of the different characters.
    // Parameters: HuffmanNode root represents the piece of data we are currently at.
    private HuffmanNode huffmanFrequencyHelper(HuffmanNode root){
        while(pq.size() != 1){
            HuffmanNode left = pq.remove();
            HuffmanNode right = pq.remove();
            String[] split1 = left.data.split("/");
            int otherNumber = Integer.parseInt(split1[1]);
            String[] split2 = right.data.split("/");
            int thisNumber = Integer.parseInt(split2[1]);
            root = new HuffmanNode(" /" + (String.valueOf(otherNumber + thisNumber)), left, right);
            pq.add(root);
        }
        return root;
    }

    // Behavior: Takes information from the input and creates the same HuffmanCode that is present
    // in the input. It should be in standard format where all the lines are in pairs. The first
    // line is the ascii value for a character and the second line contains its encoding value.
    // Parameter: Scanner input represents the file we want to take information from.
    public HuffmanCode(Scanner input){
        pq = null;
        while(input.hasNextLine()){
            int asciiValue = Integer.parseInt(input.nextLine());
            String traversal = input.nextLine();
            overallRoot = huffmanScannerHelper(overallRoot, asciiValue, traversal);
        }
    }

    // Behavior: Takes information from the input and creates the same HuffmanCode that is present
    // in the input. It should be in standard format where all the lines are in pairs. The first
    // line is the ascii value for a character and the second line contains its encoding value.
    // Return: Gives back all of the data that contains the different encoding values for the
    // different characters.
    // Parameters: HuffmanNode root represents the piece of data we are currently at.
    // int asciiValue is the current value we are looking to create an encoding for. 
    // String traversal represents the progress we have made in finding an encoding for the current
    // ascii value.
    private HuffmanNode huffmanScannerHelper(HuffmanNode root, int asciiValue,
                                                 String traversal){
        if(traversal.equals("")){
            return new HuffmanNode(((char) asciiValue) + "/ ");
        } 
        else {
            if(root == null){
                root = new HuffmanNode("0"); 
            }
            if(traversal.charAt(0) == '0'){
                root = new HuffmanNode("0", huffmanScannerHelper(root.left, asciiValue, 
                                    traversal.substring(1)), root.right);
            } else {
                root = new HuffmanNode("0", root.left, huffmanScannerHelper(root.right, asciiValue,
                                    traversal.substring(1))); 
            }
        }
        return root;
    }

    // Behavior: Takes the current HuffmanCode we have created and prints its contents out to a 
    // file. It should be in standard format where all the lines are in pairs. The first line is
    // the ascii value for a character and the second line contains its encoding value.
    // Parameter: PrintStream output represents the file we want to print the information out too.
    public void save(PrintStream output){
        save(output, overallRoot, "");
    }
    
    // Behavior: Takes the current HuffmanCode we have created and prints its contents out to a 
    // file. It should be in standard format where all the lines are in pairs. The first line is
    // the ascii value for a character and the second line contains its encoding value.
    // Parameters: PrintStream output represents the file we want to print the information out too.
    // HuffmanNode root represents the piece of data we are currently at. String soFar represents
    // the progress that has been made in printing out the encoding values.
    private void save(PrintStream output, HuffmanNode root, String soFar){
        if(root != null){
            // we are at a leaf node (last thing to print then pop back up)
            if(root.left == null && root.right == null){
                String[] split = root.data.split("/");
                Character temp = split[0].charAt(0);
                output.println((int) temp);
                output.println(soFar);
            }
            // traverse and add to soFar
            else {
                save(output, root.left, soFar + "0");
                save(output, root.right, soFar + "1");
            }
        }
    }

    // Behavior: Takes in a bit encoding and decodes it, printing out its decoded message in the 
    // provided output file.
    // Parameters: BitInputStream input represents the encoding we have been given. 
    // PrintStream output represents the file we want to print out the decoded message too.
    public void translate(BitInputStream input, PrintStream output){
        while(input.hasNextBit()){
            translate(input, output, overallRoot);
        }
    }

    // Behavior: Takes in a bit encoding and decodes it, printing out its decoded message in the 
    // provided output file.
    // Parameters: BitInputStream input represents the encoding we have been given. 
    // PrintStream output represents the file we want to print out the decoded message too.
    // HuffmanNode root represents the piece of data we are currently at.
    private void translate(BitInputStream input, PrintStream output, HuffmanNode root){
        if(root != null){
            if(root.left == null && root.right == null){
                String[] split = root.data.split("/");
                Character temp = split[0].charAt(0);
                output.write((int) temp);
            }
            else { // read the bits
                int curr = input.nextBit();
                if(curr == 0){ // 0, so go left
                    translate(input, output, root.left);
                }
                else if(curr == 1){ // 1, so go right
                    translate(input, output, root.right);
                }
            }
        }
    }

    // Class Description: HuffmanNode represents an individual piece of data within the Huffman
    // coding algorithm. It also implements Comparable<HuffmanNode>.
    private static class HuffmanNode implements Comparable<HuffmanNode>{

        public final String data;
        public final HuffmanNode left;
        public final HuffmanNode right;

        // Behavior: Creates a HuffmanNode with the inputed data and HuffmanNodes.
        // Parameter: String data represents the piece of data you want a HuffmanNode to contain.
        public HuffmanNode(String data){
            this(data, null, null);
        }

        // Behavior: Creates a HuffmanNode with the inputed data and HuffmanNodes.
        // Parameters: String data represents the piece of data you want a HuffmanNode to contain.
        // HuffmanNode left and HuffmanNode right would be the HuffmanNodes that you want to be
        // associated with the current HuffmanNode.
        public HuffmanNode(String data, HuffmanNode left, HuffmanNode right){
            this.data = data;
            this.left = left;
            this.right = right;
        }

        // Behavior: Compares HuffmanNodes to see which one is greater. Greater is determined by
        // which HuffmanNode contains the higher frequency.
        // Return: Gives back 1 if the current HuffmanNode is greater. Gives back -1 if the other
        // HuffmanNode is greater, and gives back 0 if they are equal.
        // Parameter: HuffmanNode other is the HuffmanNode you want to compare to the current one.
        public int compareTo(HuffmanNode other){
            String[] split1 = other.data.split("/");
            int otherNumber = Integer.parseInt(split1[1]);
            String[] split2 = this.data.split("/");
            int thisNumber = Integer.parseInt(split2[1]);
            return (thisNumber - otherNumber);
        }
    }
}