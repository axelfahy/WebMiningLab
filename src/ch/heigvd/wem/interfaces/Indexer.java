package ch.heigvd.wem.interfaces;

import ch.heigvd.wem.data.Metadata;

/**
 * An object that aims to index the content of documents. The created index(es) will 
 * then probably be used by a {@link Retriever} to perform efficient searches.
 * @see Retriever
 */
public interface Indexer {

	/**
	 * Indexes a document. The content is given as a raw content and should first be 
	 * cleaned before indexation. Operations such as splitting into words, stop-words
	 * removal or stemming should be applied in this method.
	 * @param metadata metadata associated to the document.
	 * @param content raw text content of the document.
	 * @see Feeder#parseCollection(java.net.URI, Indexer)
	 */
	public void index(Metadata metadata, String content);
	
	/**
	 * Finalizes the indexation. This method is usually called by the {@link Feeder}, 
	 * once all the documents of the collection have been parsed.
	 */
	public void finalizeIndexation();
	
	/**
	 * Method returning the Index created by the indexer, called after <i>finalizeIndexation()</i>
	 * @return The index
	 */
	public Index getIndex();
	
}
