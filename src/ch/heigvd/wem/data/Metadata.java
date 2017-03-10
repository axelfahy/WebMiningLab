package ch.heigvd.wem.data;

import edu.uci.ics.crawler4j.url.WebURL;

import java.io.Serializable;
import java.util.Set;

/**
 * A set of document metadata
 */
public class Metadata implements Serializable {

	private static final long serialVersionUID = -8430586236974769791L;

	private static long nextdocId = 0;

    // unique identifier of the document
    private long docid;
    // the web page URL
    private WebURL url;
    // the web page title
    private String title;
    // the links contained in the web page
    private Set<WebURL> links;

    public Metadata() {
    	this.docid = nextdocId++;
	}
    
    /**
     * Create a new metadata to describe a web page
     * @param url Its url
     * @param title Its title
     * @param links Its links
     */
    public Metadata(WebURL url, String title, Set<WebURL> links){
        this.docid = nextdocId++;
        this.url   = url;
        this.title = title;
        this.links = links;
    }

    /**
     * @return The unique web page identifier, it is auto-generated
     */
    public long getDocID(){ return docid; }
    
    /**
     * @return The web page's title
     */
    public String getTitle() { return title; }
    
    /**
     * @param title The web page's title
     */
    public void setTitle(String title) { this.title = title; }

    /**
     * @return The web page's url
     */
    public WebURL getUrl() { return url; }
    
    /**
     * @param url The web page's url
     */
    public void setUrl(WebURL url) { this.url = url; }
    
    /**
     * @return The links found in the web page
     */
    public Set<WebURL> getLinks(){ return links; }
    
    /**
     * @param links The links found in the web page
     */
    public void setLinks(Set<WebURL> links) { this.links = links; }
    
    public boolean isComplete() {
    	return !(this.url == null || this.title == null || this.links == null);
    }
    
    @Override
    public String toString(){
        return title + " [" + url + "]";
    }
    
}
