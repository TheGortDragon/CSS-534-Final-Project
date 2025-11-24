import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.crypto.dsig.keyinfo.KeyValue;

public class FourColors {
    private static List<String> visitedHalfEdges = new ArrayList<>();
    private static int faceId = 0;
    private static final String[] colors = { "R", "G", "B", "Y" };
    private static List<Face> faces = new ArrayList<>();

    public static void main(String args[]) {
        if (args.length < 1) {
            System.err.println("Usage: java FourColorsMapReduce <filename>");
            System.exit(1);
        }
        String filename = args[0];
        Map<Integer, Node> nodes = new HashMap<>();
        try {
            List<String> lines = Files.readAllLines(Paths.get(filename));
            for (String line : lines) {
                // 1863 -99.625324 40.001866 N 642 1761
                String[] parts = line.split("\\s+");
                int vertex = Integer.parseInt(parts[0]);
                int xPos = Integer.parseInt(parts[1]);
                int yPos = Integer.parseInt(parts[2]);
                String color = parts[3];
                List<Integer> neighbors = new ArrayList<>();
                for (int i = 4; i < parts.length; i++) {
                    neighbors.add(Integer.parseInt(parts[i]));
                }
                Node node = new Node(vertex, xPos, yPos, color, neighbors);
                nodes.put(vertex, node);
            }
        } catch (IOException e) {
            System.err.println("IOException: " + e.getMessage());
        }

        for (Map.Entry<Integer, Node> entry : nodes.entrySet()) {
            int id = entry.getKey();
            Node node = entry.getValue();
            Node startingNeighborNode = nodes.get(node.neighbors.get(0));
            if (!visitedHalfEdges.contains(node.vertex + "-" + startingNeighborNode.vertex)) {
                Face face = FindFace(node, startingNeighborNode, nodes);
                faces.add(face);
            }

        }
        System.out.println("Total faces found: " + faces.size());
        System.out.println();

        // Now color the faces, with no two adjacent faces having the same color
        for (int i = 0; i < faces.size(); i++) {
            Face face = faces.get(i);
            //UniqueNeighbors ensures duplicate adjacent faces are not added
            Set<Integer> uniqueNeighbors = new HashSet<>();
            // Find adjacent faces
            for (int j = 0; j < faces.size(); j++) {
                if (i == j)
                    continue;
                Face otherFace = faces.get(j);
                // Check if they share any border edges
                for (Edge edge1 : face.borderEdges) {
                    for (Edge edge2 : otherFace.borderEdges) {
                        if (edge1.equals(edge2) || edge1.equals(edge2.reverse())) {
                            if (!uniqueNeighbors.contains(otherFace.id)) {
                                uniqueNeighbors.add(otherFace.id);
                                face.neighbors.add(otherFace.id);
                                face.adjacentFaces.add(otherFace);
                            }
                            break; // Found a shared edge, no need to check more edges
                        }
                    }
                }
            }
        }

        // Sort faces by number of neighbors (descending) for better coloring
        // Faces with more neighbors should be colored first
        faces.sort((f1, f2) -> Integer.compare(f2.adjacentFaces.size(), f1.adjacentFaces.size()));
        
        for (Face face : faces) {
            if (face.color.equals("N")) {
                assignColor(face, faces);
            }
        }
        
        // Re-sort by ID for consistent output
        faces.sort((f1, f2) -> Integer.compare(f1.id, f2.id));

        // Display the faces
        for (Face face : faces) {
            System.out.print("Face " + face.id + " (" + face.faceNodes.size() + " nodes, color " + face.color + "): ");
            for (Node node : face.faceNodes) {
                System.out.print(node.vertex + " ");
            }
            System.out.print(" | Edges: ");
            for (Edge edge : face.borderEdges) {
                System.out.print("(" + edge.n1.vertex + "-" + edge.n2.vertex + ") ");
            }
            for(Face neighborFace : face.adjacentFaces) {
                System.out.print(" | Neighbor Face " + neighborFace.id + " (color " + neighborFace.color + ") ");
            }
            System.out.println();
        }
        
        // Assign face colors back to nodes
        // Each node gets the color of one of its faces (skip the outer face 0)
        for (Face face : faces) {
            if (face.id == 0) continue; // Skip outer face
            for (Node node : face.faceNodes) {
                // Only assign if node doesn't have a color yet
                if (node.color.equals("N")) {
                    node.color = face.color;
                }
            }
        }
        
        // Write output to file
        try (java.io.BufferedWriter writer = new java.io.BufferedWriter(
                new java.io.FileWriter("output.txt"))) {
            // Write node data
            for (Node node : nodes.values()) {
                writer.write(node.vertex + " " + 
                           (int)node.xPos + " " + 
                           (int)node.yPos + " " + 
                           node.color);
                for (int neighbor : node.neighbors) {
                    writer.write(" " + neighbor);
                }
                writer.newLine();
            }
            
            // Write separator
            writer.write("FACES\n");
            
            // Write face data
            for (Face face : faces) {
                writer.write(face.id + " " + face.color);
                for (Node node : face.faceNodes) {
                    writer.write(" " + node.vertex);
                }
                writer.newLine();
            }
            
            System.out.println("\nOutput written to output.txt");
        } catch (IOException e) {
            System.err.println("Error writing output: " + e.getMessage());
        }
    }

    static void assignColor(Face face, List<Face> allFaces) {
        // Count how many times each color is used
        Map<String, Integer> colorCount = new HashMap<>();
        for (String color : colors) {
            colorCount.put(color, 0);
        }
        for (Face f : allFaces) {
            if (!f.color.equals("N")) {
                colorCount.put(f.color, colorCount.getOrDefault(f.color, 0) + 1);
            }
        }
        
        // Try colors in order of least used first
        String[] sortedColors = new String[colors.length];
        System.arraycopy(colors, 0, sortedColors, 0, colors.length);
        java.util.Arrays.sort(sortedColors, (c1, c2) -> 
            Integer.compare(colorCount.get(c1), colorCount.get(c2)));
        
        // Try each color, preferring less-used colors
        for (String color : sortedColors) {
            if (isValidColor(face, color)) {
                face.color = color;
                return;
            }
        }
        // If no color works (shouldn't happen with 4 colors for planar graphs)
        System.err.println("Warning: Could not color face " + face.id);
    }

    static boolean isValidColor(Face face, String color) {
        // Check if any neighbor already has this color
        for (Face neighbor : face.adjacentFaces) {
            if (neighbor != null && neighbor.color.equals(color)) {
                return false; // This color conflicts with a neighbor
            }
        }
        return true; // Color is valid
    }

    static Face FindFace(Node startNode, Node startingNeighborNode, Map<Integer, Node> graph) {
        List<Node> faceNodes = new ArrayList<>();
        faceNodes.add(startNode);
        List<Edge> borderEdges = new ArrayList<>();
        
        // Add the initial edge
        borderEdges.add(new Edge(startNode, startingNeighborNode));
        
        Node currentNode = startingNeighborNode;
        Node previousNode = startNode;
        visitedHalfEdges.add(startNode.vertex + "-" + startingNeighborNode.vertex);

        int maxIterations = 100;
        int iterations = 0;

        // Traverse the face boundary until we return to the start
        while (currentNode.vertex != startNode.vertex && iterations < maxIterations) {
            iterations++;
            faceNodes.add(currentNode);

            // Find the next edge by taking the rightmost turn (most clockwise)
            // This follows the right-hand rule for face traversal
            Node nextNode = getNextNodeInFace(currentNode, previousNode, graph);

            if (nextNode == null) {
                System.err.println("Error: No next node found from vertex " + currentNode.vertex);
                break;
            }

            visitedHalfEdges.add(currentNode.vertex + "-" + nextNode.vertex);
            
            // Only add the edge if it's not the closing edge back to start
            // (the closing edge will be added after the loop)
            if (nextNode.vertex != startNode.vertex) {
                borderEdges.add(new Edge(currentNode, nextNode));
            } else {
                // This is the closing edge
                borderEdges.add(new Edge(currentNode, startNode));
            }
            
            previousNode = currentNode;
            currentNode = nextNode;
        }

        if (iterations >= maxIterations) {
            System.err.println("Warning: Max iterations reached starting from " + startNode.vertex);
        }

        Face face = new Face();
        face.id = faceId++;
        face.faceNodes = faceNodes;
        face.color = "N";
        face.neighbors = new ArrayList<>();
        face.adjacentFaces = new ArrayList<>();
        face.borderEdges = borderEdges;
        return face;
    }

    // Find the next node in face traversal using the right-hand rule
    // We pick the neighbor that makes the smallest left turn (or largest right
    // turn)
    static Node getNextNodeInFace(Node current, Node previous, Map<Integer, Node> graph) {
        // Calculate the angle of the incoming edge (from previous to current)
        double incomingAngle = Math.atan2(current.yPos - previous.yPos,
                current.xPos - previous.xPos);

        Node bestNext = null;
        double bestAngle = Double.NEGATIVE_INFINITY;

        // Look at all neighbors and find the one that makes the smallest left turn
        for (int neighborId : current.neighbors) {
            if (neighborId == previous.vertex) {
                continue; // Don't go back where we came from
            }

            Node neighbor = graph.get(neighborId);
            // Calculate angle to this neighbor
            double outgoingAngle = Math.atan2(neighbor.yPos - current.yPos,
                    neighbor.xPos - current.xPos);

            // Calculate the turn angle (normalize to -π to π)
            double turnAngle = outgoingAngle - incomingAngle;
            // Normalize to (-π, π]
            while (turnAngle <= -Math.PI)
                turnAngle += 2 * Math.PI;
            while (turnAngle > Math.PI)
                turnAngle -= 2 * Math.PI;

            // We want the smallest left turn (or largest right turn)
            // This means the largest turnAngle in the range (-π, π]
            if (turnAngle > bestAngle) {
                bestAngle = turnAngle;
                bestNext = neighbor;
            }
        }

        return bestNext;
    }
}
