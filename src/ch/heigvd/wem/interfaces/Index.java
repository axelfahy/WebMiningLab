package ch.heigvd.wem.interfaces;

import ch.heigvd.wem.data.Metadata;
import ch.heigvd.wem.data.VisitedPage;
import ch.heigvd.wem.labo1.Weights;
import edu.uci.ics.crawler4j.url.WebURL;

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

    // Index: Map<DocID, Map<Word, (NbOccurrences, Weights)>>
    // For those indexes, a class Weights is used in order to save multiple values
    // and avoiding the creation of multiple indexes.
    protected Map<Long, Map<String, Weights>> index;
    // InvertedIndex: Map<Word, Map<DocID, (NbOccurrences, Weights)>>
    protected Map<String, Map<Long, Weights>> invertedIndex;
    // Correspondence table with id/pages
    protected Map<Long, String> linkTable;

    // Correspondence table with docid/metadata
    protected Map<Long, VisitedPage> metadataTable;

    // Correspondence table with url/docid
    protected Map<WebURL, Long> urlTable;

    public Map<Long, Map<String, Weights>> getIndex() {
        return index;
    }

    public void setIndex(Map<Long, Map<String, Weights>> index) {
        this.index = index;
    }

    public Map<String, Map<Long, Weights>> getInvertedIndex() {
        return invertedIndex;
    }

    public void setInvertedIndex(Map<String, Map<Long, Weights>> invertedIndex) {
        this.invertedIndex = invertedIndex;
    }

    public Map<Long, String> getLinkTable() {
        return linkTable;
    }

    public void setLinkTable(Map<Long, String> linkTable) {
        this.linkTable = linkTable;
    }

    public Map<Long, VisitedPage> getMetadataTable() {
        return metadataTable;
    }

    public void setMetadataTable(Map<Long, VisitedPage> metadataTable) {
        this.metadataTable = metadataTable;
    }

    public Map<WebURL, Long> getUrlTable() {
        return urlTable;
    }

    public void setUrlTable(Map<WebURL, Long> urlTable) {
        this.urlTable = urlTable;
    }
}
