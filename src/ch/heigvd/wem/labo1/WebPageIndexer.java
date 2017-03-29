package ch.heigvd.wem.labo1;

import ch.heigvd.wem.data.Metadata;
import ch.heigvd.wem.interfaces.Index;
import ch.heigvd.wem.interfaces.Indexer;
import com.google.common.collect.Sets;

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
        index.getLinkTable().put(metadata.getDocID(), metadata.getUrl().toString());

        // For each document, store the words with its frequency (number of occurrences)
        Map<String, Long> frequencies = tokensFilter.stream().collect(Collectors.groupingBy(w -> w, counting()));

        // Get the maximal frequency (needed to calculate the weight by normalized frequency)
        Map.Entry<String, Long> maxFreq = frequencies.entrySet().stream().max(Map.Entry.comparingByValue(Long::compareTo)).get();

        // Calculation of weight by normalized frequency (w(t_i) = freq_i / max_j(freq_i))
        Map<String, Double> weightNorms = frequencies.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                        e -> (e.getValue() / (double)maxFreq.getValue())));

        // Creation of Weights objects (storing the frequency, normalized weight
        // and later the weight by td-idf normalized
        Map<String, Weights> weights = new HashMap<>();
        for(String key : Sets.union(weightNorms.keySet(), frequencies.keySet())) {
            weights.put(key, new Weights(frequencies.get(key), weightNorms.get(key), 0.0));
        }
        System.out.println(index.getLinkTable());

        // Creation of index (standard)
        // Put the Weights object, with the calculated frequency, into the index
        index.getIndex().put(metadata.getDocID(), weights);

        System.out.println(index.getIndex());

        // Creation of inverted Index
        // Each word contains a list of id (documents) in which it appears (with its frequency)
        weights.forEach(((word, weight) -> {
            index.getInvertedIndex().computeIfAbsent(word, k -> new HashMap<>()).put(metadata.getDocID(), weight);
        }));
        System.out.println(index.getInvertedIndex());
    }

    /**
     * Finalize the indexation
     *
     * Calculation of weigths
     */
    @Override
    public void finalizeIndexation() {
        // Get the maximal frequency (needed to calculate the weight by normalized frequency)
        //Map.Entry<String, Long> maxFreq = frequencies.entrySet().stream().max(Map.Entry.comparingByValue(Long::compareTo)).get();

        //// Calculation of weight by normalized frequency (w(t_i) = freq_i / max_j(freq_i))
        //Map<String, Double> weightNorms = frequencies.entrySet()
        //        .stream()
        //        .collect(Collectors.toMap(Map.Entry::getKey,
        //                e -> (double) (e.getValue() / maxFreq.getValue())));

        // Calculation of weight by tf-idf normalized


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
