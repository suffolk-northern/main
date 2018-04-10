/*
 * Roger Xue
 *
 * Beacon
 */
package track_model;

public class Beacon {

	// Location of beacon.
	protected GlobalCoordinates location;
	// Beacon message.
	protected String message;

	/**
	 * Instantiates beacon.
	 *
	 * @param location
	 * @param message
	 */
	public Beacon(GlobalCoordinates location, String message) {
		this.location = location;
		this.message = message;
	}

	public GlobalCoordinates getLocation() {
		return location;
	}

	protected void setLocation(GlobalCoordinates location) {
		this.location = location;
	}

	public String getMessage() {
		return message;
	}

	protected void setMessage(String message) {
		this.message = message;
	}

}
