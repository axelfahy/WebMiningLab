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

import static java.util.stream.Collectors.counting;


/**
 *
 */
public class WebPageIndexer implements Indexer {

    private WebPageIndex index;
    // Path to stop words files
    private static final String PATH_STOP_WORD_EN = "common_words";
    private static final String PATH_STOP_WORD_FR = "common_words_fr";

    public WebPageIndexer() {
        this.index = new WebPageIndex();
    }

    @Override
    public void index(Metadata metadata, String content) {
        // Tokenization on space and punctuation except apostrophes
        List<String> tokens = Arrays.asList(content.split("[^\\w']+"));

        // Load common words from both language (french and english)
        Set<String> stopEn = loadStopWordToSet(PATH_STOP_WORD_EN);
        Set<String> stopFr = loadStopWordToSet(PATH_STOP_WORD_FR);

        // Remove common words from tokens
        List<String> tokensFilter = tokens.stream()
                .filter(word -> !stopEn.contains(word) && !stopFr.contains(word))
                .collect(Collectors.toList());

        // Add the docID with its corresponding url into the correspondence table
        index.linkTable.put(metadata.getDocID(), metadata.getUrl().toString());

        // Creation of index (standard)
        // For each document, store the words with its frequency (number of occurrences)
        Map<String, Long> frequencies = tokensFilter.stream().collect(Collectors.groupingBy(w -> w, counting()));
        frequencies.forEach((k, v) -> System.out.println("s="+k + ": " + v));

        Map.Entry<String, Long> maxFreq = frequencies.entrySet().stream().max(Map.Entry.comparingByValue(Long::compareTo)).get();

        frequencies.replaceAll((k, v) -> (k, v / maxFreq.getValue()));
        //Map<String, Double> weightNorms = frequencies.stream().map((k, v) -> (k, v / maxFreq));
        Map<String, Double> weightNorms = new HashMap<>();

        for (Map.Entry<String, Long> entry: frequencies.entrySet()) {
            weightNorms.put(entry.getKey(), entry.getValue() / maxFreq);
        }

        //index.index.put(metadata.getDocID(), frequencies);

        // Creation of inverted Index
        // Each word contains a list of id (documents) in which it appears (with its frequency)
        tokensFilter.forEach(word -> {
            if (!index.invertedIndex.containsKey(word)) {
                index.invertedIndex.put(word, new HashMap<>());
            }
            if (!index.invertedIndex.get(word).containsKey(metadata.getDocID())) {
                index.invertedIndex.get(word).put(metadata.getDocID(), 1);
            }
            else {
                index.invertedIndex.get(word).put(metadata.getDocID(),
                        index.invertedIndex.get(word).get(metadata.getDocID()) + 1);
            }
        });
        index.invertedIndex.forEach((k, v) -> System.out.println(k + " -> " + v));
        // TODO: probably a more elegant way to do this...
        //tokensFilter.forEach(word -> {
        //    index.invertedIndex.computeIfAbsent(word, k -> new HashMap<>()).add(metadata.getDocID(), )
        //}
        //});
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
