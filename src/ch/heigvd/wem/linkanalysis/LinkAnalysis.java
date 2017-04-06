package ch.heigvd.wem.linkanalysis;

import java.util.Vector;

/**
 * This class provides static methods to make link analysis.
 */
public class LinkAnalysis {

	/**
	 * Calculates and returns the hub vector.
	 * @param m Adjacency matrix.
	 * @param ac Auhtority vector of the previous step.
	 * @return Hub vector.
	 */
	public static Vector<Double> calculateHc (AdjacencyMatrix m, Vector<Double> ac) {

		Vector<Double> result = new Vector<Double>(m.size());

		/* A IMPLEMENTER */

		return result; 
	}

	/**
	 * Calculates and returns the authority vector.
	 * @param m Adjacency matrix.
	 * @param hc Hub of the previous step.
	 * @return Authority vector.
	 */
	public static Vector<Double> calculateAc (AdjacencyMatrix m, Vector<Double> hc) {

		Vector<Double> result = new Vector<Double>(m.size());

		/* A IMPLEMENTER */

		return result; 
	}

	/**
	 * Calculates and returns the pagerank vector.
	 * @param m Adjacency matrix.
	 * @param pr Pagerank vector of the previous step.
	 * @return Pagerank vector.
	 */
	public static Vector<Double> calculatePRc (AdjacencyMatrix m, Vector<Double> pr) {

		Vector<Double> result = new Vector<Double>(m.size());

		/* A IMPLEMENTER */

		return result; 
	}
	
}
