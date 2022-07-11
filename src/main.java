import java.io.*;
import java.util.*;

//Justin Ruiz

/* TODO: Implement permutation algorithm */

public class main 
{
	static double total_weight = 0;
	static final int UNVISITED = 0;
	static final int VISITED = 1;
	static final int LAST_VISIT = 2;
	
	public static void main(String[] args) throws IOException
	{
		Graph G = new Graphm();
		CityNode[] arrCities = null;
		BufferedReader f = new BufferedReader(new InputStreamReader(new FileInputStream("src/LAGraph.gph")));
		int[] startEnd;
		
		arrCities = createCityArr(arrCities);
	    
	    startEnd = cityGrab(arrCities);
	    
	    System.out.println(arrCities.length);
	    
	    if (startEnd[0] == -1 || startEnd[1] == -1)
	    	System.out.println("City not found.");
	    else
	    {
	    	long startTime, endTime;
	    	double totalTime;
	    	
	    	startTime = System.nanoTime();
	    	
	    	//createGraph(f, G, arrCities, startEnd);
	    	arrCities = arrayAdjust(arrCities, startEnd, arrCities.length);
	    	G = createGraph(G, arrCities, startEnd);
	    	graphTraverse(G, arrCities);
	    	
	    	endTime = System.nanoTime();
	    	totalTime = (double) (endTime - startTime) / 1000000;
	    	System.out.println(totalTime + "ms");
	    }
	    
	    System.out.printf("\nTotal sum of weight = %-5.2fkm", total_weight);
	}
	
	static CityNode[] createCityArr(CityNode[] arr) 
	{
		int i = 0;
		File towns = new File("src/LATowns.txt");
		
		try 
		{
			Scanner firstScan = new Scanner(towns);
			
			firstScan.nextLine();
			while (firstScan.hasNextLine())
			{
				firstScan.nextLine();
				i++;
			}
			firstScan.close();
			
			arr = new CityNode[i+1];
			i = 0;
			
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
		catch (FileNotFoundException p)
		{System.out.println("LATowns.txt FILE NOT FOUND.");}
		
		return arr;
	}
	
	static int[] cityGrab(CityNode[] arrCities)
	{
		Scanner userIn = new Scanner(System.in);
		String startCity, endCity, response;
		int[] startEnd = new int[]{-1, -1, -1};
		
		startCity = endCity = response = "";
		
		System.out.println("Start and end in same city? (y/n)");
		response = userIn.nextLine();
		if (response.equalsIgnoreCase("y"))
		{
			startEnd[2] = 0;
			System.out.println("Please enter the name of the starting city:");
			startCity = userIn.nextLine();
			endCity = startCity;
		}
		else if (response.equalsIgnoreCase("n"))
		{
			startEnd[2] = 1;
			
			System.out.println("Please enter the name of the starting city:");
			startCity = userIn.nextLine();
			
			System.out.println("Please enter the name of the ending city:");
			endCity = userIn.nextLine();
		}
		else
			System.out.println("Invalid input.");

			
		for (int i = 0; i < arrCities.length-1; i++)
		{
			if (startCity.equalsIgnoreCase(arrCities[i].name))
				startEnd[0] = i;
			if (endCity.equalsIgnoreCase(arrCities[i].name))
				startEnd[1] = i;
		}
		userIn.close();
		return startEnd;
	}
	
	
	static CityNode[] arrayAdjust(CityNode[] arr, int[] startEnd, int arrLength)
	{
		CityNode[] newArr = Arrays.copyOf(arr, arrLength);
		CityNode startTemp = new CityNode(arr[startEnd[0]]);
		CityNode endTemp = new CityNode(arr[startEnd[1]]);
		
		newArr[startEnd[0]] = arr[0];
		newArr[0] = startTemp;
		newArr[arrLength-1] = endTemp;
		
		return newArr;
	}
	
	static public Graph createGraph(Graph G, CityNode[] arrCities, int[] startEnd)
    {
		int numNodes = arrCities.length;
		int dupe = 0;
		
		G.Init(numNodes);
		
		for (int i = 0; i < numNodes; i++)
			G.setMark(i, UNVISITED);
		
		G.setMark(G.getLastNode(), VISITED); //Last city must be marked as visited to avoid being visited prematurely.
		
		if (startEnd[2] == 1)
		{
			for (int i = 0; i < numNodes-1; i++)
				if (arrCities[i].name.equalsIgnoreCase(arrCities[numNodes-1].name))
					dupe = i;
			G.setMark(dupe, VISITED);
		}
		
		for(int i = 0; i < numNodes; i++)
			for(int j = 0 ; j < numNodes; j++)
			{
				double weight = CityNode.getDistance(arrCities[i], arrCities[j]);
				G.setEdge(i, j, weight);
				//System.out.printf("The distance from %-18s and %-18s is: %2fkm\n", 
						//arrCities[i].name, arrCities[j].name, weight);
			}
		return G;
    }
	
	/*
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
		//int n = arrCities.length;
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
	*/
	
	/*
	 * arrayAdjust swaps the first node (alphabetical) with the user-selected starting city node.
	 * It also swaps the last node (alphabetical) with the user-selected ending city node.
	 * This is done so that when the DFS recursive method is called (which visits nodes in order of array index)
	 * it will automatically visit the starting city first, and ending city last.
	 */
	
	
	
	static void graphTraverse(Graph G, CityNode[] arr) 
	{
		System.out.println("\n=-BEGIN TRAVELLING-=\n");
		int v;
		
		for (v = 0; v <G.n(); v++)
			if (G.getMark(v) == UNVISITED)
				doTraverse(G, v, arr);
	}

	static void doTraverse(Graph G, int v, CityNode[] arr) 
	{DFS(G, v, arr);}
	
	/*
	 * Improved loop first uses the G.getLeast Graphm function to visit the node for that city with the shortest distance.
	 * If that node has already been visited by a different city, find the next least weighted city.
	 * For the very last city, a special identifier was made (LAST_VISIT) that is checked so that the last city...
	 * is not visited prematurely.
	 */
	
	static void DFS(Graph G, int v, CityNode[] arr) 
	{
		G.setMark(v, VISITED);
		int index = G.getLeast(v);
		int x = G.first(v);
		
		
		//GREEDY TRAVERSAL ALGORITHM
		
		while (x < G.n())
		{
			if (G.getMark(index) == UNVISITED)
			{
				System.out.printf("Going from %-18s to %-18s was %-2.2fkm\n", arr[v].name, arr[index].name, G.weight(v, index));
				DFS(G, index, arr);
				total_weight += G.weight(v, index);
			}
			else if (G.getMark(x) == UNVISITED)
			{
				System.out.printf("Going from %-18s to %-18s was %-2.2fkm\n", arr[v].name, arr[x].name, G.weight(v, x));
				DFS(G, x, arr);
				total_weight += G.weight(v, x);
			}
			else if (x == G.getLastNode() && G.getMark(G.getLastNode()) != LAST_VISIT)
			{
				G.setMark(G.getLastNode(), LAST_VISIT);
				System.out.printf("Going from %-18s to %-18s was %-2.2fkm\n", arr[v].name, arr[x].name, G.weight(v, x));
				total_weight += G.weight(v, x);
			}
			
			x = G.next(v, x);
		}
		
		
		/* ALPHABETICAL ORDER TRAVERSAL (RANDOM)

		for (int w = G.first(v); w < G.n() ; w = G.next(v, w))
			if (G.getMark(w) == UNVISITED)
			{
					System.out.printf("Going from %-18s to %-18s was %-2.2fkm\n", arr[v].name, arr[w].name, G.weight(v, w));
					DFS(G, w, arr);
					total_weight += G.weight(v, w);
		    }
		*/
	}
}