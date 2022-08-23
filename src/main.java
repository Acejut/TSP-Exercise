//Justin Ruiz

import java.util.*;
import java.io.*;

public class main 
{
	static double total_weight = 0;
	static double running_lowest = Double.MAX_VALUE;
	static double homeDist = 0;
	static Graph G;
	static LinkedList<CityNode> arrCities;
	static LinkedList<String> salesmanPath = new LinkedList<>();
	
	static int[][] memo;
	static int n;
	static int MAX = 10000;
	
	static final int UNVISITED = 0;
	static final int VISITED = 1;
	
	public static void main(String[] args) 
	{
		initList();
		initGraph();
		graphTraverse();
	}

	static void initList() 
	{
		File towns = new File("src/LATowns.txt");
		arrCities = new LinkedList<>();
		ArrayList<String> nameList = new ArrayList<>();
		
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
				
				arrCities.add(new CityNode(name, lat, lon, pop));
				nameList.add(name.toLowerCase());
			}
			scan.close();
		}
		
		catch (FileNotFoundException p)
		{System.out.println("LATowns.txt FILE NOT FOUND.");}
		
		Scanner userIn = new Scanner(System.in);
		String startCity;
		
		System.out.println("Please enter the name of the starting city:");
		startCity = userIn.nextLine().toLowerCase();
		
		if (nameList.contains(startCity))
		{
			int index = nameList.indexOf(startCity);
			CityNode t1 = new CityNode(arrCities.getFirst());
			CityNode t2 = new CityNode(arrCities.get(index));

			arrCities.set(0, t2);
			arrCities.set(index, t1);
		}
		
		else
		{
			System.out.println("Cannot find city.");
			System.exit(0);
		}
	}
	
	static void initGraph()
	{
		G = new Graph();
		int size = arrCities.size();
		G.Init(size);

		for (int i = 0; i < G.n(); i++)
		{
			G.setMark(i, UNVISITED);
			G.setName(i, arrCities.get(i).name);
		}
		
		for(int i = 0; i < size; i++)
			for(int j = 0 ; j < size; j++)
				G.setEdge(i, j, CityNode.getDistance(arrCities.get(i), arrCities.get(j)));
	}
	
	static void graphTraverse()
	{
		Scanner ui = new Scanner(System.in);
    	
    	System.out.println("Which algorithm would you like to use?");
    	System.out.println("1) Alphabetical (random)");
    	System.out.println("2) Greedy Algorithm");
    	System.out.println("3) Permutation Algorithm");
    	
    	int choice = ui.nextInt();
    	long startTime, endTime;
    	double totalTime;
    	startTime = System.nanoTime();
    	
    	switch(choice)
    	{
    	default:
			System.out.println("INVALID CHOICE");
			break;
		case 1:
			for (int v = 0; v <G.n(); v++)
				if (G.getMark(v) == UNVISITED)
					DFSAlpha(v);
			returnHome();
			resultPrint();
			break;
		case 2:
			for (int v = 0; v <G.n(); v++)
				if (G.getMark(v) == UNVISITED)
					DFSGreed(v);
			returnHome();
			resultPrint();
			break;
		case 3:
			n = G.n()-1;
			memo = new int[n + 1][1 << (n + 1)];
			dynProg();
			break;
    	}
    	endTime = System.nanoTime();
    	totalTime = (double) (endTime - startTime) / 1000000;
    	System.out.println("\nAlgorithm took " + totalTime + "ms to process.");
	}
	
	static void DFSAlpha(int v) 
	{
		G.setMark(v, VISITED);
		int x = G.first(v);

		while (x < G.n())
		{
			if (G.getMark(x) == UNVISITED)
			{
				System.out.printf("Going from %-18s to %-18s was %-2.2fkm\n", G.getName(v), G.getName(x), G.weight(v, x));
				total_weight += G.weight(v, x);
				DFSAlpha(x);
		    }
			
			x = G.next(v, x);
		}
		
		salesmanPath.addLast(G.getName(v));
	}
	
	static void DFSGreed(int v) 
	{
		G.setMark(v, VISITED);
		int index = G.getLeast(v);
		int x = G.first(v);
		
		while (x < G.n())
		{
			if (G.getMark(index) == UNVISITED)
			{
				System.out.printf("Going from %-18s to %-18s was %-2.2fkm\n", G.getName(v), G.getName(index), G.weight(v, index));
				total_weight += G.weight(v, index);
				DFSGreed(index);
			}
			
			x = G.next(v, x);
		}
		
		salesmanPath.addLast(G.getName(v));
	}
	
	static void returnHome()
	{
		String lastCityName = salesmanPath.getFirst();
		String homeCityName = salesmanPath.getLast();
		int lastCityIndex, homeCityIndex;
		lastCityIndex = homeCityIndex = 0;
		
		for (int i = 0; i < arrCities.size(); i++)
		{
			if (arrCities.get(i).name.equals(lastCityName))
				lastCityIndex = i;
			else if (arrCities.get(i).name.equals(homeCityName))
				homeCityIndex = i;
		}
		
		homeDist = CityNode.getDistance(arrCities.get(lastCityIndex), arrCities.get(homeCityIndex));
		total_weight += homeDist;
	}
	
	static void dynProg()
	{
		int ans = MAX;
        for (int i = 1; i <= n; i++)
            // try to go from node 1 visiting all nodes in
            // between to i then return from i taking the
            // shortest route to 1
            ans = Math.min(ans, fun(i, (1 << (n + 1)) - 1)
                                    + (int) G.matrix[i][1]);
  
        System.out.printf("The total distance traveled starting from %s was %dkm ", arrCities.getFirst().name, ans);
	}
	
	static int fun(int i, int mask)
	{
		// base case
        // if only ith bit and 1st bit is set in our mask,
        // it implies we have visited all other nodes
        // already
        if (mask == ((1 << i) | 3))
            return (int) G.matrix[1][i];
        // memoization
        if (memo[i][mask] != 0)
            return memo[i][mask];
  
        int res = MAX; // result of this sub-problem
  
        // we have to travel all nodes j in mask and end the
        // path at ith node so for every node j in mask,
        // recursively calculate cost of travelling all
        // nodes in mask
        // except i and then travel back from node j to node
        // i taking the shortest path take the minimum of
        // all possible j nodes
  
        for (int j = 1; j <= n; j++)
            if ((mask & (1 << j)) != 0 && j != i && j != 1)
                res = Math.min(res,
                               fun(j, mask & (~(1 << i)))
                                   + (int) G.matrix[j][i]);
        return memo[i][mask] = res;
	}
	
	static void resultPrint()
	{
		Iterator<String> x = salesmanPath.descendingIterator();
		
		System.out.println();
		while (x.hasNext())
			System.out.printf("%s -> ", x.next());
		System.out.print(arrCities.getFirst().name);
		System.out.printf("\nTotal distance traveled = %-5.2fkm", total_weight);
	}

}
