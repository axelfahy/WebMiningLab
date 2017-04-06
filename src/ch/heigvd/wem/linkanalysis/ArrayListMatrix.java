package ch.heigvd.wem.linkanalysis;

import java.util.ArrayList;


/**
 * Simple implementation of the {@link AdjacencyMatrix} using ArrayLists.
 * @author Florian Poulin <i>(florian.poulin at heig-vd.ch)</i>
 */
public class ArrayListMatrix implements AdjacencyMatrix {

	// For outputs
	private final static String spacerStr = " ";
	private final static String newlineStr = "\n";

	/**
	 * Structure holding the content of the matrix
	 */
	private ArrayList<ArrayList<Double>> content;

	/**
	 * Default constructor. Builds a matrix of size 0.
	 */
	public ArrayListMatrix() {
		content = new ArrayList<ArrayList<Double>>();
	}

	/**
	 * Constructor. Initializes the content of the matrix to <code>0</code>.
	 */
	public ArrayListMatrix(int length) {

		// allocate memory for length * length integers and initialize to 0
		content = new ArrayList<ArrayList<Double>>(length);
		for (int i=0; i<length; i++) {
			ArrayList<Double> tmp = new ArrayList<Double>(length);
			for (int j=0; j<length; j++)
				tmp.add(j, 0.0);
			content.add(tmp);
		}
	}

	/* (non-Javadoc)
	 * @see AdjacencyMatrix#getLength()
	 */
	@Override
	public int size() {
		return content.size();
	}

	/* (non-Javadoc)
	 * @see AdjacencyMatrix#get(int, int)
	 */
	@Override
	public double get(int i, int j) {
		return content.get(i).get(j);
	}

	/* (non-Javadoc)
	 * @see AdjacencyMatrix#set(int, int, boolean)
	 */
	@Override
	public void set(int i, int j, double edge) {
		content.get(i).set(j, edge);
	}

	/* (non-Javadoc)
	 * @see AdjacencyMatrix#addLast()
	 */
	@Override
	public int addLast() {

		// Add new column
		for (ArrayList<Double> alb : content)
			alb.add(0.0);

		// Allocate and add new row
		ArrayList<Double> tmp = new ArrayList<Double>(content.size()+1);
		for (int i=0; i<content.size()+1; i++)
			tmp.add(0.0);
		content.add(tmp);

		return content.size();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		StringBuffer sb = new StringBuffer();
		for (ArrayList<Double> alb : content) {
			for (Double isEdge : alb)
				sb.append(isEdge + spacerStr);
			sb.append(newlineStr);
		}
		return sb.toString();
	}

	/* (non-Javadoc)
	 * @see labo5.AdjacencyMatrix#getTransitionMatrix()
	 */
	@Override
	public AdjacencyMatrix getTransitionMatrix() {

		AdjacencyMatrix tm = new ArrayListMatrix (size());

		// Normalize rows
		for (int i=0; i<tm.size(); i++) {
			double sumRow = 0.0;
			for (int j=0; j<tm.size(); j++)
				sumRow += get(i, j);
			if (sumRow > 0.0)
				for (int j=0; j<tm.size(); j++)
					tm.set(i, j, get(i, j) / sumRow);
		}
		return tm;
	}
}
