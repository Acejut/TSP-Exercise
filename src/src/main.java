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
	    
		if (startEnd[0] == -1 || startEnd[1] == -1)
	    	System.out.println("City not found.");
	    else
	    {
	    	/*
	    	long startTime, endTime;
	    	double totalTime;
	    	
	    	startTime = System.nanoTime();
	    	*/
	    	
	    	arrCities = arrayAdjust(arrCities, startEnd, arrCities.length);
	    	G = createGraph(G, arrCities, startEnd);
	    	
	    	Scanner ui = new Scanner(System.in);
			int choice = -1;
	    	
	    	System.out.println("Which algorithm would you like to use?");
	    	System.out.println("1) Alphabetical (random)");
	    	System.out.println("2) Greedy Algorithm");
	    	choice = ui.nextInt();
	    	
	    	if (choice == 1)
	    		graphTraverseAlpha(G, arrCities);
	    	else if (choice == 2)
	    		graphTraverseGreed(G, arrCities);
	    	else
	    		System.out.println("Invalid choice.");
	    	/*
	    	endTime = System.nanoTime();
	    	totalTime = (double) (endTime - startTime) / 1000000;
	    	System.out.println(totalTime + "ms");
	    	*/
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
		//userIn.close();
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
			}
		return G;
    }
	
	/*
	 * arrayAdjust swaps the first node (alphabetical) with the user-selected starting city node.
	 * It also swaps the last node (alphabetical) with the user-selected ending city node.
	 * This is done so that when the DFS recursive method is called (which visits nodes in order of array index)
	 * it will automatically visit the starting city first, and ending city last.
	 */
	
	static void graphTraverseAlpha(Graph G, CityNode[] arr) 
	{
		System.out.println("\n=-BEGIN TRAVELLING-=\n");
		int v;
		
		for (v = 0; v <G.n(); v++)
			if (G.getMark(v) == UNVISITED)
				doTraverseAlpha(G, v, arr);
	}
	
	static void graphTraverseGreed(Graph G, CityNode[] arr) 
	{
		System.out.println("\n=-BEGIN TRAVELLING-=\n");
		int v;
		
		for (v = 0; v <G.n(); v++)
			if (G.getMark(v) == UNVISITED)
				doTraverseGreed(G, v, arr);
	}

	static void doTraverseAlpha(Graph G, int v, CityNode[] arr) 
	{DFSAlpha(G, v, arr);}
	
	static void doTraverseGreed(Graph G, int v, CityNode[] arr) 
	{DFSGreed(G, v, arr);}
	
	/*
	 * Improved loop first uses the G.getLeast Graphm function to visit the node for that city with the shortest distance.
	 * If that node has already been visited by a different city, find the next least weighted city.
	 * For the very last city, a special identifier was made (LAST_VISIT) that is checked so that the last city...
	 * is not visited prematurely.
	 */
	
	static void DFSAlpha(Graph G, int v, CityNode[] arr) 
	{
		G.setMark(v, VISITED);
		int x = G.first(v);

		while (x < G.n())
		{
			if (G.getMark(x) == UNVISITED)
			{
					System.out.printf("Going from %-18s to %-18s was %-2.2fkm\n", arr[v].name, arr[x].name, G.weight(v, x));
					DFSAlpha(G, x, arr);
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
	}
	
	static void DFSGreed(Graph G, int v, CityNode[] arr) 
	{
		G.setMark(v, VISITED);
		int index = G.getLeast(v);
		int x = G.first(v);
		
		while (x < G.n())
		{
			if (G.getMark(index) == UNVISITED)
			{
				System.out.printf("Going from %-18s to %-18s was %-2.2fkm\n", arr[v].name, arr[index].name, G.weight(v, index));
				DFSGreed(G, index, arr);
				total_weight += G.weight(v, index);
			}
			else if (G.getMark(x) == UNVISITED)
			{
				System.out.printf("Going from %-18s to %-18s was %-2.2fkm\n", arr[v].name, arr[x].name, G.weight(v, x));
				DFSGreed(G, x, arr);
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
	}
}