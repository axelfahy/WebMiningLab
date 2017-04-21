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
 * This class aims to read the content of a file containing an oriented graph. The
 * file must be in the following format :<br/>
 * <pre><code>
 * # nodes
 * node1
 * node2
 * node3
 * # edges (from;to)
 * node1;node3
 * node2;node3
 * mode3;node1
 * </code></pre>
 *
 * @author Florian Poulin <i>(florian.poulin at heig-vd.ch)</i>
 */
public class GraphIndexReader {

    private AdjacencyMatrix am;
    private LinkedHashMap<Long, Integer> map;

    /**
     * Constructor. Parses the file with the given name.
     *
     * @param index Index to parse to create the graph
     *                 class comments.
     */
    public GraphIndexReader(Index index) {

        // Init structures for storing information
        List<Long> nodes = new LinkedList<>();
        HashMap<Long, LinkedList<Long>> edges = new HashMap<>();

        Map<String, Long> checkedPages = new HashMap<>();

        // Nodes init
        nodes.addAll(index.getLinkTable().keySet());

        // Edges init
        VisitedPage currentPage;
        String currentHash;
        for (Map.Entry<Long, VisitedPage> entry : index.getMetadataTable().entrySet()) {
            for (WebURL url : entry.getValue().getMetadata().getLinks()) {

                currentPage = index.getMetadataTable().get(index.getUrlTable().get(url));

                // TODO NullPointerException after the getContent() method is called, currentPage is null ?
                currentHash = Tools.hash(currentPage.getContent());

                // Check if we already visited this page or not (through another URL)
                if (!checkedPages.containsKey(currentHash)) {
                    checkedPages.put(currentHash, currentPage.getMetadata().getDocID());

                    if (!edges.containsKey(currentPage.getMetadata().getDocID()))
                        edges.put(currentPage.getMetadata().getDocID(), new LinkedList<>());

                    edges.get(currentPage.getMetadata().getDocID()).add(entry.getKey());
                }
            }
        }

//        // Read content
//        String line;
//        try {
//
//            // Read first line
//            br.readLine();
//
//            // Read nodes (until #)
//            while (!(line = br.readLine()).startsWith("#"))
//                nodes.add(line);
//
//            // Read edges (until EOF)
//            while ((line = br.readLine()) != null) {
//
//                String[] tmp = line.split(";");
//                LinkedList<String> list;
//                if ((list = edges.get(tmp[0])) == null) {
//                    list = new LinkedList<String>();
//                    edges.put(tmp[0], list);
//                }
//                list.add(tmp[1]);
//            }
//            br.close();


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
