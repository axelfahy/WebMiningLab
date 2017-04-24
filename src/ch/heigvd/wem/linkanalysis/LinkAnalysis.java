package ch.heigvd.wem.linkanalysis;

import java.util.ArrayList;
import java.util.Vector;

/**
 * This class provides static methods to make link analysis.
 */
public class LinkAnalysis {

    // Constants for calculateMetrics method
    private static final int AUTHORITY = 0;
    private static final int HUB = 1;

    /**
     * Calculates and returns the hub vector.
     *
     * @param m  Adjacency matrix.
     * @param ac Authority vector of the previous step.
     * @return Hub vector.
     */
    public static Vector<Double> calculateHc(AdjacencyMatrix m, Vector<Double> ac) {
        return LinkAnalysis.calculateMetrics(m, ac, LinkAnalysis.HUB);
    }

    /**
     * Calculates and returns the authority vector.
     *
     * @param m  Adjacency matrix.
     * @param hc Hub of the previous step.
     * @return Authority vector.
     */
    public static Vector<Double> calculateAc(AdjacencyMatrix m, Vector<Double> hc) {
        return LinkAnalysis.calculateMetrics(m, hc, LinkAnalysis.AUTHORITY);
    }

    /**
     * Calculates the metrics (authority or hub) depending on the mode.
     *
     * Since both methods (calculateAc and calculateHc) are pretty similar,
     * this function is used for both calculation.
     *
     * Values are normalized by the sqrt of the sum of the square of each values.
     *
     * @param m Adjacency matrix
     * @param c Authority or hub of the previous step (depending on mode)
     * @param metric Metric to use (LinkAnalysis.AUTHORITY of LinkAnalysis.HUB)
     * @return
     */
    private static Vector<Double> calculateMetrics(AdjacencyMatrix m, Vector<Double> c, int metric) {
        // The result vector contains the Ac or Hc value for each page Di
        Vector<Double> result = new Vector<>(m.size());

        // Value for normalization sqrt of sum of square of each values
        Double norm = 0.0;

        // Update authority/hub values
        // For the authority, we process the pages (q) linking to the current one (p) and sum its hub value
        // For the hub, we process the pages (q) that the current one (p) links to and sum its authority value
        for (int p = 0; p < m.size(); p++) {
            Double sum = 0.0;
            // Run over each node, if the node is linking to the current one
            // and is not the current page, sum its hub value
            for (int q = 0; q < m.size(); q++) {
                // Authority
                if (metric == LinkAnalysis.AUTHORITY) {
                    if (m.get(q, p) == 1.0 && p != q) {
                        sum += c.get(q);
                    }
                }
                else if (metric == LinkAnalysis.HUB) {
                    // Hub
                    if (m.get(p, q) == 1.0 && p != q) {
                        sum += c.get(q);
                    }
                }
            }
            // Calculate the value for the normalization
            norm += sum * sum;
            result.add(sum);
        }
        norm = Math.sqrt(norm);

        // Normalize each value of the vector
        for (int i = 0; i < m.size(); i++) {
            result.set(i, result.get(i) / norm);
        }

        return result;
    }

    /**
     * Calculates the authority and hub metrics at a certain number of iterations
     *
     * @param m Adjacency matrix
     * @param nbIterations Number of iterations to used
     * @return ArrayList containing the vector of authority(0) and the vector of hub(1)
     */
    public static ArrayList<Vector<Double>> calculateAcAndHubAtIterations(AdjacencyMatrix m, int nbIterations) {

        Vector<Double> auth= new Vector<>(m.size());
        Vector<Double> nextAuth = new Vector<>(m.size());
        Vector<Double> hub = new Vector<>(m.size());
        Vector<Double> nextHub = new Vector<>(m.size());
        ArrayList<Vector<Double>> result = new ArrayList<>();

        // Initialization of value at 1
        for (int i = 0; i < m.size(); i++) {
            auth.add(1.0);
            nextAuth.add(1.0);
            hub.add(1.0);
            nextHub.add(1.0);
        }

        for (int i = 0; i < nbIterations; i++) {
            // Update authority values
            nextAuth = LinkAnalysis.calculateAc(m, hub);

            // Update hub values
            nextHub = LinkAnalysis.calculateHc(m, auth);

            // Update hub and auth values
            hub = nextHub;
            auth = nextAuth;
        }

        // Return the two vectors in an ArrayList
        result.add(0, auth);
        result.add(1, hub);
        return result;
    }

    /**
     * Calculates and returns the pagerank vector.
     *
     * @param m  Adjacency matrix.
     * @param pr Pagerank vector of the previous step.
     * @return Pagerank vector.
     */
    public static Vector<Double> calculatePRc(AdjacencyMatrix m, Vector<Double> pr) {

        Vector<Double> result = new Vector<>(m.size());

        // E = 0.15 / |V|
        double e = 0.15 / m.size();

        AdjacencyMatrix mTransition = m.getTransitionMatrix();

        // Value for normalization sum of Pr(Dj)
        Double norm = 0.0;

        for (int p = 0; p < mTransition.size(); p++) {
            Double sum = 0.0;
            // The transition matrix already gives a percentage
            for (int q = 0; q < m.size(); q++) {
                if (p != q) {
                    // Get(q, p) because we need to transposed value
                    sum += mTransition.get(q, p) * pr.get(q);
                }
            }
            sum = 0.85 * sum + e;
            norm += sum;
            // Add the sum for the normalization
            result.add(sum);
        }

        // Normalize each value of the vector
        // Normalization is done by dividing the result by the total sum
        for (int i = 0; i < m.size(); i++) {
            result.set(i, result.get(i) / norm);
        }

        return result;
    }

    /**
     * Calculates the page rank at a certain number of iterations
     *
     * @param m  Adjacency matrix.
     * @param nbIterations Number of iterations to used
     * @return Pagerank vector.
     */
    public static Vector<Double> calculatePrAtIterations(AdjacencyMatrix m, int nbIterations) {
        Vector<Double> pr = new Vector<>(m.size());
        Vector<Double> nextPr = new Vector<>(m.size());

        // Initialization of value at 1
        for (int i = 0; i < m.size(); i++) {
            pr.add(1.0 / m.size());
            nextPr.add(1.0 / m.size());
        }

        for (int i = 0; i < nbIterations; i++) {
            // Calculate the next page rang using the current one
            nextPr = LinkAnalysis.calculatePRc(m, pr);
            pr = nextPr;
        }
        return pr;
    }
}
