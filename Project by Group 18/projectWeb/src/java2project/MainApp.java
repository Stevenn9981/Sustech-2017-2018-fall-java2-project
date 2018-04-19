package java2project;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.TreeSet;

import java2project.model.Earthquake;
import java2project.view.EarthquakeOverviewController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.helper.Validate;

/**
 * This is the main class of this project.
 * @author Ning Wentao, Huang Qiaoyou, Zhu Xinyu from Group JAVA2_PROJ 18.<br>
 */
public class MainApp extends Application {
	/**
	 * 
	 */
	private Stage primaryStage;

	private final ArrayList<Earthquake> earthQuakes = new ArrayList<Earthquake>();
	private final TreeSet<String> regionSet = new TreeSet<String>();
	private String minDate = "9999-12-31";
	private String maxDate = "0000-01-01";
	Connection con = null;

	/**
	 * 
	 * @return the minimal date of all earthquakes in the database.
	 */
	public String getMinDate() {
		return minDate;
	}

	/**
	 * 
	 * @return the maximal date of all earthquakes in the database.
	 */
	public String getMaxDate() {
		return maxDate;
	}

	/**
	 * 
	 * @return a region set of all earthquakes in the database.
	 */
	public TreeSet<String> getRegions() {
		return regionSet;
	}

	/**
	 * 
	 * @return a array list of all earthquakes in the database.
	 */
	public ArrayList<Earthquake> getEarthQuakes() {
		return earthQuakes;
	}

	/**
	 * Constructor of the mainapp.
	 */
	public MainApp() {
		webscrap();
		readQuakes();
	}

	/**
	 * 
	 * scrap data from website and insert data into database.
	 */
	public void webscrap() {

		try {
			// CLASSPATH must be properly set, for instance on
			// a Linux system or a Mac:
			// $ export CLASSPATH=.:sqlite-jdbc-version-number.jar
			//
			Class.forName("org.sqlite.JDBC");
		} catch (Exception e) {
			System.err.println("Cannot find the driver.");
			System.exit(1);
		}

		try {
			con = DriverManager.getConnection("jdbc:sqlite:earthquakes-1.sqlite");
			con.setAutoCommit(false);
			PreparedStatement stmt;

			Document doc = Jsoup.connect("https://www.emsc-csem.org/Earthquake/?view=1").get();
			Elements tables = doc.select("tbody#tbody");

			String sql = " insert or ignore into quakes(id,UTC_date," + "latitude,longitude,"
					+ "depth,magnitude,region)" + "values(?,?," + "case ? when 'N' then 1 else -1 end * ?,"
					+ "case ? when 'W' then -1 else 1 end * ?," + "?,?,?)";
			stmt = con.prepareStatement(sql);

			for (Element t : tables) {
				Elements rows = t.select("tr");
				for (Element r : rows) {
					Elements cells = r.select("td");
					if (cells.size() == 13) {
						String id = r.id();
						Elements time = cells.select("td.tabev6 a");
						String tim = time.get(0).text();
						Elements latitude = cells.select("td.tabev1");
						String lat = latitude.get(0).text();
						String lon = latitude.get(1).text();

						Elements depth = cells.select("td.tabev3");
						String dep = depth.get(0).text();

						Elements mags = cells.select("td.tabev2");
						String NorS = mags.get(0).text();
						String WorE = mags.get(1).text();
						String mag = mags.get(2).text();

						Elements regionName = cells.select("td.tb_region");
						String region = regionName.get(0).text();

						// System.out.println(id +
						// "\t"+tim+"\t"+lat+NorS+"\t"+lon+WorE+"\t"+dep+"\t"+mag+"\t"+region);
						stmt.setString(1, id);
						stmt.setString(2, tim);
						stmt.setString(3, NorS);
						stmt.setString(4, lat);
						stmt.setString(5, WorE);
						stmt.setString(6, lon);
						stmt.setString(7, dep);
						stmt.setString(8, mag);
						stmt.setString(9, region);

						stmt.executeUpdate();
					}
				}
			}
			con.commit();
		} catch (Exception e) {
			if (con != null) {
				try {
					con.rollback();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		}

	}

	/**
	 * 
	 * read data from database and put data into tableview.
	 */
	public void readQuakes() {
		// Only called once, by the constructor
		String thisDate;
		String thisDay;
		float thisMagnitude;
		String thisRegion;

		Connection con = null;

		try {
			// CLASSPATH must be properly set, for instance on
			// a Linux system or a Mac:
			// $ export CLASSPATH=.:sqlite-jdbc-version-number.jar
			//
			Class.forName("org.sqlite.JDBC");
		} catch (Exception e) {
			System.err.println("Cannot find the driver.");
			System.exit(1);
		}

		try {
			con = DriverManager.getConnection("jdbc:sqlite:earthquakes-1.sqlite");
			con.setAutoCommit(false);
			PreparedStatement stmt;
			ResultSet rs;
			stmt = con.prepareStatement("select * from quakes");
			rs = stmt.executeQuery();
			rs.next();
			regionSet.add(Earthquake.WORLDWIDE);
			while (rs.next()) {

				thisDate = rs.getString(2).replace("\"", "").substring(0, 19);
				thisDay = thisDate.substring(0, 10).trim();
				thisMagnitude = Float.parseFloat(rs.getString(6));
				thisRegion = rs.getString(7).replace("\"", "").trim();
				regionSet.add(thisRegion);
				earthQuakes.add(new Earthquake(Integer.toString(Integer.parseInt(rs.getString(1))), thisDate,
						Float.toString(Float.parseFloat(rs.getString(3))),
						Float.toString(Float.parseFloat(rs.getString(4))),
						Integer.toString(Integer.parseInt(rs.getString(5))), Float.toString(thisMagnitude),
						thisRegion));
				if (thisDay.compareTo(minDate) < 0) {
					minDate = thisDay;
				} else if (thisDay.compareTo(maxDate) > 0) {
					maxDate = thisDay;
				}
			}
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * the entrance of this java application.
	 */
	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("Earthquake Search");

		this.primaryStage.getIcons().add(new Image("file:Resources/icons/if_Linkedin_767353.png"));

		showEarthquakeOverview();
	}

	/**
	 * Using reflection to find the position of .fxml file, and load it.
	 * 
	 */
	public void showEarthquakeOverview() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("view/EarthquakeOverview.fxml"));
			AnchorPane earthquakeOverview = (AnchorPane) loader.load();

			EarthquakeOverviewController controller = loader.getController();
			controller.setMainApp(this);

			
			Scene scene = new Scene(earthquakeOverview);
			
			primaryStage.setScene(scene);
			primaryStage.setWidth(840);
			primaryStage.setHeight(730);
			primaryStage.centerOnScreen();
			primaryStage.show();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @return primary stage.
	 */
	public Stage getPrimaryStage() {
		return primaryStage;
	}
/**
 * main method of this project. <br>
 * @param args: command-line arguments.
 */
	public static void main(String[] args) {
		launch(args);
	}

	/**
	 * @param fromDay
	 *            -- the beginning of the search range.
	 * @param toDay
	 *            -- the ending of the search range,
	 * @param magnitude
	 *            -- search from which magnitude.
	 * @param region
	 *            -- search earthquakes in which region.
	 * @return the search results
	 */
	public ArrayList<Earthquake> getQuakes(String fromDay, String toDay, float magnitude, String region) {
		ArrayList<Earthquake> filteredQuakes = new ArrayList<Earthquake>();
		if (fromDay.compareTo(toDay) > 0) {
			Alert a = new Alert(AlertType.WARNING, "Your from-day is after your to-day", ButtonType.OK);
			a.showAndWait();
		}
		earthQuakes.stream().filter((q) -> (region.equals(Earthquake.WORLDWIDE) || region.equals(q.getRegion().get())))
				.filter((q) -> (fromDay.compareTo(q.getUTC_date().get().substring(0, 10).trim()) <= 0))
				.filter((q) -> (toDay.compareTo(q.getUTC_date().get().substring(0, 10).trim()) >= 0))
				.filter((q) -> (q.getMagnitude().get() >= magnitude)).forEach((q) -> {
					filteredQuakes.add(q);
				});
		return filteredQuakes;
	}

	/**
	 * 
	 * @return all earthquakes in database.
	 */
	public ArrayList<Earthquake> getQuakes() {
		return getQuakes(minDate, maxDate, 0f, Earthquake.WORLDWIDE);
	}

}
