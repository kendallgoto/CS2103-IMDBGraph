import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import java.util.*;
import java.io.*;

/**
 * Code to test Project 3; you should definitely add more tests!
 */
public class GraphPartialTester {
	private IMDBGraph imdbGraph;
	private GraphSearchEngine searchEngine;
	private IMDBGraph hardGraph;

	/**
	 * Verifies that there is no shortest path between a specific and actor and actress.
	 */
	@Test(timeout=5000)
	public void findShortestPath () throws IOException {
		imdbGraph = new IMDBGraphImpl("src/actors_test.list", "src/actresses_test.list");
		final Node actor1 = imdbGraph.getActor("Actor1");
		final Node actress2 = imdbGraph.getActor("Actress2");
		final List<Node> shortestPath = searchEngine.findShortestPath(actor1, actress2);
		assertNull(shortestPath);  // there is no path between these people
	}

	@Before
	/**
	 * Instantiates the graph
	 */
	public void setUp () throws IOException {

		imdbGraph = new IMDBGraphImpl("src/actors_test.list", "src/actresses_test.list");
		searchEngine = new GraphSearchEngineImpl();
	}

	@Test
	/**
	 * Just verifies that the graphs could be instantiated without crashing.
	 */
	public void finishedLoading () {
		assertTrue(true);
		// Yay! We didn't crash
	}

	@Test
	/**
	 * Verifies that a specific movie has been parsed.
	 */
	public void testSpecificMovie () {
		testFindNode(imdbGraph.getMovies(), "Movie1 (2001)");
	}

	@Test
	/**
	 * Verifies that a specific actress has been parsed.
	 */
	public void testSpecificActress () {
		testFindNode(imdbGraph.getActors(), "Actress2");
	}

	/**
	 * Verifies that the specific graph contains a node with the specified name
	 * @param graph the IMDBGraph to search for the node
	 * @param name the name of the Node
	 */
	private static Node testFindNode (Collection<? extends Node> nodes, String name) {
		boolean found = false;
		Node forReturn = null;
		for (Node node : nodes) {
			if (node.getName().trim().equals(name)) {
				found = true;
				forReturn = node;
				break;
			}
		}
		assertTrue(found);
		return forReturn;
	}

	/* Custom tests for IMDBGraph */
	private void setupHardGraph() throws IOException {
		IMDBGraph imdbGraph_short = new IMDBGraphImpl("src/actors_short.list", "src/actresses_short.list");
		String firstActorN = "Aarre-Ahtio, Tapio";
		Node firstActor = imdbGraph_short.getActor(firstActorN);
		assertNotNull(firstActor);
		assertEquals(firstActor, testFindNode(imdbGraph_short.getActors(), firstActorN));
		hardGraph = imdbGraph_short;
	}
	private void testHardGraph() {
		String lastActor_N = "Abad, Mauricio";
		String TVOnlyActor_N = "Abad, Nacho (I)";
		Node lastActor = hardGraph.getActor(lastActor_N);
		Node TVOnlyActor = hardGraph.getActor(TVOnlyActor_N);
		assertNotNull(lastActor);
		assertNull(TVOnlyActor);
		assertEquals(lastActor, testFindNode(hardGraph.getActors(), lastActor_N));
	}
	private void testMovieChain() {
//		Abad, Ariel		Island of Desire (1990)  <25>
//						Tora tora, bang bang bang (1990)
		String targetActor = "Abad, Ariel";
		String firstMovie = "Island of Desire (1990)";
		String secondMovie = "Tora tora, bang bang bang (1990)";
		Node actor = hardGraph.getActor(targetActor);
		assertNotNull(actor);
		assertEquals(actor, testFindNode(hardGraph.getActors(), targetActor));

		//ensure movies
		Node movie = hardGraph.getMovie(firstMovie);
		assertNotNull(movie);
		assertEquals(movie, testFindNode(hardGraph.getMovies(), firstMovie));
		Node movie2 = hardGraph.getMovie(secondMovie);
		assertNotNull(movie2);
		assertEquals(movie2, testFindNode(hardGraph.getMovies(), secondMovie));

		//check connection
		assertEquals(actor.getNeighbors().size(), 2);
		Iterator iterator = actor.getNeighbors().iterator();
		Node firstResult = (Node) iterator.next();
		Node secondResult = (Node) iterator.next();
		//order is not necessarily maintained:
		assertTrue( (firstResult.equals(movie) && secondResult.equals(movie2)) ||
				(firstResult.equals(movie2) && secondResult.equals(movie)));
		assertFalse(iterator.hasNext());

		Node actor3 = hardGraph.getActor("$hutter");

	}
	private void testVerifyCount() {
		int totalEntries = hardGraph.getActors().size();
		assertTrue( Math.abs(totalEntries - 2200) < 100);
	}

	private void setupFullGraph() throws IOException {
		IMDBGraph imdbGraph_short = new IMDBGraphImpl("src/actors_live.list", "src/actresses_live.list");
		String firstActorN = "Aarre-Ahtio, Tapio";
		Node firstActor = imdbGraph_short.getActor(firstActorN);
		assertNotNull(firstActor);
		assertEquals(firstActor, testFindNode(imdbGraph_short.getActors(), firstActorN));
		hardGraph = imdbGraph_short;
	}
	@Test
	public void testPartial() throws IOException {
		setupHardGraph();
		testHardGraph();
		testMovieChain();
		testVerifyCount();
	}
	public void testBasicChain () {
		//Brad Pitt -> Fight Club -> Edward Norton
		final Node actor1 = hardGraph.getActor("Pitt, Brad");
		final Node actor2 = hardGraph.getActor("Norton, Edward (I)");
		final List<Node> shortestPath = searchEngine.findShortestPath(actor1, actor2);
		assertEquals(shortestPath.get(0).getName(), "Pitt, Brad");
		assertEquals(shortestPath.get(2).getName(), "Norton, Edward (I)");
		assertEquals(shortestPath.size(), 3);
	}
	public void testBasicCrossChain () {
		//Brad Pitt -> Fight Club -> Eugenie Bondurant
		final Node actor1 = hardGraph.getActor("Pitt, Brad");
		final Node actress2 = hardGraph.getActor("Bondurant, Eugenie");
		final List<Node> shortestPath = searchEngine.findShortestPath(actor1, actress2);
		assertEquals(shortestPath.get(0).getName(), "Pitt, Brad");
		assertEquals(shortestPath.get(1).getName(), "Fight Club (1999)");
		assertEquals(shortestPath.get(2).getName(), "Bondurant, Eugenie");
		assertEquals(shortestPath.size(), 3);
	}
	public void testComplexChain() {
		final Node actor1 = hardGraph.getActor("Bieber, Justin");
		final Node actor2 = hardGraph.getActor("Bacon, Kevin (I)");
		final List<Node> shortestPath = searchEngine.findShortestPath(actor1, actor2);
		assertEquals(shortestPath.size(), 5);

		final Node actor3 = hardGraph.getActor("Lee, Bruce (I)");
		final List<Node> shortestPath2 = searchEngine.findShortestPath(actor2, actor3);
		assertEquals(shortestPath2.size(), 5);

		final Node actor4 = hardGraph.getActor("Leke, Acha");
		final Node actor5 = hardGraph.getActor("Monkhouse, John");
		final List<Node> shortestPath3 = searchEngine.findShortestPath(actor4, actor5);
		assertEquals(shortestPath3.size(), 11);
	}

	@Test
	public void testFull() throws IOException {
		setupFullGraph();
		//these tests should still pass successfully
		testHardGraph();
		testMovieChain();
		testBasicChain();
		testBasicCrossChain();
		testComplexChain();
	}

}
