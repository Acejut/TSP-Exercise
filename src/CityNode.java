
public class CityNode 
{
	public String name;
	public double lat, lon;
	
	CityNode(String name, double lat, double lon)
	{
		this.name = name;
		this.lat = lat;
		this.lon = lon;
	}

	public static double getDistance(CityNode city1, CityNode city2)
	{
		var R = 6371; // Radius of the earth in km
		var dLat = deg2rad(city2.lat-city1.lat); // deg2rad below
		var dLon = deg2rad(city2.lon-city1.lon);
		var a = Math.sin(dLat/2) * Math.sin(dLat/2) + Math.cos(deg2rad(city1.lat)) * Math.cos(deg2rad(city2.lat)) * Math.sin(dLon/2) * Math.sin(dLon/2);
		var c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
		var d = R * c; // Distance in km
		return d;
	}
	
	public static double deg2rad(double deg)
	{
		return (deg * Math.PI/180);
	}
}
