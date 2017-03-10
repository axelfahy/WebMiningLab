package ch.heigvd.wem.labo1;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import ch.heigvd.wem.WebPageIndexerQueue;
import ch.heigvd.wem.WebPageCrawler;
import ch.heigvd.wem.interfaces.Index;
import ch.heigvd.wem.interfaces.Indexer;
import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

public class Labo1 {

	private enum Mode {
		CRAWL,
		RESTORE;
	}
	
	// CONFIGURATION
	public static final String  START_URL 			= "http://iict.heig-vd.ch";
	public static final boolean DEBUG				= true;
	private static final Mode	mode				= Mode.CRAWL;
	private static final String	indexSaveFileName	= "iict.bin";
	
	public static void main(String[] args) {

		Index index = null;
		
		switch(mode) {
		case CRAWL:
			//we crawl and save the index to disk
			index = crawl();
			saveIndex(indexSaveFileName, index);
			break;
			
		case RESTORE:
			//we load the index from disk
			index = loadIndex(indexSaveFileName);
			break;
		}

		//TODO recherche
		
	}
	
	private static Index crawl() {
		// CONFIGURATION
		CrawlConfig config = new CrawlConfig();
		config.setMaxConnectionsPerHost(10);		//maximum 10 for tests
		config.setConnectionTimeout(4000); 			//4 sec.
		config.setSocketTimeout(5000);				//5 sec.
		config.setCrawlStorageFolder("temp");
		config.setIncludeHttpsPages(true);
		config.setPolitenessDelay(250); 			//minimum 250ms for tests
		config.setUserAgentString("crawler4j/WEM/2017");
		config.setMaxDepthOfCrawling(8);			//max 2-3 levels for tests on large website
		config.setMaxPagesToFetch(5000);			//-1 for unlimited number of pages
		
		RobotstxtConfig robotsConfig = new RobotstxtConfig(); //by default
		
		PageFetcher pageFetcher = new PageFetcher(config);
		RobotstxtServer robotstxtServer = new RobotstxtServer(robotsConfig, pageFetcher);
        
		//we create the indexer and the indexerQueue
		Indexer indexer = null; //TODO vous instancierez ici votre implémentation de l'indexer
		WebPageIndexerQueue queue = new WebPageIndexerQueue(indexer);
		queue.start();
		//we set the indexerQueue reference to all the crawler threads
		WebPageCrawler.setIndexerQueue(queue);
		
		try {
			CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);
			controller.addSeed(START_URL);
			controller.start(WebPageCrawler.class, 20); //this method keep the hand until the crawl is done
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		queue.setAllDone(); //we notify the indexerQueue that it will not receive more data
		try {
			queue.join(); //we wait for the indexerQueue to finish
		} catch (InterruptedException e) { /* NOTHING TO DO */ }
		
		//we return the created index
		return indexer.getIndex();
	}
	
	private static void saveIndex(String filename, Index index) {
		try {
			File outputFile = new File("save", filename);
			ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(outputFile));
			out.writeObject(index);
			out.close();
		} catch(IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	private static Index loadIndex(String filename) {
		try {
			File inputFile = new File("save", filename);
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(inputFile));
			Object o = in.readObject();
			in.close();
			if(o instanceof Index) {
				return (Index) o;
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		return null;
	}
	
}
