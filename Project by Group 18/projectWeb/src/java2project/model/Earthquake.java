package java2project.model;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * This is a model class. which define some attributes of the earthquake.
 */
public class Earthquake {

	private final IntegerProperty id;
	private final StringProperty UTC_date;
	private final DoubleProperty latitude;
	private final DoubleProperty longitude;
	private final IntegerProperty depth;
	private final DoubleProperty magnitude;
	private final StringProperty region;

	public static final String WORLDWIDE = "--- World Wide ---";

	/**
	 * constructor1
	 * @param id : id of this earthquake.
	 * @param date: date of this earthquake.
	 * @param latitude: latitude of this earthquake.
	 * @param longitude: longitude of this earthquake.
	 * @param depth: depth of this earthquake.
	 * @param magnitude: magnitude of this earthquake.
	 * @param region: region of this earthquake.
	 * 
	 */
	public Earthquake(int id, String date, float latitude, float longitude, int depth, float magnitude, String region) {
		this.id = new SimpleIntegerProperty(id);
		this.UTC_date = new SimpleStringProperty(date);
		this.latitude = new SimpleDoubleProperty(latitude);
		this.longitude = new SimpleDoubleProperty(longitude);
		this.depth = new SimpleIntegerProperty(depth);
		this.magnitude = new SimpleDoubleProperty(magnitude);
		this.region = new SimpleStringProperty(region);
	}

	/**
	 * constructor2
	 * @param id : id of this earthquake.
	 * @param uTC_date : date of this earthquake.
	 * @param latitude : latitude of this earthquake.
	 * @param longitude : longitude of this earthquake.
	 * @param depth : depth of this earthquake.
	 * @param magnitude : magnitude of this earthquake.
	 * @param region : region of this earthquake.
	 */
	public Earthquake(IntegerProperty id, StringProperty uTC_date, DoubleProperty latitude, DoubleProperty longitude,
			IntegerProperty depth, DoubleProperty magnitude, StringProperty region) {
		super();
		this.id = id;
		UTC_date = uTC_date;
		this.latitude = latitude;
		this.longitude = longitude;
		this.depth = depth;
		this.magnitude = magnitude;
		this.region = region;
	}

	/**
	 * constructor3
	 * @param string : id of this earthquake.
	 * @param string2 : date of this earthquake.
	 * @param string3 : latitude of this earthquake.
	 * @param string4 : longitude of this earthquake.
	 * @param string5 : depth of this earthquake.
	 * @param string6 : magnitude of this earthquake.
	 * @param string7 : region of this earthquake.
	 */
	public Earthquake(String string, String string2, String string3, String string4, String string5, String string6,
			String string7) {
		this(new SimpleIntegerProperty(Integer.parseInt(string)), new SimpleStringProperty(string2),
				new SimpleDoubleProperty(Double.parseDouble(string3)),
				new SimpleDoubleProperty(Double.parseDouble(string4)),
				new SimpleIntegerProperty(Integer.parseInt(string5)),
				new SimpleDoubleProperty(Double.parseDouble(string6)), new SimpleStringProperty(string7));
	}

	/**
	 * 
	 * @return id of this earthquake.
	 */
	public IntegerProperty getId() {
		return id;
	}

	/**
	 * 
	 * @return UTC date of this earthquake.
	 */
	public StringProperty getUTC_date() {
		return UTC_date;
	}

	/**
	 * 
	 * @return latitude of this earthquake.
	 */
	public DoubleProperty getLatitude() {
		return latitude;
	}

	/**
	 * 
	 * @return longitude of this earthquake.
	 */
	public DoubleProperty getLongitude() {
		return longitude;
	}

	/**
	 * 
	 * @return depth of this earthquake.
	 */
	public IntegerProperty getDepth() {
		return depth;
	}

	/**
	 * 
	 * @return magnitude of this earthquake.
	 */
	public DoubleProperty getMagnitude() {
		return magnitude;
	}

	/**
	 * 
	 * @return region of this earthquake.
	 */
	public StringProperty getRegion() {
		return region;
	}

	/**
	 * 
	 * @param id
	 *            : id of this earthquake.<br>
	 *            set id of this earthquake.
	 */
	public void setId(int id) {
		this.id.set(id);
	}

	/**
	 * 
	 * @param uTC_date:
	 *            UTC date of this earthquake.<br>
	 *            set date of this earthquake.
	 */
	public void setUTC_date(String uTC_date) {
		UTC_date.set(uTC_date);
	}

	/**
	 * 
	 * @param latitude:
	 *            latitude of this earthquake.<br>
	 *            set latitude of this earthquake.
	 */
	public void setLatitude(double latitude) {
		this.latitude.set(latitude);
	}

	/**
	 * 
	 * @param longitude:
	 *            longitude of this earthquake.<br>
	 *            set longitude of this earthquake.
	 */
	public void setLongitude(double longitude) {
		this.longitude.set(longitude);
	}

	/**
	 * 
	 * @param depth:
	 *            depth of this earthquake.<br>
	 *            set depth of this earthquake.
	 */
	public void setDepth(int depth) {
		this.depth.set(depth);
		;
	}

	/**
	 * 
	 * @param magnitude:
	 *            magnitude of this earthquake.<br>
	 *            set magnitude of this earthquake.
	 */
	public void setMagnitude(double magnitude) {
		this.magnitude.set(magnitude);
	}

	/**
	 * 
	 * @param region:
	 *            region of this earthquake.<br>
	 *            set region of this earthquake.
	 */
	public void setRegion(String region) {
		this.region.set(region);
	}
}