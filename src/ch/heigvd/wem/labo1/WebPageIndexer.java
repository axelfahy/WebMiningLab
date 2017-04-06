package ch.heigvd.wem.labo1;

import ch.heigvd.wem.data.Metadata;
import ch.heigvd.wem.interfaces.Index;
import ch.heigvd.wem.interfaces.Indexer;
import ch.heigvd.wem.tools.Utils;
import com.google.common.collect.Sets;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.counting;

/**
 * Class to index a website
 *
 * An index contains a normal index and an inverted index.
 * A link table is used to know which document is which web page.
 */
public class WebPageIndexer implements Indexer {

    private WebPageIndex index;

    public WebPageIndexer() {
        this.index = new WebPageIndex();
    }

    @Override
    public void index(Metadata metadata, String content) {
        // Tokenization on space and punctuation except apostrophes
        List<String> tokens = Utils.tokenize(content);

        List<String> tokensFilter = Utils.removeStopWords(tokens);

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

        // Creation of index (standard)
        // Put the Weights object, with the calculated frequency, into the index
        index.getIndex().put(metadata.getDocID(), weights);

        // Creation of inverted Index
        // Each word contains a list of id (documents) in which it appears (with its frequency)
        weights.forEach(((word, weight) -> {
            index.getInvertedIndex().computeIfAbsent(word, k -> new HashMap<>()).put(metadata.getDocID(), weight);
        }));
    }

    /**
     * Finalize the indexation
     *
     * We need to run two times over each word of a document.
     * The first time to calculate the tf-idfs and get the maximal value.
     * The second time to calculate the weight by tf-idf normalized.
     *
     * Calculation of weights
     */
    @Override
    public void finalizeIndexation() {
        // Calculation of weight by tf-idf normalized
        int nbWebPages = index.getLinkTable().size();

        index.getIndex().forEach((doc, words) -> {
            // Calculate the tf-idf for each word of the document
            Map<String, Double> tfidfMap = new HashMap<>();
            words.forEach((word, weights) -> {
                int n = index.getInvertedIndex().get(word).size();
                double tfidf = Math.log(weights.getFrequency() + 1) / Math.log(2) *
                        Math.log(nbWebPages / n) / Math.log(2);
                tfidfMap.put(word, tfidf);
            });

            // Get the maximal tf-idf
            Map.Entry<String, Double> maxTfidf = tfidfMap.entrySet().stream().max(Map.Entry.comparingByValue(Double::compareTo)).get();

            // Calculate the weight by tf-idf normalized
            words.forEach((word, weights) -> {
                double wTfidf = tfidfMap.get(word) / maxTfidf.getValue();

                // Update Weights in indexes
                // Normal index
                index.getIndex().get(doc).get(word).setWeightTfIdf(wTfidf);
                index.getInvertedIndex().get(word).get(doc).setWeightTfIdf(wTfidf);
            });
        });
    }

    @Override
    public Index getIndex() {
        return this.index;
    }
}
