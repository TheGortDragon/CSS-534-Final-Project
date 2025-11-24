import java.util.List;

public class Face {
    int id;                      // Unique face ID
    List<Node> faceNodes;     // List of (x, y) points forming the polygon
    String color;                // Assigned color (initially "N")
    List<Integer> neighbors;     // IDs of adjacent faces
    List<Edge> borderEdges; // Edges forming the border of the face
    List<Face> adjacentFaces; // Adjacent faces
}
