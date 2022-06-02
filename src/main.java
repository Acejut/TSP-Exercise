import java.io.*;
import java.util.*;

//Justin Ruiz

/* TODO: Modify BFS function to not return trivial solution - create path based on edge weight */

public class main 
{
	
	static final int UNVISITED = 0;
	static final int VISITED = 1;
	static double total_weight = 0;

	public static void main(String[] args) throws IOException
	{
		Graph G = new Graphm();
		CityNode[] arrCities = new CityNode[25];
		arrCities = createCityArr(arrCities);
		BufferedReader f = new BufferedReader(new InputStreamReader(new FileInputStream("src/LAGraph.gph")));
		int cityStart;
		
	    createGraph(f, G, arrCities);
	    //graphTraverse(G); //DFS Implementation. BFS might be better(?)
	    
	    cityStart = cityGrab(arrCities);
	    
	    if (cityStart != -1)
	    	BFS(G, cityStart);
	    else
	    	System.out.println("City not found.");
	    
	    System.out.printf("Total sum of weight = %-5.2fkm", total_weight);
	}
	
	static Graph createGraph(BufferedReader file, Graph G, CityNode[] arrCities) throws IOException 
	{
		String line = null;
		StringTokenizer token;
		boolean undirected = false;
		int i, v1, v2;
		double weight;
		
		line = file.readLine();
		while(line.charAt(0) == '#')
			line = file.readLine();
		token = new StringTokenizer(line);
		int n = Integer.parseInt(token.nextToken());
		G.Init(n);
		for (i=0; i<n; i++)
			G.setMark(i, UNVISITED);
		line = file.readLine();
		if (line.charAt(0) == 'U')
			undirected = true;
		else if (line.charAt(0) == 'D')
			undirected = false;
		//else assert false : "Bad graph type: " + line;
		
		// Read in edges
		while((line = file.readLine()) != null) 
		{
			token = new StringTokenizer(line);
			v1 = Integer.parseInt(token.nextToken());
			v2 = Integer.parseInt(token.nextToken());
			if (token.hasMoreTokens())
			{
				weight = CityNode.getDistance(arrCities[v1], arrCities[v2]);
				System.out.printf("The distance from %-15s and %-15s is: %2f\n", arrCities[v1].name, arrCities[v2].name, weight);
			}
			else // No weight given -- set at 1
				weight = 1;
			G.setEdge(v1, v2, weight);
			if (undirected) // Put in edge in other direction
				G.setEdge(v2, v1, weight);
		}
		return G;
	}
	
	public static int cityGrab(CityNode[] arrCities)
	{
		Scanner userIn = new Scanner(System.in);
		String startCity;
		
		System.out.println("Please enter the name of the starting city:");
		startCity = userIn.nextLine();
		
		for (int i = 0; i < arrCities.length; i++)
		{
			if (startCity.equalsIgnoreCase(arrCities[i].name))
				return i;
		}
		return -1;
	}
	
	static void BFS(Graph G, int start) 
	{
		Queue<Integer> Q = new AQueue<Integer>(G.n());
		Q.enqueue(start);
		G.setMark(start, VISITED);
		while (Q.length() > 0) 
		{    // Process each vertex on Q
			int v = Q.dequeue();
			for (int w = G.first(v); w < G.n(); w = G.next(v, w))
				if (G.getMark(w) == UNVISITED) 
				{ // Put neighbors on Q
					total_weight += G.weight(v, w);
					System.out.printf("Visiting node %-2d from node %-2d with edge weight %-5.2f\n", w, v, G.weight(v, w));
					G.setMark(w, VISITED);
					Q.enqueue(w);
				}
		}
	}
	
	public static CityNode[] createCityArr(CityNode[] arr) 
	{
		int i = 0;
		arr = new CityNode[25];
		File towns = new File("src/LATowns.txt");
		
		try 
		{
			Scanner scan = new Scanner(towns);
			
			scan.nextLine();
			
			while (scan.hasNextLine())
			{
				String name = scan.next();
				double lat = scan.nextDouble();
				double lon = scan.nextDouble();
				int pop = scan.nextInt();
				
				arr[i] = new CityNode(name, lat, lon);
				i++;
			}
		}
		
		catch (FileNotFoundException e) 
			{System.out.println("LATowns.txt FILE NOT FOUND.");}
		
		return arr;
	}
	
	//for DFS Implementation. BFS might be better(?)
	
	/*
	static void graphTraverse(Graph G) 
	{
		int v;
		for (v = 0; v < G.n(); v++)
			G.setMark(v, UNVISITED); // Initialize 
		for (v = 0; v <G.n(); v++)
			if (G.getMark(v) == UNVISITED)
				doTraverse(G, v);
	}

	static void DFS(Graph G, int v) 
	{
		G.setMark(v, VISITED);
		for (int w = G.first(v); w < G.n() ; w = G.next(v, w))
			if (G.getMark(w) == UNVISITED)
			{
				DFS(G, w);
				total_weight += G.weight(v, w);
				System.out.printf("Visiting node %-2d from node %-2d with edge weight %f\n", w, v, G.weight(v, w));
		    }
	}
	
	static void doTraverse(Graph G, int v) 
	{
		DFS(G, v);
	}
	*/
}
