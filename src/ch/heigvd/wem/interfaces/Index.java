package ch.heigvd.wem.interfaces;

import ch.heigvd.wem.labo1.Weights;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;

/**
 * A dummy class representing the index
 * You should extend it for your implementation
 * This class is Serializable, it will allow you to save it on disk
 */
public abstract class Index implements Serializable {

    private static final long serialVersionUID = -7032327683456713025L;

    // Index: Map<DocID, Map<Word, NbOccurrences>>
    public Map<Long, Map<String, Weights>> index;
    // InvertedIndex: Map<Word, Map<DocID, NbOccurrences>>
    public Map<String, Map<Long, Weights>> invertedIndex;
    // Index: Map<DocID, Map<Word, NormalizedFreq>>
    //public Map<Long, Map<String, >> ponderation;
    // InvertedIndex: Map<Word, Map<DocID, NbOccurrences>>
    //public Map<String, Map<Long, Double>> invertedPonderation;
    public Map<Long, String> linkTable;

}
