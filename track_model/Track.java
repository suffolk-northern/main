/*
 * Kaylene
 */
package track_model;

/**
 *
 * @author Fenne
 */
public class Track {
	public int numSections;
	public TrackSection sections[];
	
	public Track()
	{
		
	}
	
	public Track(int newNum, TrackSection newSections[])
	{
		numSections = newNum;
		sections = newSections[];
	}
}
