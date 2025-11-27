import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.io.*;

/**
 *
 * @author Andrew Howell
 */
public class GeneratePlanarGraph {

    public static void addEdge(List<Edge> edges, int vertex, int neighbor) {
        Edge edge = new Edge(new Node(vertex), new Node(neighbor));
        if (!edges.contains(edge)) {
            edges.add(edge);
        }
    }
    
    public static void main(String[] args) throws Exception {
        if ( args.length < 2 ) {
            System.out.println("Usage: java GeneratePlanarGraph vertexCount output");
            System.exit(-1);
	    }
        int size = Integer.parseInt(args[0]);
        int gridSize = (int)Math.sqrt(size);
        String filename = args[1];
        PrintWriter writer = new PrintWriter(filename);

        List<Node> nodes = new ArrayList<>();
        List<Edge> edges = new ArrayList<>();

        // generate a triangle grid
        for (int i = 0; i < size; i++) {
            int x = 2 + (i % gridSize) * (600 / gridSize);
            int y = 2 + (i / gridSize) * (600 / gridSize);
            if (i % gridSize > 0) addEdge(edges, i, i - 1); // left neighbor
            if (i / gridSize > 0) addEdge(edges, i, i - gridSize); // top neighbor
            if (i % gridSize > 0 && i / gridSize < gridSize - 1) addEdge(edges, i, i + gridSize - 1); // diagonal neighbor
            nodes.add(new Node(i, x, y, "N", null));
        } 
        
        for (Edge edge : edges) {
            Node node1 = nodes.get(edge.n1.vertex);
            Node node2 = nodes.get(edge.n2.vertex);
            // randomly remove edges (for more unique graphs)
            if (Math.random() > .3) { 
                node1.neighbors.add(edge.n2.vertex);
                node2.neighbors.add(edge.n1.vertex);
            }
        }
        for (Node node : nodes) {
            writer.print(node.vertex + " " + node.xPos + " " + node.yPos + " N");
            Collections.sort(node.neighbors);
            for (Integer neighbor : node.neighbors) {
                writer.print(" " + neighbor);
            }
            writer.println();
        }

        writer.close();
    }
}
