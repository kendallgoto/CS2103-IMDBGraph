import java.util.Collection;
import java.util.HashSet;

public class MovieNode extends IMDBNode {
    final private HashSet<ActorNode> _neighbors = new HashSet<>();
    public MovieNode(String name) {
        super(name);
    }
    public void addNeighbor(ActorNode node) {
        _neighbors.add(node);
    }
    public Collection<? extends Node> getNeighbors() {
        return _neighbors;
    }
}
