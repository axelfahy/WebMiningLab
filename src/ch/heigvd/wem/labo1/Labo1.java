package ch.heigvd.wem.labo1;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

import ch.heigvd.wem.WebPageIndexerQueue;
import ch.heigvd.wem.WebPageCrawler;
import ch.heigvd.wem.interfaces.Index;
import ch.heigvd.wem.interfaces.Indexer;
import ch.heigvd.wem.interfaces.Retriever;
import ch.heigvd.wem.linkanalysis.GraphFileReader;
import ch.heigvd.wem.linkanalysis.LinkAnalysis;
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
    public static final String START_URL = "http://iict.heig-vd.ch";
    public static final boolean DEBUG = true;
    private static final Mode mode = Mode.RESTORE;
    private static final String indexSaveFileName = "iict.bin";

    public static void main(String[] args) throws IOException {

        Index index = null;

        switch (mode) {
            case CRAWL:
                // We crawl and save the index to disk
                index = crawl();
                saveIndex(indexSaveFileName, index);
                break;

            case RESTORE:
                // We load the index from disk
                index = loadIndex(indexSaveFileName);
                break;
        }

        WebPageRetriever retriever = new WebPageRetriever(index, Retriever.WeightingType.NORMALIZED_FREQUENCY);

        Scanner in = new Scanner(System.in);
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String s;
        int docID;
        int choice = 0;

        do {
            System.out.println("===================================");
            System.out.println("|         LABO 1 - MENU           |");
            System.out.println("===================================");
            System.out.println("| Options:                        |");
            System.out.println("|        1. Search document       |");
            System.out.println("|        2. Search term           |");
            System.out.println("|        3. Execute query         |");
            System.out.println("|        4. Get URL from docID    |");
            System.out.println("|        5. Hyperlinks analysis   |");
            System.out.println("|           (graph_example.txt)   |");
            System.out.println("|        0. Exit                  |");
            System.out.println("===================================");

            System.out.print("> ");
            // Check if there is an Integer to read
            // avoid InputMismatchException if user does not enter an Integer
            try {
                choice = in.nextInt();
                switch (choice) {
                    case 1:
                        System.out.print("Enter a docID > ");
                        docID = in.nextInt();
                        Map<String, Double> res = retriever.searchDocument(docID);
                        System.out.println(res);
                        break;
                    case 2:
                        System.out.print("Enter a term > ");
                        s = br.readLine();
                        System.out.println(retriever.searchTerm(s));
                        break;
                    case 3:
                        System.out.print("Enter a query > ");
                        s = br.readLine();
                        Map<Long, Double> resQuery = retriever.executeQuery(s);
                        // Sort the results to have the most relevant results at the top
                        if (!resQuery.isEmpty()) {
                            resQuery.entrySet().stream()
                                    .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                                    .forEach(System.out::println);
                        }
                        else {
                            System.out.println("No result");
                        }
                        break;
                    case 4:
                        System.out.print("Enter a docID > ");
                        long id = in.nextLong();
                        System.out.println(index.getLinkTable().get(id));
                        break;
                    case 5:
                        GraphFileReader graphReference = new GraphFileReader("graph_reference.txt");
                        System.out.println(graphReference);

                        Vector<Double> input = new Vector<>();
                        // Initialization of value at 1
                        for (int i = 0; i < graphReference.getAdjacencyMatrix().size(); i++) {
                            input.add(1.0);
                        }

                        System.out.println("Authority(1)");
                        System.out.println(LinkAnalysis.calculateAc(graphReference.getAdjacencyMatrix(), input));
                        System.out.println("Hub(1)");
                        System.out.println(LinkAnalysis.calculateHc(graphReference.getAdjacencyMatrix(), input));

                        ArrayList<Vector<Double>> resReference = LinkAnalysis.calculateAcAndHubAtIterations(graphReference.getAdjacencyMatrix(), 5);
                        System.out.println("Authority(5)");
                        System.out.println(resReference.get(0));
                        System.out.println("Hub(5)");
                        System.out.println(resReference.get(1));
                        break;
                    case 0:
                        System.out.println("Exiting program");
                        break;
                    default:
                        System.out.println("Invalid selection");
                        break;
                }
            } catch (InputMismatchException e) {
                System.out.println("Wrong input!");
                choice = 0;
            }
        } while (choice != 0);
    }

    private static Index crawl() {
        // CONFIGURATION
        CrawlConfig config = new CrawlConfig();
        config.setMaxConnectionsPerHost(10);        //maximum 10 for tests
        config.setConnectionTimeout(4000);            //4 sec.
        config.setSocketTimeout(5000);                //5 sec.
        config.setCrawlStorageFolder("temp");
        config.setIncludeHttpsPages(true);
        config.setPolitenessDelay(250);            //minimum 250ms for tests
        config.setUserAgentString("crawler4j/WEM/2017");
        config.setMaxDepthOfCrawling(8);            //max 2-3 levels for tests on large website
        config.setMaxPagesToFetch(5000);            //-1 for unlimited number of pages

        RobotstxtConfig robotsConfig = new RobotstxtConfig(); //by default

        PageFetcher pageFetcher = new PageFetcher(config);
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotsConfig, pageFetcher);

        // We create the indexer and the indexerQueue
        Indexer indexer = new WebPageIndexer();
        WebPageIndexerQueue queue = new WebPageIndexerQueue(indexer);
        queue.start();
        // We set the indexerQueue reference to all the crawler threads
        WebPageCrawler.setIndexerQueue(queue);

        try {
            CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);
            controller.addSeed(START_URL);
            controller.start(WebPageCrawler.class, 20); // This method keep the hand until the crawl is done
        } catch (Exception e) {
            e.printStackTrace();
        }

        queue.setAllDone(); // We notify the indexerQueue that it will not receive more data
        try {
            queue.join(); // We wait for the indexerQueue to finish
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
        } catch (IOException e) {
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
            if (o instanceof Index) {
                return (Index) o;
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        return null;
    }

}
