package ch.heigvd.wem.labo1;

import ch.heigvd.wem.interfaces.Index;
import ch.heigvd.wem.interfaces.Retriever;
import ch.heigvd.wem.tools.Utils;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.counting;

public class WebPageRetriever extends Retriever {

    public WebPageRetriever(Index index, WeightingType weightingType) {
        super(index, weightingType);
    }

    /**
     * Search a document in an index
     *
     * Depending on the WeightingType, either the normalized frequency
     * or the tf-idf is returned
     *
     * @param documentId a document identifier.
     * @return a map containing the term with its weight
     */
    @Override
    public Map<String, Double> searchDocument(Integer documentId) {
        Map<String, Double> results = new HashMap<>();

        try {
            this.index.getIndex().get(new Long(documentId)).forEach((term, weight) -> {
                results.put(term, (this.weightingType == WeightingType.NORMALIZED_FREQUENCY) ?
                        weight.getWeightNorm() : weight.getWeightTfIdf());
            });
        } catch (NullPointerException e) {
            System.out.println("No result");
        }
        return results;
    }

    /**
     * Search a term in the index
     *
     * Depending on the WeightingType, either the normalized frequency
     * or the tf-idf is returned
     *
     * @param term a term.
     * @return a map containing the document and its weight
     */
    @Override
    public Map<Long, Double> searchTerm(String term) {
        Map<Long, Double> results = new HashMap<>();

        try {
            this.index.getInvertedIndex().get(term).forEach((doc, weight) -> {
                results.put(doc, (this.weightingType == WeightingType.NORMALIZED_FREQUENCY) ?
                        weight.getWeightNorm() : weight.getWeightTfIdf());
            });
        } catch (NullPointerException e) {
            System.out.println("No result");
        }
        return results;
    }

    /**
     * Execute a query
     *
     * The map returned is not sorted by value, since it is not possible to sort a HashMap by its values,
     * we would have to use another structure.
     * However, the sort can be done when printing the results.
     *
     * @param query a string query, containing a list of words.
     * @return a map containing the results with their corresponding scores
     */
    @Override
    public Map<Long, Double> executeQuery(String query) {
        // Tokenization on space and punctuation except apostrophes
        List<String> tokens = Utils.tokenize(query);

        // Set the token of the query to lowercase
        List<String> tokensLower = tokens.stream()
                .map(String::toLowerCase)
                .collect(Collectors.toList());

        Map<Long, Double> scores = new HashMap<>();
        Map<Long, Double> lengthsDoc = new HashMap<>();

        // Calculate frequencies of terms from query
        Map<String, Long> frequencies = tokensLower.stream().collect(Collectors.groupingBy(w -> w, counting()));
        // Calculate length of frequency
        Double lengthsQuery = frequencies.values().stream().map(v -> Math.pow(v, 2)).mapToDouble(Number::doubleValue).sum();

        for (String token : tokensLower) {
            Long freq = frequencies.get(token);
            Map<Long, Weights> postings = this.index.getInvertedIndex().get(token);

            // Check if term has any postings
            if (postings != null) {
                // Run over each document
                for (Map.Entry<Long, Weights> entry : postings.entrySet()) {
                    // Calculate the score
                    if (!scores.containsKey(entry.getKey()))
                        scores.put(entry.getKey(), 0.0);
                    scores.put(entry.getKey(), scores.get(entry.getKey()) + freq * entry.getValue().getFrequency());

                    // Calculate the length
                    if (!lengthsDoc.containsKey(entry.getKey()))
                        lengthsDoc.put(entry.getKey(), 0.0);
                    lengthsDoc.put(entry.getKey(), lengthsDoc.get(entry.getKey()) + Math.pow(entry.getValue().getFrequency(), 2));
                }
            }
        }
        // Run over each document and calculate the length
        for (Map.Entry<Long, Map<String, Weights>> entry : this.index.getIndex().entrySet()) {
            // Calculate the length
            if (!lengthsDoc.containsKey(entry.getKey()))
                lengthsDoc.put(entry.getKey(), 0.0);

            for (Map.Entry<String, Weights> entryTerm : entry.getValue().entrySet()) {
                lengthsDoc.put(entry.getKey(), lengthsDoc.get(entry.getKey()) + Math.pow(entryTerm.getValue().getFrequency(), 2));
            }
        }

        // Calculate the final lengths
        for (Map.Entry<Long, Double> entry : lengthsDoc.entrySet()) {
            lengthsDoc.put(entry.getKey(), Math.sqrt(lengthsQuery * entry.getValue()));
        }

        // Calculate the final score (score[d] / length[d])
        for (Map.Entry<Long, Double> entry : scores.entrySet()) {
            scores.put(entry.getKey(), entry.getValue() / lengthsDoc.get(entry.getKey()));
        }

        return scores;
    }
}
