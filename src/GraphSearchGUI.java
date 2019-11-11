import java.util.stream.*;
import java.util.function.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.util.*;

/**
 * A simple GUI to illustrate breadth-first search on movie/actor nodes in Project 3.
 */
public class GraphSearchGUI extends Application {
	private final GraphSearchEngine _searchEngine = new GraphSearchEngineImpl();

	/**
	 * Constructor with no parameters.
	 */
	public GraphSearchGUI () {
	}

	/**
	 * Comparator for comparing String objects which may be null. Used
	 * for sorting a collection of actor/actress/movie names.
	 */
	private static class StringComparator implements Comparator<String> {
		public int compare (String obj1, String obj2) {
			if (obj1 == obj2) {
				return 0;
			} else if (obj1 == null) {
				return -1;
			} else if (obj2 == null) {
				return 1;
			} else {
				return obj1.compareTo(obj2);
			}
		}
	}

	/**
	 * Converts from a Collection to a List of Node objects.
	 */
	private static List<String> getNames (Collection<? extends Node> nodes) {
		return nodes.stream().map(node -> node.getName()).collect(Collectors.toList());
	}

	/**
	 * Converts from a Collection to an ObservableList of Node objects.
	 * @param nodes the collection of Node objects to convert
	 * @return the ObservableList of String objects converted from the collection.
	 */
	private static ObservableList<String> getSortedObservableList (Collection<? extends Node> nodes) {
		return FXCollections.observableList(getNames(nodes)).sorted(new StringComparator());
	}

	/**
	 * Performs BFS on the pair of actors currently selected in the two ListViews. 
	 * Puts results into the ListView of results and also summarizes the success/failure
	 * of the search in the Label.
	 * @param graph the IMDBGraph of nodes on which to perform BFS
	 * @param actorsList1 the ListView containing the starting node
	 * @param actorsList2 the ListView containing the target node
	 * @param resultsList the ListView in which to show the shortest path
	 * @param resultsLabel the Label in which to summarize the success/failure of the search
	 */
	private void searchAndUpdateGUI (IMDBGraph graph, ListView<String> actorsList1, ListView<String> actorsList2,
	                                 ListView<String> resultsList, Label resultsLabel) {
		if (! actorsList1.getSelectionModel().isEmpty() && ! actorsList2.getSelectionModel().isEmpty()) {
			String actor1 = actorsList1.getSelectionModel().getSelectedItem();
			String actor2 = actorsList2.getSelectionModel().getSelectedItem();
			List<Node> shortestPath = _searchEngine.findShortestPath(graph.getActor(actor1),
										 graph.getActor(actor2));
			if (shortestPath == null) {
				List<String> nopath = new ArrayList<String>();
				nopath.add("No path");
				resultsList.getItems().setAll(FXCollections.observableList(nopath));
				resultsLabel.setText("");
			} else {
				resultsList.getItems().setAll(getNames(shortestPath));
				resultsLabel.setText((shortestPath.size()-1) + " hops");
			}
		}
	}

	@Override
	public void start (Stage primaryStage) {
		primaryStage.setTitle("IMDB Graph Search");

		// Load graph data and initialize the ListViews
		final IMDBGraph graph;
		try {
			graph = new IMDBGraphImpl("/Users/jake/Courses/CS210X/Graph/IMDB/actors.list", "/Users/jake/Courses/CS210X/Graph/IMDB/actresses.list");
		} catch (IOException ioe) {
			System.out.println("Couldn't load data");
			return;
		}

		final ListView<String> actorsList1 = new ListView<String>(getSortedObservableList(graph.getActors()));
		final ListView<String> actorsList2 = new ListView<String>(getSortedObservableList(graph.getActors()));
		final ListView<String> resultsList = new ListView<String>();

		// Create the panels of the GIU
		final VBox rootPanel = new VBox();
			final HBox comboPanel = new HBox();
			comboPanel.setPadding(new Insets(15, 12, 15, 12));
			comboPanel.setSpacing(10);
				VBox actorsPanel1 = new VBox();
				actorsPanel1.getChildren().addAll(new Label("Actor 1"), actorsList1);
				VBox actorsPanel2 = new VBox();
				actorsPanel2.getChildren().addAll(new Label("Actor 2"), actorsList2);
			comboPanel.getChildren().addAll(actorsPanel1, actorsPanel2);

			final VBox buttonPanel = new VBox();
			buttonPanel.setPadding(new Insets(15, 12, 15, 12));
			buttonPanel.setSpacing(10);
			final Label resultsLabel = new Label();
			final Button searchButton = new Button("Search") {
				public void fire () {
					searchAndUpdateGUI(graph, actorsList1, actorsList2, resultsList, resultsLabel);
				}
			};
			buttonPanel.getChildren().addAll(searchButton, resultsList, resultsLabel);
		rootPanel.getChildren().addAll(comboPanel, buttonPanel);

		// Set the JavaFX scene		
		primaryStage.setScene(new Scene(rootPanel, 400, 300));
		primaryStage.show();
	}
}
