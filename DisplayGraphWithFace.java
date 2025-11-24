import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Polygon;
import javax.swing.JFrame;
import javax.swing.JPanel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.io.*;

/**
 *
 * @author Andrew Howell (modified to display faces)
 */
class DisplayGraphWithFace {
    private JFrame gWin;                     // a graphics window

    public DisplayGraphWithFace(List<Node> nodes, List<Edge> edges, List<Face> faces) {
	    // initialize window and graphics:
        gWin = new JFrame( "Node Graph with Faces" );
        gWin.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gWin.setLocation( 50, 50 );  // screen coordinates of top left corner
	    gWin.setResizable( false );
        GraphPanel gPanel = new GraphPanel(nodes, edges, faces);
        gWin.add(gPanel);
        gWin.setVisible( true );     // show it!
        gWin.setSize( 600, 600);
    }

    private class GraphPanel extends JPanel {
        private int textOffsetX = 17;
        private int textOffsetY = 37;

        private int lineOffsetX = 25;
        private int lineOffsetY = 25;

        HashMap<String, Color> colors = new HashMap<>();
        List<Node> nodes;
        List<Edge> edges;
        List<Face> faces;

        public GraphPanel(List<Node> nodes, List<Edge> edges, List<Face> faces) {
            this.nodes = nodes;
            this.edges = edges;
            this.faces = faces;
        }

        @Override
        protected void paintComponent(Graphics g) {
           super.paintComponent(g);

           colors.put("N", new Color(127,127,127));
           colors.put("R", new Color(255,0,0));
           colors.put("G", new Color(0,255,0));
           colors.put("B", new Color(0,0,255));
           colors.put("Y", new Color(255,255,0));

           Graphics2D g2D = (Graphics2D)g;
           g2D.setFont(g2D.getFont().deriveFont(30f));
           g2D.setStroke(new BasicStroke(5));

           // Draw filled faces first (as background)
           for (Face face : faces) {
               if (face.faceNodes.size() >= 3) {  // Need at least 3 points for a polygon
                   Polygon polygon = new Polygon();
                   for (Node node : face.faceNodes) {
                       polygon.addPoint(node.xPos + lineOffsetX, node.yPos + lineOffsetY);
                   }
                   Color faceColor = colors.get(face.color);
                   if (faceColor != null) {
                       g2D.setColor(faceColor);
                       g2D.fillPolygon(polygon);
                   }
               }
           }

           // Draw edges (as foreground)
           g2D.setColor(new Color(0,0,0));
           for (int i = 0; i < edges.size(); i++) {
                int v1 = edges.get(i).n1.vertex;
                int v2 = edges.get(i).n2.vertex;
                
                int xPos1 = nodes.get(v1).xPos;
                int yPos1 = nodes.get(v1).yPos;
                int xPos2 = nodes.get(v2).xPos;
                int yPos2 = nodes.get(v2).yPos;

                g2D.drawLine(xPos1 + lineOffsetX, yPos1 + lineOffsetY, 
                    xPos2 + lineOffsetX, yPos2 + lineOffsetY);
           }

           for (Node node : nodes) {
                Color color = colors.get(node.color);
                g2D.setColor(color);
                g2D.fillOval(node.xPos, node.yPos, 50, 50); 

                g2D.setColor(new Color(255,255,255));
                g2D.drawString("" + node.vertex, node.xPos + textOffsetX, node.yPos + textOffsetY);
           }
        }
    }

    public static void main(String args[]) {
        List<Node> nodes = new ArrayList<>();
        List<Edge> edges = new ArrayList<>();
        List<Face> faces = new ArrayList<>();
        Scanner keyboard = new Scanner( System.in ); // reading from the standard input

        // First, read node information
        while ( keyboard.hasNext( ) ) {
            String line = keyboard.nextLine().trim();
            if (line.isEmpty()) continue;
            
            if (line.startsWith("FACES")) {
                // Switch to reading face data
                break;
            }
            
            String[] parts = line.split("\\s+");
            if (parts.length < 4) continue;
            
            int vertex = Integer.parseInt(parts[0]);
            int xPos = Integer.parseInt(parts[1]);
            int yPos = Integer.parseInt(parts[2]);
            String color = parts[3];

            System.out.print("vertex=" + vertex + " xPos=" + xPos + " yPos=" + yPos 
                + " color=" + color + " Neighbors=");

            // Read neighbors (remaining parts)
            for (int i = 4; i < parts.length; i++) {
                int neighbor = Integer.parseInt(parts[i]);
                System.out.print(" " + neighbor);

                Edge edge = new Edge(new Node(vertex), new Node(neighbor));
                if (!edges.contains(edge)) {
                    edges.add(edge);
                }
            }
            Node node = new Node(vertex, xPos, yPos, color, null);
            nodes.add(node);
            System.out.println();
        }
        
        // Now read face information
        while ( keyboard.hasNext( ) ) {
            String line = keyboard.nextLine().trim();
            if (line.isEmpty()) continue;
            
            String[] parts = line.split("\\s+");
            if (parts.length < 3) continue;
            
            int faceId = Integer.parseInt(parts[0]);
            String faceColor = parts[1];
            
            // Remaining parts are vertex IDs that form the face
            List<Node> faceNodes = new ArrayList<>();
            for (int i = 2; i < parts.length; i++) {
                int vertexId = Integer.parseInt(parts[i]);
                if (vertexId < nodes.size()) {
                    faceNodes.add(nodes.get(vertexId));
                }
            }
            
            Face face = new Face();
            face.id = faceId;
            face.color = faceColor;
            face.faceNodes = faceNodes;
            faces.add(face);
            
            System.out.println("Face " + faceId + " with color " + faceColor + 
                " has " + faceNodes.size() + " nodes");
        }
        
        DisplayGraphWithFace box = new DisplayGraphWithFace(nodes, edges, faces);  // create a graphics
        System.out.println( "Done..." );
        keyboard.close();
    }
}
