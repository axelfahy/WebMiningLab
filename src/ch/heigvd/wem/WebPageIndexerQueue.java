package ch.heigvd.wem;

import java.util.concurrent.ConcurrentLinkedQueue;

import ch.heigvd.wem.data.Metadata;
import ch.heigvd.wem.data.VisitedPage;
import ch.heigvd.wem.interfaces.Indexer;
import ch.heigvd.wem.labo1.Labo1;

/**
 * Class representing an active queue
 * This queue is filled by the crawlers Threads asynchronously
 * The queue then call the indexer for each element, one after the other
 * @author fabien.dutoit
 */
public class WebPageIndexerQueue extends Thread {

	private ConcurrentLinkedQueue<VisitedPage> queueToIndex = null;
	private boolean allDone = false;
	private Indexer indexer = null;
	
	/**
	 * WebPageIndexerQueue constructor
	 * @param indexer The indexer implementation that will be called by visited queued pages
	 */
	public WebPageIndexerQueue(Indexer indexer) {
		this.queueToIndex = new ConcurrentLinkedQueue<VisitedPage>();
		this.indexer = indexer;
	}
	
	/**
	 * Method used to notify the queue that no more data will be queued
	 * The queue will continue indexing its content and then finish
	 */
	public void setAllDone() { 
		this.allDone = true;
	}
	
	/**
	 * Method used by crawlers to queue visited page
	 * @param page The visited page
	 * @return boolean indicating that the page is queued
	 */
	public boolean queueVisitedPage(VisitedPage page) {
		//we accept only complete data
		if(page == null) throw new NullPointerException("The page is null");
		if(!page.isComplete()) throw new RuntimeException("The visited page is not complete " + page.getMetadata().getUrl());
		
		return this.queueToIndex.add(page);
	}

	@Override
	public synchronized void start() {
		this.allDone = false;
		super.start();
	}
	
	@Override
	public void run() {
		while(!allDone || queueToIndex.size() > 0) {
			
			if(Labo1.DEBUG) System.err.println("INDEXER: remaining: " + queueToIndex.size() + " [" + (allDone ? "waiting for finish" : "waiting for data") + "]");
			
			if(queueToIndex.isEmpty()) {
				//we wait for an instant
				try {
					sleep(1000);
				} catch (InterruptedException e) { /* NOTHING TO DO */ }
				
				continue;
			}
			
			//we get first element
			VisitedPage page = queueToIndex.poll();
			if(page != null) {
				index(page.getMetadata(), page.getContent());
			}
			
		}
		//we are done
		indexer.finalizeIndexation();
	}
	
	private void index(Metadata metadata, String content) {
		if(indexer == null) {
			System.err.println("WebPageIndexerQueue doesn't contains a WebPageIndexer, you must set it in Labo1.java before starting the crawling");
			System.exit(1);
		}
		indexer.index(metadata, content);
	}
	
}
