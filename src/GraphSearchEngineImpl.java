import java.util.*;

public class GraphSearchEngineImpl implements GraphSearchEngine {

    /**
     * Determines the shortest path between two nodes
     * @param s the start node.
     * @param t the target node.
     * @return A list of the nodes between S and T, starting with S.
     */
    public List<Node> findShortestPath(Node s, Node t) {
        HashMap<Node, Integer> seenNodes = new HashMap<>();
        Queue<Node> toVisit = new LinkedList<>();
        //starting at Node s, breadth first to find node T:
        toVisit.add(s);
        seenNodes.put(s, 0);
        while(toVisit.size() > 0) {
            Node start = toVisit.remove();
            int dist = seenNodes.get(start);
            if(start.equals(t)) {
                //We're done!
                break;
            }
            for(Node neighbor : start.getNeighbors()) {
                if(!seenNodes.containsKey(neighbor)) {
                    seenNodes.put(neighbor, dist+1);
                    toVisit.add(neighbor);
                }
            }
        }
        LinkedList<Node> finalList = new LinkedList<>();
        if(!seenNodes.containsKey(t))
            return null;
        Node currentNode = t;
        int currentDistance = seenNodes.get(t);
        while(currentDistance != 0) {
            //check each of currentNode's children's distance, find distance - 1;
            finalList.push(currentNode);
            for(Node child : currentNode.getNeighbors()) {
                if(seenNodes.containsKey(child) && seenNodes.get(child) == currentDistance - 1) {
                    //go down this path!
                    currentDistance--;
                    currentNode = child;
                    break;
                }
            }
        }
        //finally, push our start
        finalList.push(s);
        return finalList;
    }
}
