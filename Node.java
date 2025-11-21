import java.util.ArrayList;
import java.util.List;

public class Node {
    int vertex;
    int xPos;
    int yPos;
    String color;
    List<Integer> neighbors;

    public Node(int vertex) {
        this(vertex, 0, 0, "N", null);
    }

    public Node(int vertex, String color) {
        this(vertex, 0, 0, color, null);
    }

    public Node(int vertex, String color, List<Integer> neighbors) {
        this(vertex, 0, 0, color, neighbors);
    }

    public Node(int vertex, int xPos, int yPos, String color, List<Integer> neighbors){
        this.vertex = vertex;
        this.xPos = xPos;
        this.yPos = yPos;
        this.color = color;
        if ( neighbors != null ) {
            this.neighbors = new ArrayList<>( neighbors );
        } else {
            this.neighbors = new ArrayList<>( );
        }
    }
}
