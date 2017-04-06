package ch.heigvd.wem.tools;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Utility class
 *
 * Class used for tokenization, removing stop words, changing tokens to lowercase, ...
 */
public class Utils {

    // Path to stop words files
    private static final String PATH_STOP_WORD_EN = "common_words";
    private static final String PATH_STOP_WORD_FR = "common_words_fr";

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
        List<String> tokens = Arrays.asList(s.split("(?U)[^\\w']+"));

        // Remove apostrophe if at the end of a word
        for (int i = 0; i < tokens.size(); i++) {
            if (tokens.get(i).substring(tokens.get(i).length() - 1).equals("'")) {
                tokens.set(i, tokens.get(i).substring(0, tokens.get(i).length() - 1));
            }
        }
        return tokens;
    }

    /**
     * Remove stop words from a list of tokens
     *
     * Tokens are set to lowercase too.
     *
     * Stop words are taken from two stop words files (english and french)
     *
     * @param tokens tokens to remove the stop words from
     * @return a list of tokens without the removed stop words
     */
    public static List<String> removeStopWords(List<String> tokens) {
        // Load common words from both language (french and english)
        Set<String> stopEn = loadStopWordToSet(PATH_STOP_WORD_EN);
        Set<String> stopFr = loadStopWordToSet(PATH_STOP_WORD_FR);

        // Remove common words from tokens
        List<String> tokensFilter = tokens.stream()
                .map(String::toLowerCase)
                .filter(word -> !stopEn.contains(word.toLowerCase()) && !stopFr.contains(word.toLowerCase()))
                .collect(Collectors.toList());

        return tokensFilter;
    }


    /**
     * Loads a file of word stops into a set of string.
     *
     * Lines starting with '#' are considered as comment
     * and are filtered.
     *
     * @param path File with the word stops
     * @return A set of string containing the words
     */
    private static Set<String> loadStopWordToSet(String path) {
        Set<String> set = new HashSet<>();

        try (Stream<String> stream = Files.lines(Paths.get(path))) {
            set = stream.filter(line -> line.charAt(0) != '#')
                    .collect(Collectors.toSet());
        } catch (IOException e) {
            System.err.println("Error while reading file: " + path);
        }
        return set;
    }
}
