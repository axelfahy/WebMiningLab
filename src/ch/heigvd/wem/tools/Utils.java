package ch.heigvd.wem.tools;

import java.util.Arrays;
import java.util.List;

public class Utils {

    /**
     * Tokenization on space and punctuation except apostrophes
     *
     * If an apostrophe is at the end of a word, removes it.
     * If it is in the middle of a word, keep it.
     *
     * @param s String to tokenize
     * @return List of tokens (String)
     */
    public static List<String> tokenize(String s) {
        List<String> tokens = Arrays.asList(s.split("[^\\w']+"));

        // Remove apostrophe if at the end of a word
        for (int i = 0; i < tokens.size(); i++) {
            if (tokens.get(i).substring(tokens.get(i).length() - 1).equals("'")) {
                tokens.set(i, tokens.get(i).substring(0, tokens.get(i).length() - 1));
            }
        }
        return tokens;
    }
}
