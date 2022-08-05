import java.io.*;
import java.util.*;

//Justin Ruiz

/* TODO: Integrate Q-Learning somehow */

public class main 
{
	static double total_weight = 0;
	static double running_lowest = Double.MAX_VALUE;
	static int[] startEnd;
	static HashMap<Double, CityNode[]> cityDist = new HashMap<Double, CityNode[]>();
	static final int UNVISITED = 0;
	static final int VISITED = 1;
	static final int LAST_VISIT = 2;
	
	
	public static void main(String[] args) throws IOException
	{
		Graph G = new Graphm();
		CityNode[] arrCities = null;
		
		arrCities = createCityArr(arrCities);
	    
		startEnd = cityGrab(arrCities);
	    
		if (startEnd[0] == -1 || startEnd[1] == -1)
	    	System.out.println("City not found.");
	    else
	    {
	    	long startTime, endTime;
	    	double totalTime;
	    	
	    	arrCities = arrayAdjust(arrCities, arrCities.length);
	    	G = createGraph(G, arrCities);
	    	
	    	Scanner ui = new Scanner(System.in);
			int choice = -1;
	    	
	    	System.out.println("Which algorithm would you like to use?");
	    	System.out.println("1) Alphabetical (random)");
	    	System.out.println("2) Greedy Algorithm");
	    	System.out.println("3) Permutation Algorithm");
	    	choice = ui.nextInt();
	    	
	    	startTime = System.nanoTime();
	    	graphTraverse(G, arrCities, choice);
	    	
	    	
	    	endTime = System.nanoTime();
	    	totalTime = (double) (endTime - startTime) / 1000000;
	    	System.out.println("\nAlgorithm took " + totalTime + "ms to process.");
	    	
	    	if (choice == 3)
	    	{
		    	System.out.printf("\nShortest path was %-5.2fkm, being", running_lowest);
		    	
		    	for (int i = 0; i < arrCities.length; i++)
		    	{
		    		System.out.printf(" %s ", cityDist.get(running_lowest)[i].name);
		    		if (i != arrCities.length-1)
		    			System.out.print("->");
		    	}
	    	}
	    }
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
	
	/*
	 * arrayAdjust swaps the 0th index city with the chosen starting city,
	 * then it places the ending city at the end of the array.
	 * later, in createGraph, the duplicate first entry of the selected end city
	 * is flagged "visited" so that it doesn't get visited at all.
	 */
	
	static CityNode[] arrayAdjust(CityNode[] arr, int arrLength)
	{
		CityNode[] newArr = Arrays.copyOf(arr, arrLength);
		CityNode startTemp = new CityNode(arr[startEnd[0]]);
		CityNode endTemp = new CityNode(arr[startEnd[1]]);
		
		newArr[startEnd[0]] = arr[0];
		newArr[0] = startTemp;
		newArr[arrLength-1] = endTemp;
		
		return newArr;
	}
	
	static public Graph createGraph(Graph G, CityNode[] arrCities)
    {
		int numNodes = arrCities.length;
		int dupePos = 0;
		
		G.Init(numNodes);
		
		for (int i = 0; i < numNodes; i++)
			G.setMark(i, UNVISITED);
		
		//Last city must be marked as visited to avoid being visited prematurely.
		G.setMark(G.getLastNode(), VISITED); 
		
		//If the start and end city are the same, mark the first instance of the city "visited".
		if (startEnd[2] == 1)
		{
			for (int i = 0; i < numNodes-1; i++)
				if (arrCities[i].name.equalsIgnoreCase(arrCities[numNodes-1].name))
					dupePos = i;
			G.setMark(dupePos, VISITED);
		}
		
		for(int i = 0; i < numNodes; i++)
			for(int j = 0 ; j < numNodes; j++)
			{
				double weight = CityNode.getDistance(arrCities[i], arrCities[j]);
				G.setEdge(i, j, weight);
			}
		return G;
    }
	
	//Determine which algorithm to run
	static void graphTraverse(Graph G, CityNode[] arr, int choice) 
	{
		System.out.println("\n=-BEGIN TRAVELLING-=\n");
		int v;
		
		for (v = 0; v <G.n(); v++)
			if (G.getMark(v) == UNVISITED)
			{
				switch(choice)
				{
				case 1:
					doTraverseAlpha(G, v, arr);
					break;
				case 2:
					doTraverseGreed(G, v, arr);
					break;
				case 3:
					CityNode[] arrModded = Arrays.copyOfRange(arr, 1, arr.length-1);
					permutateArray(arrModded.length, arrModded, arr);
					break;
				}
			}
	}
	
	//find all permutations of the original array and create a map for each one
	static void permutateArray(int n, CityNode[] arrModded, CityNode[] og)
	{
		if (n == 1)
		{
			doTraversePerm(arrModded, og);
		}
		else 
		{
			for (int i = 0; i < n-1; i++) 
			{
				permutateArray(n - 1, arrModded, og);
				if (n % 2 == 0)
					swap(arrModded, i, n-1);
				else
					swap(arrModded, 0, n-1);
	        }
			permutateArray(n - 1, arrModded, og);
		}
	}
	
	//swap function for the permuteArray function
	private static void swap(CityNode[] input, int a, int b) 
	{
		    CityNode tmp = input[a];
		    input[a] = input[b];
		    input[b] = tmp;
	}
	
	//create a map for the permutated array
	private static void doTraversePerm(CityNode[] input, CityNode[] og) 
	{
		Graph G = new Graphm();
		
		for (int i = 1; i < og.length-1; i++)
			og[i] = input[i-1];
		
		G = createGraph(G, og);
		graphTraverse(G, og, 1);
	}
		
	
	static void doTraverseAlpha(Graph G, int v, CityNode[] arr) 
	{
		total_weight = 0;
		DFSAlpha(G, v, arr);
		System.out.printf("\nTotal sum of weight = %-5.2fkm", total_weight);
		
		cityDist.put(total_weight, arr);
		
		if (total_weight < running_lowest)
			running_lowest = total_weight;
	}
	
	static void doTraverseGreed(Graph G, int v, CityNode[] arr) 
	{
		total_weight = 0;
		DFSGreed(G, v, arr);
		System.out.printf("\nTotal sum of weight = %-5.2fkm", total_weight);
		
		cityDist.put(total_weight, arr);
		
		if (total_weight < running_lowest)
			running_lowest = total_weight;
	}
	/*
	 * Random algo visits each node by array order. In the base case array, this is alphabetical
	 * This algo is also used for the permutation algo, as it serves the same function
	 * once a new permutated array is fed into the params.
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
				total_weight += G.weight(v, x);
				DFSAlpha(G, x, arr);
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
	
	/*
	 * Improved greed algo first uses the G.getLeast Graphm function to visit the node for that city with the shortest distance.
	 * If that node has already been visited by a different city, find the next least weighted city.
	 * For the very last city, a special identifier was made (LAST_VISIT) that is checked so that the last city...
	 * is not visited prematurely.
	 */
	
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
				total_weight += G.weight(v, index);
				DFSGreed(G, index, arr);
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