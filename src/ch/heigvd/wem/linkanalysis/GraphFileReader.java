package ch.heigvd.wem.linkanalysis;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * This class aims to read the content of a file containing an oriented graph. The
 * file must be in the folowing format :<br/>
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
 * @author Florian Poulin <i>(florian.poulin at heig-vd.ch)</i>
 */
public class GraphFileReader {

	private AdjacencyMatrix am;
	private LinkedHashMap<String,Integer> map;

	/**
	 * Constructor. Parses the file with the given name.
	 * @param filename Name of the file to be parsed. See format instructions in the 
	 * class comments.
	 */
	public GraphFileReader (String filename) {

		// Init structures for storing information
		List<String> nodes = new LinkedList<String>();
		HashMap<String, LinkedList<String>> edges = 
				new HashMap<String, LinkedList<String>>();

		// Open file
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(filename));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.exit(1);
		}

		// Read content
		String line;
		try {

			// Read first line
			br.readLine();

			// Read nodes (until #)
			while (!(line = br.readLine()).startsWith("#"))
				nodes.add(line);

			// Read edges (until EOF)
			while ((line = br.readLine()) != null) {

				String[] tmp = line.split(";");
				LinkedList<String> list;
				if ((list = edges.get(tmp[0])) == null) {
					list = new LinkedList<String>();
					edges.put(tmp[0], list);
				}
				list.add(tmp[1]);
			}
			br.close();

		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}

		// Init members
		am  = new ArrayListMatrix(nodes.size());
		map = new LinkedHashMap<String,Integer>();

		// Build nodes
		int i=0;
		for (String node : nodes)
			map.put(node, i++);

		// Build edges
		for (String key : edges.keySet())
			for (String value : edges.get(key))
				am.set(map.get(key), map.get(value), 1);
	}

	/**
	 * Returns the adjacency matrix.
	 * @return the adjacency matrix.
	 */
	public AdjacencyMatrix getAdjacencyMatrix() {
		return am;
	}

	/**
	 * Returns the node name mapping. This map associates a node name to its matrix 
	 * index.
	 * @return a map associating a node name to its index in the matrix.
	 */
	public HashMap<String,Integer> getNodeMapping() {
		return map;
	}
}
