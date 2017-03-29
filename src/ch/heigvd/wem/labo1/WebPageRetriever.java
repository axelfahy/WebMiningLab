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
    public Map<String, Double> searchDocument(Integer docmentId) {
        Map<String, Double> results = new HashMap<>();

        this.index.getIndex().get(docmentId).forEach((term, weight) -> {
            results.put(term, (double)weight.getFrequency());
        });
        return results;
    }

    @Override
    public Map<Long, Double> searchTerm(String term) {
        Map<Long, Double> results = new HashMap<>();

        this.index.getInvertedIndex().get(term).forEach((doc, weight) -> {
            results.put(doc, (double)weight.getFrequency());
        });
        return results;
    }

    @Override
    public Map<Long, Double> executeQuery(String query) {
        return null;
    }
}
