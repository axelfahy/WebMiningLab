package ch.heigvd.wem.labo1;

import ch.heigvd.wem.interfaces.Index;
import ch.heigvd.wem.interfaces.Retriever;

import java.util.HashMap;
import java.util.Map;

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
        return null;
    }
}
