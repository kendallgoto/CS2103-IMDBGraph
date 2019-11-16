import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Scanner;

public class IMDBGraphImpl implements IMDBGraph {
    private static final String FIRST_LINE = "----\t\t\t------"; //Header line of "names   titles"
    private static final String LAST_LINE = "-----------------------------------------------------------------------------"; //ensure appropriate ending
    private final HashMap<String, MovieNode> _movieCollection = new HashMap<>();
    private final HashMap<String, ActorNode> _actorCollection = new HashMap<>();

    /**
     * Instantiates a new IMDBGraph given two IMDB Text database file
     * @param file1 actors file .txt
     * @param file2 actresses file .txt
     * @throws IOException error if file not found
     */
    public IMDBGraphImpl(String file1, String file2) throws IOException {
        processFile(new File(file1));
        processFile(new File(file2));
    }

    /**
     * Given a file object, process the file for actor names and their associated movies.
     * @param target a File object containing an IMDB text database file
     * @throws IOException error if file not found
     */
    private void processFile(File target) throws IOException {
        final Scanner scanner = new Scanner(target, "ISO-8859-1");
        boolean reachedBeginning = false;
        while(scanner.hasNextLine()) {
            String thisLine = scanner.nextLine();
            //Process to beginning of list:
            if(!reachedBeginning) {
                if (thisLine.equals(FIRST_LINE))
                    reachedBeginning = true;
                continue;
            }
            if(thisLine.equals(LAST_LINE))
                break;
            if(thisLine.indexOf('\t') != 0 && thisLine.indexOf('\t') != -1) {
                //If the line doesn't begin with tab, then it's an actor's name
                String actorName = thisLine.substring(0, thisLine.indexOf('\t'));
                ActorNode thisActor = (ActorNode)getActor(actorName); //semantical... we should never run into a duplicate actor in the listing.
                if(thisActor == null)
                    thisActor = new ActorNode(actorName);
                int size = processMovies(scanner, thisActor, thisLine);
                if(size > 0) //ensure the actor actually had movies
                    _actorCollection.put(actorName, thisActor);
            }

        }
        scanner.close();
    }

    /**
     * Processes movies for each actor associated
     * @param s - A new scanner initated for the IMDB database file
     * @param actor a specific found actor to associate new movies with
     * @param line Our current processed line from scanner
     * @return an integer describing the number of movies the actor was added to
     */
    private int processMovies(Scanner s, ActorNode actor, String line) {
        boolean firstLine = true;
        int results = 0;
        while(firstLine || s.hasNextLine()) {
            if(!firstLine)
                line = s.nextLine();

            if(line.equals("")) break; //Stop processing at the end of this actor's movie list (gap of just \n)
            int LPad = line.lastIndexOf("\t"); //Initial tab
            int RPad = line.indexOf(")", LPad); //End of year parenthetical
            firstLine = false;

            //remove undesired
            if(line.contains("(TV)")) //made for tv movies
                continue;
            if(line.matches(".*\"(.*?)\".*")) //if we contain a quoted string (tv shows)
                continue;
            if(LPad == -1 || RPad == -1 || LPad > RPad) {
                //TESTED: this should never fire even for the full IMDB dataset.
                return 0; //We should have caught this error sooner. For the moment, we'll just pop out and continue.
            }
            String finalName = line.substring(LPad+1, RPad+1);
            MovieNode thisMovie = (MovieNode)getMovie(finalName); //ensure we don't duplicate a movie if already defined
            if(thisMovie == null)
                thisMovie = new MovieNode(finalName);
            //ensure double link:
            thisMovie.addNeighbor(actor);
            actor.addNeighbor(thisMovie);
            //and global link
            _movieCollection.put(finalName, thisMovie); //use key=name to allow getMovie() to run in O(1)
            results++;
        }
        return results;
    }

    /**
     * Fetches the total database of processed actors
     * @return a collection of actors
     */
    public Collection<? extends Node> getActors() {
        return _actorCollection.values();
    }
    /**
     * Fetches the total database of processed movies
     * @return a collection of movies
     */

    public Collection<? extends Node> getMovies() {
        return _movieCollection.values();
    }

    /**
     * Fetches a single movie by name
     * @param name the name of the requested movie
     * @return a Node object of the movie
     */
    public Node getMovie(String name) {
        return _movieCollection.get(name);
    }

    /**
     * Fetches a single actor by name
     * @param name the name of the requested actor
     * @return a Node object of the actor
     */
    public Node getActor(String name) {
        return _actorCollection.get(name);
    }
}
