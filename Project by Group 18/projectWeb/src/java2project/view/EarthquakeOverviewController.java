package java2project.view;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.TreeMap;

import java2project.MainApp;
import java2project.model.Earthquake;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
/**
 * This is the controller of the EarthquakeOverview.fxml file.<br>
 * Some controller of some controls in the .fxml file are there. 
 */
public class EarthquakeOverviewController {
	@FXML
	private TableView<Earthquake> earthquakeTable;
	@FXML
	private TableColumn<Earthquake, String> dateAndTime;
	@FXML
	private TableColumn<Earthquake, Double> latitude;
	@FXML
	private TableColumn<Earthquake, Double> longitude;
	@FXML
	private TableColumn<Earthquake, Integer> depth;
	@FXML
	private TableColumn<Earthquake, Double> magnitude;
	@FXML
	private TableColumn<Earthquake, String> region;
	@FXML
	private DatePicker fromDate;
	@FXML
	private DatePicker toDate;
	@FXML
	private Button search;
	@FXML
	private ComboBox<String> regionset = new ComboBox<String>();
	@FXML
	private Slider mag;
	@FXML
	private Label status;
	@FXML
	private AnchorPane apMercator;
	@FXML
	private ImageView iv;
	@FXML
	private Canvas cvMercator;
	
	private MainApp mainApp;
	
	
	@FXML
	private AnchorPane chartMagnitudePane;
	@FXML
	private AnchorPane chartDayPane;
	
	private String[] magnitudeRanges = { "Under 2.0", "2.0 to 3.0", "3.0 to 4.0", "4.0 to 5.0", "5.0 and over" };
	@FXML
	private BarChart<String, Integer> Chart1;
	@FXML
	private BarChart<String, Integer> Chart2;
	

	/**
	 * initial data, put data into tableview.
	 */
	@FXML
	private void initialize() {
		mainApp = new MainApp();

		ObservableList<Earthquake> eq = FXCollections.observableArrayList(mainApp.getEarthQuakes());
		earthquakeTable.setItems(eq);
		dateAndTime.setCellValueFactory(cellData -> cellData.getValue().getUTC_date());
		latitude.setCellValueFactory(cellData -> cellData.getValue().getLatitude().asObject());
		longitude.setCellValueFactory(cellData -> cellData.getValue().getLongitude().asObject());
		depth.setCellValueFactory(cellData -> cellData.getValue().getDepth().asObject());
		magnitude.setCellValueFactory(cellData -> cellData.getValue().getMagnitude().asObject());
		region.setCellValueFactory(cellData -> cellData.getValue().getRegion());
		regionset.setItems(FXCollections.observableArrayList(new ArrayList<String>(mainApp.getRegions())));
		regionset.setValue(Earthquake.WORLDWIDE);

		fromDate.setValue(LocalDate.parse(mainApp.getMinDate()));
		toDate.setValue(LocalDate.parse(mainApp.getMaxDate()));
		
		status.setText(earthquakeTable.getItems().size() + " earthquakes in total");
	}

	/**
	 * the controller of search button.
	 */
	public void handlesearch() {
		ObservableList<Earthquake> quakes = FXCollections.observableArrayList(mainApp
				.getQuakes(fromDate.getValue().toString(),
						toDate.getValue().toString(),
						(float)mag.getValue(),
						regionset.getValue()));
		earthquakeTable.setItems(quakes);
		status.setText(Integer.toString(quakes.size()) + " earthquakes selected");
		refreshMaps();
		refreshCharts();
	}
	
	/**
	 * The set method of mainApp.
	 * @param mainApp: an object of MainApp.
	 */
	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
	}
	/**
	 * when the search button is clicked, map will be refresh.
	 */
	private void refreshMaps() {
		int diameter;
		double[] xy;
		ObservableList<Node> paneChildren;

		paneChildren = apMercator.getChildren();
		if (paneChildren.size() > 1) {
			paneChildren.remove(1, paneChildren.size());
		}
		GraphicsContext gcm = cvMercator.getGraphicsContext2D();

		gcm.clearRect(0,0,cvMercator.getWidth(),cvMercator.getHeight());
		gcm.setStroke(Color.CHARTREUSE);
		gcm.setLineWidth(2);
		for (Earthquake q : earthquakeTable.getItems()) {
			diameter = (int) (3 * q.getMagnitude().get() );
			xy = xy(q.getLatitude().get(), q.getLongitude().get());
			gcm.strokeOval(xy[0] - diameter / 2, xy[1] - diameter / 2, diameter, diameter);
		}
		apMercator.getChildren().add(cvMercator);
	}

	/**
	 * 
	 * @param lat -- the latitude of the earthquake.
	 * @param lon -- the longitude of the earthquake.
	 * @return the x,y coordinate of that earthquake on the map.
	 */
	public double[] xy(double lat, double lon) {
		double[] xyc = new double[2];
		xyc[1] = iv.getFitHeight() * (0.5 - lat / 180);
		double x = ((lon) * iv.getFitWidth() / 360);
		xyc[0] =  x >= 0? x : x + iv.getFitWidth();
		
		return xyc;
	}

	/**
	 * when the search button is clicked, the 2 charts will be refresh.
	 */
	public void refreshCharts() {
		ObservableList<XYChart.Data<String,Integer>> chart1Data =FXCollections.observableArrayList();
	    ObservableList<XYChart.Data<String,Integer>> chart2Data =FXCollections.observableArrayList();
		XYChart.Series<String, Integer> seriesChart2 = new XYChart.Series<>();  
		XYChart.Series<String, Integer> seriesChart1 = new XYChart.Series<>();
		
			Chart1.getData().clear();
			Chart2.getData().clear();
			seriesChart1.getData().clear();
			seriesChart2.getData().clear();
			chart1Data.clear();
		    chart2Data.clear();
			TreeMap<String, Integer> counters1 = new TreeMap<>();
			TreeMap<String, Integer> counters2 = new TreeMap<>();
			int xAxis;
			String date = null;
			Integer counter;
			float magnitude;
			for (int i = 0; i < magnitudeRanges.length; i++) {
				counters1.put(magnitudeRanges[i], 0);
			}
			for (Earthquake q : earthquakeTable.getItems()) {
				magnitude = (float) q.getMagnitude().get();
				if (magnitude < 2.0) {
					xAxis = 0;
				} else if (magnitude < 3.0) {
					xAxis = 1;
				} else if (magnitude < 4.0) {
					xAxis = 2;
				} else if (magnitude < 5.0) {
					xAxis = 3;
				} else {
					xAxis = 4;
				}
				date = q.getUTC_date().get().substring(0, 10);
				// System.out.println(date);
				counter = counters1.get(magnitudeRanges[xAxis]);
				counter++;
				counters1.put(magnitudeRanges[xAxis], counter);
				counter = counters2.get(date);
				if (counter == null) {
					counter = 1;
				} else {
					counter++;
				}
				counters2.put(date, counter);
			}
			for (int i = 0; i < magnitudeRanges.length; i++) {
				chart1Data.add(new XYChart.Data<>(magnitudeRanges[i], counters1.get(magnitudeRanges[i])));
				seriesChart1.setData(chart1Data);
				// System.out.println(seriesChart1);
			}
			if(date!=null)
			date = counters2.firstKey();
			while (date != null) {
				seriesChart2.getData().add(new XYChart.Data<>(date, counters2.get(date)));
				// System.out.println(seriesChart2);
				date = counters2.higherKey(date);
			}
		Chart1.getData().add(seriesChart1);
		Chart2.getData().add(seriesChart2);
	}
	
}
