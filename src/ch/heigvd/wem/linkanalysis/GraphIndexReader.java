package ch.heigvd.wem.linkanalysis;

import ch.heigvd.wem.data.VisitedPage;
import ch.heigvd.wem.interfaces.Index;
import ch.heigvd.wem.tools.Tools;
import edu.uci.ics.crawler4j.url.WebURL;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * This class aims to create an oriented graph based on an index
 * previously created by a crawler. <br/>
 *
 * @author Florian Poulin <i>(florian.poulin at heig-vd.ch)</i>
 */
public class GraphIndexReader {

    private AdjacencyMatrix am;
    private LinkedHashMap<Long, Integer> map;

    /**
     * Constructor. Parses the index
     *
     * @param index Index to parse to create the graph
     *                 class comments.
     */
    public GraphIndexReader(Index index) {

        // Init structures for storing information
        List<Long> nodes = new LinkedList<>();
        Map<Long, LinkedList<Long>> edges = new HashMap<>();

        Map<String, Long> checkedPages = new HashMap<>();

        // Nodes init
        nodes.addAll(index.getLinkTable().keySet());

        // Edges init
        VisitedPage currentPage;
        String currentHash;
        for (Map.Entry<Long, VisitedPage> entry : index.getMetadataTable().entrySet()) {
            for (WebURL url : entry.getValue().getMetadata().getLinks()) {

                try {
                    currentPage = index.getMetadataTable().get(index.getUrlTable().get(url));

                    // Hash the content of the page to prevent analyzing multiple times the same page
                    currentHash = Tools.hash(currentPage.getContent());

                    // Check if we already visited this page or not (through another URL)
                    if (!checkedPages.containsKey(currentHash)) {
                        checkedPages.put(currentHash, currentPage.getMetadata().getDocID());

                        // Create a new edges entry in the map
                        if (!edges.containsKey(currentPage.getMetadata().getDocID()))
                            edges.put(currentPage.getMetadata().getDocID(), new LinkedList<>());

                        // Add the link between two URLs in the edge map
                        edges.get(currentPage.getMetadata().getDocID()).add(entry.getKey());
                    }
                }

                // If an URL is not known by the index, an exception is thrown
                catch (NullPointerException e) {
                    System.out.println("URL unknown : " + url.toString());
                }
            }
        }

        // Init members
        am = new ArrayListMatrix(nodes.size());
        map = new LinkedHashMap<>();

        // Build nodes
        int i = 0;
        for (Long node : nodes)
            map.put(node, i++);

        // Build edges
        for (Long key : edges.keySet())
            for (Long value : edges.get(key))
                am.set(map.get(key), map.get(value), 1);
    }

    /**
     * Returns the adjacency matrix.
     *
     * @return the adjacency matrix.
     */
    public AdjacencyMatrix getAdjacencyMatrix() {
        return am;
    }

    /**
     * Returns the node name mapping. This map associates a node name to its matrix
     * index.
     *
     * @return a map associating a node name to its index in the matrix.
     */
    public HashMap<Long, Integer> getNodeMapping() {
        return map;
    }

    @Override
    public String toString() {
        String s = "AdjacencyMatrix: \n";
        s += this.am.toString() + "\n";
        s += "Map: \n";
        s+= this.map.toString();
        return s;
    }
}
