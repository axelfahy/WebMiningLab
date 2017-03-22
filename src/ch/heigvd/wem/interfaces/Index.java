package ch.heigvd.wem.interfaces;

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

    public Map<String, Map<Long, Integer>> index;
    public Map<String, Map<Long, Integer>> invertedIndex;

    // Same for ponderations ?

    // For each document, we want to store the words and their corresponding frequency
    // Ex: doc1: { ('Apple', 2), ('Orange', 1), ...
    //     doc2: { ('The', 24), ...
    // Map of map ? Map([Doc], Map([Word] => Freq))


    // Inverted index:
    // 'The': {(doc1, 3), (doc2, 4), (doc4, 1)}
    // 'cow': {doc2}
    // 'says': {doc4, doc5}
    // Map of map ? Map([Word] => Map([Doc], freq))
}
