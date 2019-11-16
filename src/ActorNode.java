import java.util.Collection;
import java.util.HashSet;

public class ActorNode extends IMDBNode {
    final private HashSet<MovieNode> _neighbors = new HashSet<>();
    public ActorNode(String name) {
        super(name);
    }
    public void addNeighbor(MovieNode node) {
        _neighbors.add(node);
    }
    public Collection<? extends Node> getNeighbors() { return _neighbors; }
}
