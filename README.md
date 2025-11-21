# Display Graph

## planargraph.txt
Each node contains a vertex, x and y coordinate, color character, and a list of neighboring nodes. There is one node per line, and each part is separated by spaces.

For example, vertex 2, with the coordinates (160, 20), a color of N, and a list of neighbors containing vertex 1 and 3 would be the following:

> 2 160 20 N 1 3

The color key is shown below:

- N = No Color (Gray)
- R = Red
- G = Green
- B = Blue
- Y = Yellow

## Execution
To output to the display, simply output the graph to the console, following the format above, and pipe it to the DisplayGraph, as shown below:

> java PROGRAM_NAME_HERE | java DisplayGraph

To see an example of the expected output, run the following command:

> cat possibleSolution.txt | java DisplayGraph