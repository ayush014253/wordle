package wordle;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static wordle.Huffman.encodeWord;

public class Wordle {

    String fileName = "wordle/resources/dictionary.txt";
    //String fileName = "wordle/resources/extended-dictionary.txt";
    List<String> dictionary = null;
    List<String> guesswords = null;
    final int num_guesses = 5;
    final long seed = 42;
    //Random rand = new Random(seed);
    Random rand = new Random();

    static final String winMessage = "CONGRATULATIONS! YOU WON! :)";
    static final String lostMessage = "YOU LOST :( THE WORD CHOSEN BY THE GAME IS: ";
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_GREEN_BACKGROUND = "\u001B[42m";
    public static final String ANSI_YELLOW_BACKGROUND = "\u001B[43m";
    public static final String ANSI_GREY_BACKGROUND = "\u001B[100m";

    Wordle() throws IOException {

        this.dictionary = readDictionary(fileName);

        System.out.println("dict length: " + this.dictionary.size());
        System.out.println("dict: " + dictionary);
        System.out.println("dict: size without huffman is " + this.dictionary.size()*8*5);
        int afterhuffman=0;
        Huffman huff = new Huffman();
        Huffman.Node root=huff.readInput();
        for(String word:dictionary){
            String encodedWord = encodeWord(word, root);
            afterhuffman=afterhuffman+encodedWord.length();
        }
        System.out.println("dict: size with huffman is " + afterhuffman);
        double ratio= (double) afterhuffman /(this.dictionary.size()*8*5);
        System.out.println("the ratio is " + ratio*100);
    }

    public static void main(String[] args) throws IOException {
        Wordle game = new Wordle();

        String target = game.getRandomTargetWord();

        System.out.println("target: " + target);


        game.play(target);

    }

    public void play(String target) throws IOException {
        // TODO
        // TODO: You have to fill in the code
        Huffman huff = new Huffman();
        Huffman.Node root=huff.readInput();
        Map<String,Integer> wordFreqMap = huff.getWordFrequency(dictionary);
        for(int i = 0; i < num_guesses; ++i) {
            if(i!=0){

               wordFreqMap = huff.getWordFrequency(guesswords);
               System.out.println("guesshint length: " + this.guesswords.size());
               System.out.println("guesshint " + guesswords);
                int maxValue = Integer.MIN_VALUE;
                String maxKey = null;
                for(Map.Entry<String,Integer> entry : wordFreqMap.entrySet()){
                    if(entry.getValue()>maxValue){
                        maxValue = entry.getValue();
                        maxKey = entry.getKey();
                    }
                }
                System.out.println("Your next guess should be: " + maxKey);

            }

            String guess = getGuess();

            if(guess == target) { // you won!
                win(target);
                return;
            }

            // the hint is a string where green="+", yellow="o", grey="_"
            // didn't win ;(
            String [] hint = {"_", "_", "_", "_", "_"};
            String modifiedTarget = target;
            int c=0;
            for (int k = 0; k < target.length(); k++) {
                // TODO:
                if (hint[k].equals("+")) {
                    continue;
                }

                // Check if the letter at index k in guessWord matches the letter at index k in targetWord
                if (guess.charAt(k) == target.charAt(k)) {
                    hint[k] = "+"; // Mark it as guessed correctly (green)
                    modifiedTarget = modifiedTarget.substring(0, k-c) + modifiedTarget.substring(k-c+ 1);
                    c=c+1;



                }
            }
            for (int k = 0; k < target.length(); k++) {
                // TODO:

                if (hint[k].equals("+")) {
                    continue;
                }
                else if (modifiedTarget.contains(String.valueOf(guess.charAt(k)))) {

                    hint[k] = "o"; // Mark it as present but not in the right place (yellow)
                    modifiedTarget = removeFirstChar(modifiedTarget, guess.charAt(k));



                } else {
                    hint[k] = "_"; // Mark it as not present (grey)
                }
            }

            // set the arrays for yellow (present but not in right place), grey (not present)
            // loop over each entry:
            //  if hint == "+" (green) skip it
            //  else check if the letter is present in the target word. If yes, set to "o" (yellow)
            for (int k = 0; k < 5; k++) {
                // TODO:
                System.out.print(hint[k]+ " ");

            }

            // after setting the yellow and green positions, the remaining hint positions must be "not present" or "_"
            System.out.println("hint: " + Arrays.toString(hint));


            // check for a win
            int num_green = 0;
            for(int k = 0; k < 5; ++k) {
                if(hint[k] == "+") num_green += 1;
            }
            if(num_green == 5) {
                 win(target);
                 return;
            }
        if(i==0){
            guesswords=filterWords( dictionary,guess, hint);
        }else{
            guesswords=filterWords( guesswords,guess, hint);
       }
        }

        lost(target);
    }
    private static String removeFirstChar(String str, char charToRemove) {
        int index = str.indexOf(charToRemove);
        if (index != -1) { // Character found
            return str.substring(0, index) + str.substring(index + 1);
        } else {
            return str; // Character not found, return original string
        }
    }
    public List<String> filterWords( List<String> wordList,String guess,String[] hints) {
        List<String> filteredword=new ArrayList<>();
        for(String word : wordList){
            boolean c=true;
            String modifiedTarget = word;
            int a=0;

            for (int k = 0; k < word.length(); k++) {
                // TODO:
                if (hints[k].equals("+")) {

                    // Check if the letter at index k in guessWord matches the letter at index k in targetWord
                    if (guess.charAt(k) == word.charAt(k)) {


                        modifiedTarget = modifiedTarget.substring(0, k-a) + modifiedTarget.substring(k-a+ 1);
                        a=a+1;


                    }else{
                        c=false;
                        break;
                    }
                }
            }
            if(c==true){
                for (int k = 0; k < word.length(); k++) {
                    // TODO:
                    if (hints[k].equals("+")) {
                        continue;
                    }

                    // Check if the letter at index k in guessWord matches the letter at index k in targetWord
                    else if (hints[k].equals("o")) {
                        if (modifiedTarget.contains(String.valueOf(guess.charAt(k)))) {

                            modifiedTarget = removeFirstChar(modifiedTarget, guess.charAt(k));

                        } else {
                            c=false;
                            break;
                        }

                    } else{
                        if (modifiedTarget.contains(String.valueOf(guess.charAt(k)))) {

                            c=false;
                            break;

                        }
                    }
                }

            }

            if(c==true){
                filteredword.add(word);
            }
        }
        return filteredword;
    }
    public void lost(String target) {
        System.out.println();
        System.out.println(lostMessage + target.toUpperCase() + ".");
        System.out.println();

    }
    public void win(String target) {
        System.out.println(ANSI_GREEN_BACKGROUND + target.toUpperCase() + ANSI_RESET);
        System.out.println();
        System.out.println(winMessage);
        System.out.println();
    }

    public String getGuess() {
        Scanner myScanner = new Scanner(System.in, StandardCharsets.UTF_8.displayName());  // Create a Scanner object
        System.out.println("Guess:");

        String userWord = myScanner.nextLine();  // Read user input
        userWord = userWord.toLowerCase(); // covert to lowercase

        // check the length of the word and if it exists
        while ((userWord.length() != 5) || !(dictionary.contains(userWord))) {
            if ((userWord.length() != 5)) {
                System.out.println("The word " + userWord + " does not have 5 letters.");
            } else {
                System.out.println("The word " + userWord + " is not in the word list.");
            }
            // Ask for a new word
            System.out.println("Please enter a new 5-letter word.");
            userWord = myScanner.nextLine();
        }
        return userWord;
    }

    public String getRandomTargetWord() {
        // generate random values from 0 to dictionary size
        return dictionary.get(rand.nextInt(dictionary.size()));
    }
    public List<String> readDictionary(String fileName) {
        List<String> wordList = new ArrayList<>();

        try {
            // Open and read the dictionary file
            InputStream in = this.getClass().getClassLoader().getResourceAsStream(fileName);
            assert in != null;
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String strLine;

            //Read file line By line
            while ((strLine = reader.readLine()) != null) {
                wordList.add(strLine.toLowerCase());
            }
            //Close the input stream
            in.close();

        } catch (Exception e) {//Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }
        return wordList;
    }
}
