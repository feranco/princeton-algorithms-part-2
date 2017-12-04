import java.util.ArrayList;
import edu.princeton.cs.algs4.*;

/**
 *  The SAP class implements functionalities to find shortest paths (and their distances) between wordnet graph nodes.
 *  @author Fernando Franco
 */

public class SAP {
    
    private Digraph graph;
    
    private boolean isValidVertex (int i) {
        return (i < 0 || i >= graph.V()) ? false : true;
    }
    
    private boolean areValidVertices(Iterable<Integer> v) {
        
        for (Integer x : v) {
            if (!isValidVertex(x)) {
                return false;
            }
        }
        
        return true;
    }
    
    // constructor takes a digraph (not necessarily a DAG)
    public SAP(Digraph G) {
        graph = G;
    }
    
    // length of shortest ancestral path between v and w; -1 if no such path
    public int length(int v, int w) {
        if (!isValidVertex(v) || !isValidVertex(w)) {
            throw new IndexOutOfBoundsException();
        }
        
        BreadthFirstDirectedPaths bfsV = new BreadthFirstDirectedPaths(graph, v);
        BreadthFirstDirectedPaths bfsW = new BreadthFirstDirectedPaths(graph, w);
        
        int ancestor = ancestor(v, w);
        int pathLength = (ancestor == - 1) ? -1 : bfsV.distTo(ancestor) + bfsW.distTo(ancestor); //we start from -2 to exclude the edge vertices
        
        return pathLength;
        
    }
    
    // a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
    public int ancestor(int v, int w) {
        
        if (!isValidVertex(v) || !isValidVertex(w)) {
            throw new IndexOutOfBoundsException();
        }
        
        int shortestPath = Integer.MAX_VALUE;
        ArrayList<Integer> ancestors = new ArrayList<Integer>();
        int ancestor = -1;
        
        BreadthFirstDirectedPaths bfsV = new BreadthFirstDirectedPaths(graph, v);
        BreadthFirstDirectedPaths bfsW = new BreadthFirstDirectedPaths(graph, w);
        
        for (int i = 0; i < graph.V(); i++) {
            if (bfsV.hasPathTo(i) && bfsW.hasPathTo(i)) {
                ancestors.add(i);
            }
        }
        
        for (Integer x : ancestors) {
            if ((bfsV.distTo(x) + bfsW.distTo(x)) < shortestPath) {
                shortestPath = (bfsV.distTo(x) + bfsW.distTo(x));
                ancestor = x;
            }
        }
        return ancestor;
    }
    
    // length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        
        if (!areValidVertices(v) || !areValidVertices(w)) {
            throw new IndexOutOfBoundsException();
        }
        
        BreadthFirstDirectedPaths bfsV = new BreadthFirstDirectedPaths(graph, v);
        BreadthFirstDirectedPaths bfsW = new BreadthFirstDirectedPaths(graph, w);
        
        int ancestor = ancestor(v, w);
        int pathLength = (ancestor == - 1) ? -1 : bfsV.distTo(ancestor) + bfsW.distTo(ancestor); //we start from -2 to exclude the edge vertices
        
        return pathLength;
    }
    
    // a common ancestor that participates in shortest ancestral path; -1 if no such path
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        
        if (!areValidVertices(v) || !areValidVertices(w)) {
            throw new IndexOutOfBoundsException();
        }
        
        int shortestPath = Integer.MAX_VALUE;
        ArrayList<Integer> ancestors = new ArrayList<Integer>();
        int ancestor = -1;
        
        BreadthFirstDirectedPaths bfsV = new BreadthFirstDirectedPaths(graph, v);
        BreadthFirstDirectedPaths bfsW = new BreadthFirstDirectedPaths(graph, w);
        
        for (int i = 0; i < graph.V(); i++) {
            if (bfsV.hasPathTo(i) && bfsW.hasPathTo(i)) {
                ancestors.add(i);
            }
        }
        
        for (Integer x : ancestors) {
            if ((bfsV.distTo(x) + bfsW.distTo(x)) < shortestPath) {
                shortestPath = (bfsV.distTo(x) + bfsW.distTo(x));
                ancestor = x;
            }
        }
        return ancestor;
    }
    
    // do unit testing of this class
    public static void main(String[] args) {
        In in = new In("digraph1.txt");
	Digraph G = new Digraph(in);
	SAP sap = new SAP(G);
	while (!StdIn.isEmpty()) {
	    int v = StdIn.readInt();
	    int w = StdIn.readInt();
	    int length   = sap.length(v, w);
	    int ancestor = sap.ancestor(v, w);
	    StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
	}
    }
}
