package ch.heigvd.wem.labo1;

import ch.heigvd.wem.data.Metadata;
import ch.heigvd.wem.interfaces.Index;
import ch.heigvd.wem.interfaces.Indexer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 *
 */
public class WebPageIndexer implements Indexer {

    private Index index;
    // Path to stop words files
    private static final String PATH_STOP_WORD_EN = "common_words";
    private static final String PATH_STOP_WORD_FR = "common_words";

    public WebPageIndexer() {
        this.index = new WebPageSaver();
    }

    @Override
    public void index(Metadata metadata, String content) {
        List<String> tokens = Arrays.asList(content.split(" ")); // TODO: check for apostrophe: can't => "can't" vs parents' => "parents"

        // Load common words from both language (french and english)
        Set<String> stopEn = loadStopWordToSet(PATH_STOP_WORD_EN);
        Set<String> stopFr = loadStopWordToSet(PATH_STOP_WORD_FR);

        // Remove common words from tokens
        List<String> tokensFilter = tokens.stream()
                .filter(word -> !stopEn.contains(word) || !stopFr.contains(word))
                .collect(Collectors.toList());

        // TODO: Creation of index (normal)
        // For each document, store the words with its frequency

        // TODO: Creation of inverted Index
        // Each word contains a list of id (documents) in which it appears (with its frequency)
    }

    @Override
    public void finalizeIndexation() {

    }

    @Override
    public Index getIndex() {
        return this.index;
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
    private Set<String> loadStopWordToSet(String path) {
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
