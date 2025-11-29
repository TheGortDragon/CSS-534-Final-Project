import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import javax.swing.JFrame;
import javax.swing.JPanel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.io.*;

/**
 *
 * @author Andrew Howell
 */
class DisplayGraph {
    private JFrame gWin;                     // a graphics window

    public DisplayGraph(List<Node> nodes, List<Edge> edges, int size) {
	    // initialize window and graphics:
        gWin = new JFrame( "Node Graph" );
        gWin.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gWin.setLocation( 50, 50 );  // screen coordinates of top left corner
	    gWin.setResizable( false );
        GraphPanel gPanel = new GraphPanel(nodes, edges, size);
        gWin.add(gPanel);
        gWin.setVisible( true );     // show it!
        gWin.setSize( 600, 600);
    }

    private class GraphPanel extends JPanel {
        // private int textOffsetX = 17;
        // private int textOffsetY = 37;

        // private int lineOffsetX = 25;
        // private int lineOffsetY = 25;

        private int textOffsetX = 8;
        private int textOffsetY = 12;

        private int lineOffsetX = 12;
        private int lineOffsetY = 10;

        HashMap<String, Color> colors = new HashMap<>();
        List<Node> nodes;
        List<Edge> edges;
        int size;

        public GraphPanel(List<Node> nodes, List<Edge> edges, int size) {
            this.nodes = nodes;
            this.edges = edges;
            this.size = size;
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
           g2D.setFont(g2D.getFont().deriveFont(10f));
           g2D.setStroke(new BasicStroke(2));

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
            int circleSize = (int)(500 / Math.sqrt(size));
            circleSize = circleSize < 25 ? circleSize : 25;
           for (Node node : nodes) {
                Color color = colors.get(node.color);
                g2D.setColor(color);
                g2D.fillOval(node.xPos, node.yPos, circleSize, circleSize); 

                if (size < 500) {
                    g2D.setColor(new Color(255,255,255));
                    g2D.drawString("" + node.vertex, node.xPos + textOffsetX, node.yPos + textOffsetY);
                }
           }
        }
    }

    public static void main(String args[]) {
        List<Node> nodes = new ArrayList<>();
        List<Edge> edges = new ArrayList<>();
        Scanner keyboard = new Scanner( System.in ); // reading from the standard input

        int size = Integer.parseInt(keyboard.nextLine());
        int count = 0;
        System.out.println("Size=" + size);

        while ( keyboard.hasNext( ) && count < size ) {
            int vertex = keyboard.nextInt();
            int xPos = keyboard.nextInt();
            int yPos = keyboard.nextInt();

            String color = keyboard.next();
            String neighborData = keyboard.nextLine().trim();
            String[] neighbors = neighborData.split(" ");

            System.out.print("vertex=" + vertex + " xPos=" + xPos + " yPos=" + yPos 
                + " color=" + color + " Neighbors=");

            for (int i = 0; i < neighbors.length; i++) {
                if (neighbors[i].equals("")) { continue; }
                int neighbor = Integer.parseInt(neighbors[i]);
                System.out.print(" " + neighbor);

                Edge edge = new Edge(new Node(vertex), new Node(neighbor));
                if (!edges.contains(edge)) {
                    edges.add(edge);
                }
            }
            Node node = new Node(vertex, xPos, yPos, color, null);
            nodes.add(node);
            System.out.println();
            count++;
        }
        DisplayGraph box = new DisplayGraph(nodes, edges, size);  // create a graphics
        System.out.println( "Done..." );
        keyboard.close();
    }
}
