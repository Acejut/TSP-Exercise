/** Source code example for "A Practical Introduction to Data
    Structures and Algorithm Analysis, 3rd Edition (Java)" 
    by Clifford A. Shaffer
    Copyright 2008-2011 by Clifford A. Shaffer
*/
import java.util.*;
/** Graph: Adjacency matrix */
class Graph {
  public double[][] matrix;                // The edge matrix
  private int numEdge;                   // Number of edges
  public int[] Mark;                     // The mark array
  public String[] nodeName;

  public Graph() {}                     // Constructors
  public Graph(int n) {
    Init(n);
  }

  public void Init(int n) {
    Mark = new int[n];
    matrix = new double[n][n];
    numEdge = 0;
    nodeName = new String[n];
    Arrays.fill(nodeName, 0, n, "");
  }

  public int n() { return Mark.length; } // # of vertices
  public int e() { return numEdge; }     // # of edges

  /** @return v's first neighbor */
  public int first(int v) {
    for (int i=0; i<Mark.length; i++)
      if (matrix[v][i] != 0) return i;
    return Mark.length;  // No edge for this vertex
  }

 /** @return v's next neighbor after w */
  public int next(int v, int w) {
    for (int i=w+1; i<Mark.length; i++)
      if (matrix[v][i] != 0)
        return i;
    return Mark.length;  // No next edge;
  }

  /** Set the weight for an edge */
  public void setEdge(int i, int j, double wt) {
    assert wt!=0 : "Cannot set weight to 0";
    if (matrix[i][j] == 0) numEdge++;
    matrix[i][j] = wt;
  }

  /** Delete an edge */
  public void delEdge(int i, int j) { // Delete edge (i, j)
    if (matrix[i][j] != 0) numEdge--;
    matrix[i][j] = 0;
  }

  /** Determine if an edge is in the graph */
  public boolean isEdge(int i, int j)
    { return matrix[i][j] != 0; }
  
  /** @return an edge's weight */
  public double weight(int i, int j) {
    return matrix[i][j];
  }

  /** Set/Get the mark value for a vertex */
  public void setMark(int v, int val) { Mark[v] = val; }
  public int getMark(int v) { return Mark[v]; }
  
  /** Set/Get the name value for a vertex */
  public void setName(int v, String val) { nodeName[v] = val; }
  public String getName(int v) { return nodeName[v]; }
  
  /** @return the index of the city node whose weight is the least for that city and unvisited*/
  public int getLeast(int i)
  {
	  double min = Double.MAX_VALUE;
	  int index = 0;
	  for (int j = 0; j < Mark.length; j++)
	  {
		  if ((matrix[i][j] < min) && (matrix[i][j] != 0) && (getMark(j) == 0))
		  {
			  min = matrix[i][j];
			  index = j;
		  }
	  }
	  return index;
  }
  
  /** @return last node of the graph */
  public int getLastNode()
  {return (this.n()-1);}
  
}