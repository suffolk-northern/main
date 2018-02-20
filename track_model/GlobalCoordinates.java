/*
 * Ryan Matthews
 *
 * Geometry
 */

package track_model;

// Location of an object on the globe of the Earth

public class GlobalCoordinates
{
	// radius of the Earth in yards
	//
	//   = radius in miles * yard/mi
	//
	//   radius in miles from Wikipedia
	private static final double radius = 3958.8 * 1760;

	// stored as degrees
	private final double latitude;
	private final double longitude;

	// Constructs a GlobalCoordinates object as a copy of another.
	public GlobalCoordinates(GlobalCoordinates other)
	{
		latitude  = other.latitude;
		longitude = other.longitude;
	}

	// Constructs a GlobalCoordinates object from a latitude coordinate and
	// a longitude coordinate.
	//
	// Parameters latitude and longitude are degrees.
	//
	// Throws IllegalArgumentException if latitude not in [-90.0, 90.0] or
	// longitude not in [-180.0, 180.0].
	public GlobalCoordinates(double latitude, double longitude)
		throws IllegalArgumentException
	{
		if (latitude < -90.0 || latitude > 90.0)
			throw new IllegalArgumentException("latitude bounds");

		if (longitude < -180.0 || longitude > 180.0)
			throw new IllegalArgumentException("longitude bounds");

		this.latitude  = latitude;
		this.longitude = longitude;
	}

	// Returns the latitude coordinate.
	//
	// Units: degrees
	public double latitude()
	{
		return latitude;
	}

	// Returns the longitude coordinate.
	//
	// Units: degrees
	public double longitude()
	{
		return longitude;
	}

	// Returns the great-circle distance between this set of coordinates
	// and another set of coordinates.
	//
	// Great-circle distance is the shortest "straight-line" distance
	// across the surface of a sphere.
	public double distanceTo(GlobalCoordinates other)
	{
		// Wikipedia article "Great-circle distance" says haversine is
		// "numerically better-conditioned for small distances".

		double  latitude0 = Math.toRadians(latitude);
		double  latitude1 = Math.toRadians(other.latitude);

		double longitude0 = Math.toRadians(longitude);
		double longitude1 = Math.toRadians(other.longitude);

		double term1 = hav(latitude0 - latitude1);

		double term2 = Math.cos(latitude0) * Math.cos(latitude1)
		               * hav(longitude0 - longitude1);

		return radius * ahav(term1 + term2);
	}

	// Adds latitude and longitude components in degrees to this set of
	// coordinates.
	//
	// Returns the result.
	//
	// Clips latitude and longitude to legal values, e.g.
	//   (45, 1) + (50, 2) = (90, 3)
	public GlobalCoordinates addDegrees(double latitude, double longitude)
	{
		double clippedLatitude  = this.latitude  + latitude;
		double clippedLongitude = this.longitude + longitude;

		if (clippedLatitude < -90.0) clippedLatitude = -90.0;
		if (clippedLatitude >  90.0) clippedLatitude =  90.0;

		if (clippedLongitude < -180.0) clippedLongitude = -180.0;
		if (clippedLongitude >  180.0) clippedLongitude =  180.0;

		return new GlobalCoordinates(clippedLatitude, clippedLongitude);
	}

	// Subtracts latitude and longitude components in degrees from this set
	// of coordinates.
	//
	// Returns the result.
	//
	// Clips latitude and longitude to legal values, e.g.
	//   (-45, 1) - (50, 2) = (-90, -1)
	public GlobalCoordinates subtractDegrees(double latitude,
	                                         double longitude)
	{
		return addDegrees(-latitude, -longitude);
	}

	// Adds latitude and longitude components in yards to this set of
	// coordinates.
	//
	// Returns the result.
	//
	// Do not use for long distances (e.g. 100 miles).
	//
	// Clips latitude and longitude to legal values, e.g.
	//   (45, 1) + (50, 2) = (90, 3)
	public GlobalCoordinates addYards(double latitude, double longitude)
	{
		final double radiansPerYard = radius;

		double  latitudeIncrease =  latitude / radius * 180.0 / Math.PI;
		double longitudeIncrease = longitude / radius * 180.0 / Math.PI;

		longitudeIncrease /= Math.cos(this.latitude * Math.PI / 180.0);

		return new GlobalCoordinates(
			this.latitude  +  latitudeIncrease,
			this.longitude + longitudeIncrease
		);
	}

	// Subtracts latitude and longitude components in yards from this set
	// of coordinates.
	//
	// Returns the result.
	//
	// Do not use for long distances (e.g. 100 miles).
	//
	// Clips latitude and longitude to legal values, e.g.
	//   (-45, 1) - (50, 2) = (-90, -1)
	public GlobalCoordinates subtractYards(double latitude,
	                                       double longitude)
	{
		return addYards(-latitude, -longitude);
	}

	// Adds another set of coordinates to this set of coordinates.
	//
	// Returns the result.
	//
	// Clips latitude and longitude to legal values, e.g.
	//   (45, 1) + (50, 2) = (90, 3)
	public GlobalCoordinates add(GlobalCoordinates other)
	{
		return addDegrees(other.latitude, other.longitude);
	}

	// Subtracts another set of coordinates from this set of coordinates.
	//
	// Returns the result.
	//
	// Clips latitude and longitude to legal values, e.g.
	//   (-45, 1) - (50, 2) = (-90, -1)
	public GlobalCoordinates subtract(GlobalCoordinates other)
	{
		return subtractDegrees(other.latitude, other.longitude);
	}

	// haversine function (see Wikipedia)
	private static double hav(double theta)
	{
		return (1.0 - Math.cos(theta)) / 2.0;
	}

	// inverse haversine function (see Wikipedia)
	private static double ahav(double h)
	{
		return 2.0 * Math.asin(Math.sqrt(h));
	}
}
