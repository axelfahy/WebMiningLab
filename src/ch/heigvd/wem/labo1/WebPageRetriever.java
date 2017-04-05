package ch.heigvd.wem.labo1;

import ch.heigvd.wem.interfaces.Index;
import ch.heigvd.wem.interfaces.Retriever;
import ch.heigvd.wem.tools.Utils;

import java.util.*;

public class WebPageRetriever extends Retriever {

    public WebPageRetriever(Index index, WeightingType weightingType) {
        super(index, weightingType);
    }

    @Override
    public Map<String, Double> searchDocument(Integer documentId) {
        Map<String, Double> results = new HashMap<>();

        try {
            if (this.weightingType == WeightingType.NORMALIZED_FREQUENCY) {
                this.index.getIndex().get(new Long(documentId)).forEach((term, weight) -> {
                    results.put(term, weight.getWeightNorm());
                });
            }
            else if (this.weightingType == WeightingType.TF_IDF) {
                this.index.getIndex().get(new Long(documentId)).forEach((term, weight) -> {
                    results.put(term, weight.getWeightTfIdf());
                });
            }
        } catch (NullPointerException e) {
            System.out.println("No result");
        }
        return results;
    }

    @Override
    public Map<Long, Double> searchTerm(String term) {
        Map<Long, Double> results = new HashMap<>();

        try {
            if (this.weightingType == WeightingType.NORMALIZED_FREQUENCY) {
                this.index.getInvertedIndex().get(term).forEach((doc, weight) -> {
                    results.put(doc, weight.getWeightNorm());
                });
            }
            else if (this.weightingType == WeightingType.TF_IDF) {
                this.index.getInvertedIndex().get(term).forEach((doc, weight) -> {
                    results.put(doc, weight.getWeightTfIdf());
                });
            }
        } catch (NullPointerException e) {
            System.out.println("No result");
        }
        return results;
    }

    @Override
    public Map<Long, Double> executeQuery(String query) {
        // Tokenization on space and punctuation except apostrophes
        List<String> tokens = Utils.tokenize(query);

        Map<Long, Double> scores = new HashMap<>();
        Map<Long, Double> lengthsDoc = new HashMap<>();
        Double lengthsQuery = 0.0;

        // Calculate frequencies of terms from query
        Map<String, Long> frequencies = new HashMap<>();
        for (String token : tokens) {
            if (!frequencies.containsKey(token))
                frequencies.put(token, 0L);

            frequencies.put(token, frequencies.get(token) + 1);
            lengthsQuery += Math.pow(frequencies.get(token) ,2);
        }

        for (String token : tokens) {
            Long freq = frequencies.get(token);
            Map<Long, Weights> postings = this.index.getInvertedIndex().get(token);

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

        Set<Map.Entry<Long, Double>> set = scores.entrySet();
        List<Map.Entry<Long, Double>> list = new ArrayList<>(set);
        Collections.sort(list, new Comparator<Map.Entry<Long, Double>>() {
            public int compare(Map.Entry<Long, Double> o1,
                    Map.Entry<Long, Double> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
        });

        Map<Long, Double> results = new HashMap<>();
        for(Map.Entry<Long, Double> entry : list) {
            results.put(entry.getKey(), entry.getValue());
        }

        return results;
    }
}
