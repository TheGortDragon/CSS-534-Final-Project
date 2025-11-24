public class Edge {
    Node n1;
    Node n2;

    public Edge(Node n1, Node n2) {
        this.n1 = n1;
        this.n2 = n2;
    }

    @Override
    public boolean equals(Object obj) {
        Edge edge = (Edge) obj;
        int thisVertex1 = this.n1.vertex;
        int thisVertex2 = this.n2.vertex;

        int thatVertex1 = edge.n1.vertex;
        int thatVertex2 = edge.n2.vertex;

        return (thisVertex1 == thatVertex1 && thisVertex2 == thatVertex2) 
            || (thisVertex2 == thatVertex1 && thisVertex1 == thatVertex2);
    }

    public Edge reverse() {
        return new Edge(this.n2, this.n1);
    }
}
