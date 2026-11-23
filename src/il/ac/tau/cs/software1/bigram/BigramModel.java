package il.ac.tau.cs.software1.bigram;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Scanner;
import java.io.IOException;
import java.util.Locale;

public class BigramModel {
    public static final int MAX_VOCABULARY_SIZE = 14500;
    public static final String VOC_FILE_SUFFIX = ".voc";
    public static final String COUNTS_FILE_SUFFIX = ".counts";
    public static final String SOME_NUM = "some_num";
    public static final int ELEMENT_NOT_FOUND = -1;

    String[] mVocabulary;
    int[][] mBigramCounts;

    // DO NOT CHANGE THIS !!!
    public void initModel(String fileName) throws IOException {
        mVocabulary = buildVocabularyIndex(fileName);
        mBigramCounts = buildCountsArray(fileName, mVocabulary);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Helper methods - already implemented for you. Use them!
    ///////////////////////////////////////////////////////////////////////////

    /*
     * Converts a single token (a whitespace-free piece of text from the file)
     * into the form in which it is stored in the vocabulary:
     *   - a token with at least one English letter  -> the token in lowercase
     *   - a token made only of digits (an integer)  -> SOME_NUM
     *   - anything else is not a legal word         -> null
     *
     * @pre: token != null
     */
    public static String normalizeWord(String token) {
        boolean hasLetter = false;
        boolean allDigits = !token.isEmpty();
        for (int i = 0; i < token.length(); i++) {
            char c = token.charAt(i);
            if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')) {
                hasLetter = true;
            }
            if (c < '0' || c > '9') {
                allDigits = false;
            }
        }
        if (hasLetter) {
            return token.toLowerCase(Locale.ROOT);
        }
        if (allDigits) {
            return SOME_NUM;
        }
        return null;
    }

    /*
     * @pre: vocabulary != null
     * @post: $ret = the index of word in vocabulary, or ELEMENT_NOT_FOUND if
     * word is null or does not appear in it
     */
    public static int findIndex(String[] vocabulary, String word) {
        if (word == null) {
            return ELEMENT_NOT_FOUND;
        }
        for (int i = 0; i < vocabulary.length; i++) {
            if (word.equals(vocabulary[i])) {
                return i;
            }
        }
        return ELEMENT_NOT_FOUND;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Questions 1 - 5: implement these
    ///////////////////////////////////////////////////////////////////////////

    /*
     * @post: mVocabulary = prev(mVocabulary)
     * @post: mBigramCounts = prev(mBigramCounts)
     */
    public String[] buildVocabularyIndex(String fileName) throws IOException { // Question 1
        String[] curr_voc = new String[MAX_VOCABULARY_SIZE];
        int curr_voc_length = 0;
        // Establishing stream to read the file, word by word
        File fromFile = new File(fileName);
        Scanner words = new Scanner(new FileReader(fromFile));
        String curr_word;
        while (curr_voc.length < MAX_VOCABULARY_SIZE && words.hasNext())
        {
            // Normalizing the word and checking if it is already in the vocabulary
            curr_word = normalizeWord(words.next());
            if (curr_word != null && findIndex(curr_voc, curr_word) == ELEMENT_NOT_FOUND)
            {
                curr_voc[curr_voc_length] = curr_word;
                curr_voc_length++;
            }
        }
        words.close();
        // Copying the vocabulary to a new array of the correct size, and returning it
        String[] final_voc = new String[curr_voc_length];
        for (int i = 0; i < curr_voc_length; i++)
            final_voc[i] = curr_voc[i];
        if (final_voc.length != 0)
            {return final_voc;}
        else
            {return null;}
    }

    /*
     * @post: mVocabulary = prev(mVocabulary)
     * @post: mBigramCounts = prev(mBigramCounts)
     */
    public int[][] buildCountsArray(String fileName, String[] vocabulary) throws IOException { // Question 2
        if (vocabulary.length == 0)
            {return null;}
        // Establishing stream to read the file, line by line
        File fromFile = new File(fileName);
        BufferedReader bufferedReader = new BufferedReader(new FileReader(fromFile));
        int[][] bigram_counts = new int[vocabulary.length][vocabulary.length];
        // Initializing the bigram_counts array to 0
        for (int i = 0; i < vocabulary.length; i++)
        {
            for (int j = 0; j < vocabulary.length; j++)
            {
                bigram_counts[i][j] = 0;
            }
        }
        String curr_word;
        String next_word;
        String line;
        String[] current_line;
        while ((line = bufferedReader.readLine()) != null)
        {
            // Splitting the line into words, and skipping it if it has less than 2 words
            current_line = line.trim().split("\\s+");
            if (current_line.length < 2)
            {
                continue;
            }
            for (int i = 0; i < current_line.length - 1; i++)
            {
                // Normalizing the current word and the next word, and checking if they are in the vocabulary
                curr_word = normalizeWord(current_line[i]);
                next_word = normalizeWord(current_line[i + 1]);
                if (curr_word != null && next_word != null)
                {
                    int curr_word_index = findIndex(vocabulary, curr_word);
                    int next_word_index = findIndex(vocabulary, next_word);
                    if (curr_word_index != ELEMENT_NOT_FOUND && next_word_index != ELEMENT_NOT_FOUND)
                    {
                        bigram_counts[curr_word_index][next_word_index]++;
                    }
                }
                
            }
        }
        bufferedReader.close();
        return bigram_counts;
    }

    /*
     * @pre: the method initModel was called (the language model is initialized)
     * @pre: fileName is a legal file path
     */
    public void saveModel(String fileName) throws IOException { // Question 3
        File voc_file = new File(fileName + VOC_FILE_SUFFIX);
        File counts_file = new File(fileName + COUNTS_FILE_SUFFIX);
        voc_file.createNewFile();
        counts_file.createNewFile();
        // Establishing stream to write the vocabulary to the file
        BufferedWriter voc_writer = new BufferedWriter(new FileWriter(voc_file));
        voc_writer.write(mVocabulary.length + " words" + "\r\n");
        for (int i = 0; i < mVocabulary.length; i++)
        {
            voc_writer.write(i + ", " + mVocabulary[i] + "\r\n");
        }
        voc_writer.write("\r\n");
        voc_writer.close();
        // Establishing stream to write the bigram counts to the file
        BufferedWriter counts_writer = new BufferedWriter(new FileWriter(counts_file));
        for (int i = 0; i < mBigramCounts.length; i++)
        {
            for (int j = 0; j < mBigramCounts[i].length; j++)
            {
                if (mBigramCounts[i][j] > 0)
                {
                    counts_writer.write(i + ", " + j + ": " + mBigramCounts[i][j] + "\r\n");
                }
            }
            counts_writer.write("\r\n");
        }
        counts_writer.close();
    }

    /*
     * @pre: fileName is a legal file path
     */
    public void loadModel(String fileName) throws IOException { // Question 4
        File voc_file = new File(fileName + VOC_FILE_SUFFIX);
        File counts_file = new File(fileName + COUNTS_FILE_SUFFIX);
        // Establishing stream to read the vocabulary from the file
        BufferedReader voc_reader = new BufferedReader(new FileReader(voc_file));
        String line;
        line = voc_reader.readLine();
        // Getting the vocabulary length from the first line of the file and initializing the vocabulary array
        int voc_length = Integer.parseInt(line.split(" ")[0]);
        mVocabulary = new String[voc_length];
        for (int i = 0; i < voc_length; i++)
        {
            // Reading the next line and getting the word from it
            line = voc_reader.readLine();
            mVocabulary[i] = line.split(", ")[1];
        }
        voc_reader.close();
        // Establishing stream to read the bigram counts from the file
        BufferedReader counts_reader = new BufferedReader(new FileReader(counts_file));
        // Initializing the bigram counts array
        mBigramCounts = new int[voc_length][voc_length];
        while ((line = counts_reader.readLine()) != null)
        {
            // Skipping empty lines
            if (line.trim().isEmpty())
            {
                continue;
            }
            // Splitting the line into parts and getting the indices and count from it
            String[] parts = line.split(": ");
            String[] indices = parts[0].split(", ");
            int i = Integer.parseInt(indices[0]);
            int j = Integer.parseInt(indices[1]);
            int count = Integer.parseInt(parts[1]);
            mBigramCounts[i][j] = count;
        }
        counts_reader.close();

        
    }

    /*
     * @pre: sentence is in lowercase
     * @pre: the method initModel was called (the language model is initialized)
     * @pre: each two words in the sentence are separated with a single space
     * @post: if sentence is probable, according to the model, $ret = true, else, $ret = false
     */
    public boolean isLegalSentence(String sentence) { // Question 5
        if (sentence.isEmpty())
        {
            return true;
        }
        String[] words = sentence.split(" ");
        String curr_word;
        String next_word;
        int curr_index;
        int next_index;
        for(int i = 0; i < words.length - 1; i++)
        {    
            curr_word = words[i];
            next_word = words[i+1];
            curr_index = findIndex(mVocabulary, curr_word);
            next_index = findIndex(mVocabulary, next_word);

            if (curr_index == ELEMENT_NOT_FOUND || next_index == ELEMENT_NOT_FOUND)
            {
                return false;
            }
            if (mBigramCounts[curr_index][next_index] == 0)
            {
                return false;
            }
            

        }
        return true;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Queries on the model - already implemented for you.
    // They start working once Questions 1 - 2 are done.
    ///////////////////////////////////////////////////////////////////////////

    /*
     * @pre: word is in lowercase
     * @pre: the method initModel was called (the language model is initialized)
     * @post: $ret = -1 if word is not in vocabulary, otherwise $ret = the index of word in vocabulary
     */
    public int getWordIndex(String word) {
        return findIndex(mVocabulary, word);
    }

    /*
     * @pre: word1, word2 are in lowercase
     * @pre: the method initModel was called (the language model is initialized)
     * @post: $ret = the count for the bigram <word1, word2>. if one of the words does not
     * exist in the vocabulary, $ret = 0
     */
    public int getBigramCount(String word1, String word2) {
        int index1 = getWordIndex(word1);
        int index2 = getWordIndex(word2);
        if (index1 == ELEMENT_NOT_FOUND || index2 == ELEMENT_NOT_FOUND) {
            return 0;
        }
        return mBigramCounts[index1][index2];
    }

    /*
     * @pre: word is in lowercase, and is in mVocabulary
     * @pre: the method initModel was called (the language model is initialized)
     * @post: $ret = the word with the lowest vocabulary index that appears most frequently after word
     * (if a bigram starting with word was never seen, $ret will be null)
     */
    public String getMostFrequentProceeding(String word) {
        int[] row = mBigramCounts[getWordIndex(word)];
        int best = ELEMENT_NOT_FOUND;
        for (int i = 0; i < row.length; i++) {
            if (row[i] > 0 && (best == ELEMENT_NOT_FOUND || row[i] > row[best])) {
                best = i;
            }
        }
        return best == ELEMENT_NOT_FOUND ? null : mVocabulary[best];
    }

    /*
     * @pre: arr1.length = arr2.length
     * @post: if arr1 or arr2 are only filled with zeros, $ret = -1, otherwise calculates CosineSim
     */
    public static double calcCosineSim(int[] arr1, int[] arr2) {
        double dot = 0;
        double norm1 = 0;
        double norm2 = 0;
        for (int i = 0; i < arr1.length; i++) {
            dot += (double) arr1[i] * arr2[i];
            norm1 += (double) arr1[i] * arr1[i];
            norm2 += (double) arr2[i] * arr2[i];
        }
        if (norm1 == 0 || norm2 == 0) {
            return -1;
        }
        return dot / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }

    /*
     * @pre: word is in vocabulary
     * @pre: the method initModel was called (the language model is initialized)
     * @post: $ret = w implies that w is the word with the largest cosineSimilarity(vector for word, vector for w)
     * among all the other words in vocabulary (the lowest index wins a tie)
     */
    public String getClosestWord(String word) {
        int wordIndex = getWordIndex(word);
        int best = ELEMENT_NOT_FOUND;
        double bestSim = 0;
        for (int i = 0; i < mVocabulary.length; i++) {
            if (i == wordIndex) {
                continue;
            }
            double sim = calcCosineSim(mBigramCounts[wordIndex], mBigramCounts[i]);
            if (best == ELEMENT_NOT_FOUND || sim > bestSim) {
                best = i;
                bestSim = sim;
            }
        }
        // A vocabulary with a single word: the closest word is the word itself
        return best == ELEMENT_NOT_FOUND ? word : mVocabulary[best];
    }
}
