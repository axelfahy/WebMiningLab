package ch.heigvd.wem;

import ch.heigvd.wem.data.Metadata;
import ch.heigvd.wem.data.VisitedPage;
import ch.heigvd.wem.labo1.Labo1;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.parser.ParseData;
import edu.uci.ics.crawler4j.url.WebURL;

import java.util.regex.Pattern;

public class WebPageCrawler extends WebCrawler {

    private static WebPageIndexerQueue indexer = null;
    private final static Pattern FILTERS = Pattern.compile(".*(\\.(css|js|gif|jpg"
            + "|png|mp3|zip|gz))$");

    @Override
    public boolean shouldVisit(Page referringPage, WebURL url) {
        if (Labo1.DEBUG) {
            System.out.println("shouldVisit called");
            System.out.println("Referring page: " + referringPage.getWebURL());
            System.out.println("Url: " + url);
        }

        String href = url.getURL().toLowerCase();
        return !FILTERS.matcher(href).matches()
                && href.startsWith(referringPage.getWebURL().toString());
    }

    @Override
    public void visit(Page page) {

        if (indexer == null) {
            System.err.println("WebPageSaver doesn't contains a WebPageIndexer, you must set it in Labo1.java before starting the crawling");
            System.exit(1);
        }

        Metadata metadata = new Metadata();
        String content = null;

        ParseData data = page.getParseData();
        if (data instanceof HtmlParseData) {
            HtmlParseData htmlData = (HtmlParseData) data;
            metadata.setTitle(htmlData.getTitle());
            metadata.setUrl(page.getWebURL());
            metadata.setLinks(htmlData.getOutgoingUrls());
            content = htmlData.getText();
        }

        // We queue the page for indexer
        VisitedPage visitedPage = new VisitedPage(metadata, content);
        indexer.queueVisitedPage(visitedPage);
    }

    public static void setIndexerQueue(WebPageIndexerQueue indexer) {
        WebPageCrawler.indexer = indexer;
    }

    public static WebPageIndexerQueue getIndexerQueue() {
        return indexer;
    }

}
