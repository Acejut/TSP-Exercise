//Justin Ruiz

import java.util.*;
import java.io.*;

public class main 
{
	static double total_weight = 0;
	static double running_lowest = Double.MAX_VALUE;
	static Graph G;
	static LinkedList<CityNode> arrCities;
	
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
	}
	
	static void initGraph()
	{
		G = new Graph();
		int size = arrCities.size();
		
		G.Init(size);
		
		for (int i : G.Mark)
			G.setMark(i, UNVISITED);
		
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
    	
    	for (int v = 0; v <G.n(); v++)
			if (G.getMark(v) == UNVISITED)
			{
				switch(ui.nextInt())
				{
				default:
					System.out.println("INVALID CHOICE");
				case 1:
					doTraverseAlpha(v);
					break;
				case 2:
					doTraverseGreed(v);
					break;
				}
			}
	}
	
	static void doTraverseAlpha(int v) 
	{
		total_weight = 0;
		DFSAlpha(v);
		System.out.printf("\nTotal sum of weight = %-5.2fkm", total_weight);
		
		if (total_weight < running_lowest)
			running_lowest = total_weight;
	}
	
	static void doTraverseGreed(int v) 
	{
		total_weight = 0;
		DFSGreed(v);
		System.out.printf("\nTotal sum of weight = %-5.2fkm", total_weight);
		
		if (total_weight < running_lowest)
			running_lowest = total_weight;
	}
	
	static void DFSAlpha(int v) 
	{
		G.setMark(v, VISITED);
		int x = G.first(v);

		while (x < G.n())
		{
			if (G.getMark(x) == UNVISITED)
			{
				System.out.printf("Going from %-18s to %-18s was %-2.2fkm\n", arrCities.get(v).name, arrCities.get(x).name, G.weight(v, x));
				total_weight += G.weight(v, x);
				DFSAlpha(x);
		    }
			
			x = G.next(v, x);
		}
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
				System.out.printf("Going from %-18s to %-18s was %-2.2fkm\n", arrCities.get(v).name, arrCities.get(index).name, G.weight(v, index));
				total_weight += G.weight(v, index);
				DFSGreed(index);
			}
			else if (G.getMark(x) == UNVISITED)
			{
				System.out.printf("Going from %-18s to %-18s was %-2.2fkm\n", arrCities.get(v).name, arrCities.get(x).name, G.weight(v, x));
				total_weight += G.weight(v, x);
				DFSGreed(x);
			}
			
			x = G.next(v, x);
		}
	}

}
