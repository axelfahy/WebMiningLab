package ch.heigvd.wem.linkanalysis;

/**
 * An interface for defining an adjacency matrix of a directed graph. The content of 
 * a cell (i,j) specifies if there is an edge going from i to j. If there is an edge
 * going from node i to node j, then the cell (i,j) contains 1.0, 0.0 otherwise. New 
 * elements can be dynamically added to the matrix.
 * @author Florian Poulin <i>(florian.poulin at heig-vd.ch)</i>
 */
public interface AdjacencyMatrix {

	/**
	 * Returns the length of the adjacency matrix.
	 * @return Length of the square matrix.
	 */
	public int size();

	/**
	 * Returns the element at the specified position in this matrix.
	 * @param i Index of element row to return.
	 * @param j Index of element column to return.
	 * @return the value of the specified cell
	 */
	public double get(int i, int j);

	/**
	 * Define the element at the specified position in this matrix.
	 * @param i Index of element row to define.
	 * @param j Index of element column to define.
	 * @param edge Define whether there is an edge (=1.0), or not (=0.0).
	 */
	public void set(int i, int j, double edge);

	/**
	 * Dynamically adds an element to the end of the matrix. This will add a new row
	 * and a new column at the end of the matrix with all cells initialized to 
	 * <code>0.0</code>.
	 * @return Index of the newly created row and column (same for both).
	 */
	public int addLast();

	/**
	 * Returns a copy of the matrix with <i>transition probabilities</i> instead of 
	 * 0.0 and 1.0. The sum of the elements of a row must be 1.0.
	 * @return Transition Matrix
	 */
	public AdjacencyMatrix getTransitionMatrix();
}
