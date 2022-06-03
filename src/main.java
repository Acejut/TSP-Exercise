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
		BufferedReader f = new BufferedReader(new InputStreamReader(new FileInputStream("src/LAGraph.gph")));
		int[] startEnd;
		
		arrCities = createCityArr(arrCities);
	    
	    startEnd = cityGrab(arrCities);
	    
	    if (startEnd[0] == -1 || startEnd[1] == -1)
	    	System.out.println("City not found or starting city is the same as end city.");
	    else
	    {
	    	createGraph(f, G, arrCities, startEnd);
	    	graphTraverse(G, arrCities);
	    }
	    
	    System.out.printf("\nTotal sum of weight = %-5.2fkm", total_weight);
	}
	
	static CityNode[] createCityArr(CityNode[] arr) 
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
				
				arr[i] = new CityNode(name, lat, lon, pop);
				i++;
			}
			scan.close();
		}
		
		catch (FileNotFoundException e) 
			{System.out.println("LATowns.txt FILE NOT FOUND.");}
		
		return arr;
	}
	
	static int[] cityGrab(CityNode[] arrCities)
	{
		Scanner userIn = new Scanner(System.in);
		String startCity, endCity;
		int[] startEnd = new int[]{-1, -1};
		
		System.out.println("Please enter the name of the starting city:");
		startCity = userIn.nextLine();
		
		System.out.println("Please enter the name of the ending city:");
		endCity = userIn.nextLine();
		
		for (int i = 0; i < arrCities.length; i++)
		{
			if (startCity.equalsIgnoreCase(arrCities[i].name))
				startEnd[0] = i;
			else if (endCity.equalsIgnoreCase(arrCities[i].name))
				startEnd[1] = i;
		}
		userIn.close();
		return startEnd;
	}
	
	static Graph createGraph(BufferedReader file, Graph G, CityNode[] arrCities, int[] startEnd) throws IOException 
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
		arrCities = arrayAdjust(arrCities, startEnd, n);
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
		System.out.println("\n=-DISTANCE PRINTOUTS-=\n");
		
		while((line = file.readLine()) != null) 
		{
			token = new StringTokenizer(line);
			v1 = Integer.parseInt(token.nextToken());
			v2 = Integer.parseInt(token.nextToken());
			if (token.hasMoreTokens())
			{
				weight = CityNode.getDistance(arrCities[v1], arrCities[v2]);
				System.out.printf("The distance from %-18s and %-18s is: %2fkm\n", 
						arrCities[v1].name, arrCities[v2].name, weight);
			}
			else // No weight given -- set at 1
				weight = 1;
			G.setEdge(v1, v2, weight);
			if (undirected) // Put in edge in other direction
				G.setEdge(v2, v1, weight);
		}
		return G;
	}
	
	static CityNode[] arrayAdjust(CityNode[] arr, int[] startEnd, int n)
	{
		CityNode temp0 = new CityNode(arr[startEnd[0]]);
		CityNode temp1 = new CityNode(arr[startEnd[1]]);
		
		arr[startEnd[0]] = arr[0];
		arr[0] = temp0;
		
		arr[startEnd[1]] = arr[n-1];
		arr[n-1] = temp1;
		
		return arr;
	}
	
	static void graphTraverse(Graph G, CityNode[] arr) 
	{
		System.out.println("\n=-BEGIN TRAVELLING-=\n");
		int v;
		for (v = 0; v < G.n(); v++)
			G.setMark(v, UNVISITED); // Initialize 
		for (v = 0; v <G.n(); v++)
			if (G.getMark(v) == UNVISITED)
				doTraverse(G, v, arr);
	}

	static void doTraverse(Graph G, int v, CityNode[] arr) 
	{DFS(G, v, arr);}
	
	static void DFS(Graph G, int v, CityNode[] arr) 
	{
		G.setMark(v, VISITED);
		for (int w = G.first(v); w < G.n() ; w = G.next(v, w))
			if (G.getMark(w) == UNVISITED)
			{
				System.out.printf("Going from %-18s to %-18s was %-2.2fkm\n", arr[v].name, arr[w].name, G.weight(v, w));
				DFS(G, w, arr);
				total_weight += G.weight(v, w);
		    }
	}
}