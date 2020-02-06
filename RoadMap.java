
/* C1821631
	Rhys Connor
 * Optionally, if you have any comments regarding your submission, put them here.
 * For instance, specify here if your program does not generate the proper output or does not do it in the correct manner.
 */

import java.util.*;
import java.io.*;

class Vertex {

	// Constructor: set name, chargingStation and index according to given values,
	// initilaize incidentRoads as empty array
	public Vertex(String placeName, boolean chargingStationAvailable, int idx) {
		name = placeName;
		incidentRoads = new ArrayList<Edge>();
		index = idx;
		chargingStation = chargingStationAvailable;
	}

	public String getName() {
		return name;
	}

	public boolean hasChargingStation() {
		return chargingStation;
	}

	public ArrayList<Edge> getIncidentRoads() {
		return incidentRoads;
	}

	// Add a road to the array incidentRoads
	public void addIncidentRoad(Edge road) {
		incidentRoads.add(road);
	}

	public int getIndex() {
		return index;
	}

	private String name; // Name of the place
	private ArrayList<Edge> incidentRoads; // Incident edges
	private boolean chargingStation; // Availability of charging station
	private int index; // Index of this vertex in the vertex array of the map
}

class Edge {
	public Edge(int roadLength, Vertex firstPlace, Vertex secondPlace) {
		length = roadLength;
		incidentPlaces = new Vertex[] { firstPlace, secondPlace };
	}

	public Vertex getFirstVertex() {
		return incidentPlaces[0];
	}

	public Vertex getSecondVertex() {
		return incidentPlaces[1];
	}

	public int getLength() {
		return length;
	}

	private int length;
	private Vertex[] incidentPlaces;
}

// A class that represents a sparse matrix
public class RoadMap {

	// Default constructor
	public RoadMap() {
		places = new ArrayList<Vertex>();
		roads = new ArrayList<Edge>();
	}

	// Auxiliary function that prints out the command syntax
	public static void printCommandError() {
		System.err.println("ERROR: use one of the following commands");
		System.err.println(" - Read a map and print information: java RoadMap -i <MapFile>");
		System.err.println(
				" - Read a map and find shortest path between two vertices with charging stations: java RoadMap -s <MapFile> <StartVertexIndex> <EndVertexIndex>");
	}

	public static void main(String[] args) throws Exception {
		if (args.length == 2 && args[0].equals("-i")) {
			RoadMap map = new RoadMap();
			try {
				map.loadMap(args[1]);
			} catch (Exception e) {
				System.err.println("Error in reading map file");
				System.exit(-1);
			}

			System.out.println("Read road map from " + args[1] + ":");
			map.printMap();
		} else if (args.length == 4 && args[0].equals("-s")) {
			RoadMap map = new RoadMap();
			map.loadMap(args[1]);
			System.out.println("Read road map from " + args[1] + ":");
			map.printMap();

			int startVertexIdx = -1, endVertexIdx = -1;
			try {
				startVertexIdx = Integer.parseInt(args[2]);
				endVertexIdx = Integer.parseInt(args[3]);
			} catch (NumberFormatException e) {
				System.err.println("Error: start vertex and end vertex must be specified using their indices");
				System.exit(-1);
			}

			if (startVertexIdx < 0 || startVertexIdx >= map.numPlaces()) {
				System.err.println("Error: invalid index for start vertex");
				System.exit(-1);
			}

			if (endVertexIdx < 0 || endVertexIdx >= map.numPlaces()) {
				System.err.println("Error: invalid index for end vertex");
				System.exit(-1);
			}

			Vertex startVertex = map.getPlace(startVertexIdx);
			Vertex endVertex = map.getPlace(endVertexIdx);
			if (!map.isConnectedWithChargingStations(startVertex, endVertex)) {
				System.out.println();
				System.out.println("There is no path connecting " + map.getPlace(startVertexIdx).getName() + " and "
						+ map.getPlace(endVertexIdx).getName() + " with charging stations");
			} else {
				ArrayList<Vertex> path = map.shortestPathWithChargingStations(startVertex, endVertex);
				System.out.println();
				System.out.println("Shortest path with charging stations between " + startVertex.getName() + " and "
						+ endVertex.getName() + ":");
				map.printPath(path);
			}

		} else {
			printCommandError();
			System.exit(-1);
		}
	}

	// Load matrix entries from a text file
	public void loadMap(String filename) {
		File file = new File(filename);
		places.clear();
		roads.clear();

		try {
			Scanner sc = new Scanner(file);

			// Read the first line: number of vertices and number of edges
			int numVertices = sc.nextInt();
			int numEdges = sc.nextInt();

			for (int i = 0; i < numVertices; ++i) {
				// Read the vertex name and its charing station flag
				String placeName = sc.next();
				int charginStationFlag = sc.nextInt();
				boolean hasChargingStataion = (charginStationFlag == 1);

				// Add your code here to create a new vertex using the information above and add
				// it to places

				Vertex newVertex = new Vertex(placeName, hasChargingStataion, i);
				places.add(newVertex);

			}

			for (int j = 0; j < numEdges; ++j) {
				// Read the edge length and the indices for its two vertices
				int vtxIndex1 = sc.nextInt();
				int vtxIndex2 = sc.nextInt();
				int length = sc.nextInt();
				Vertex vtx1 = places.get(vtxIndex1);
				Vertex vtx2 = places.get(vtxIndex2);

				// Add your code here to create a new edge using the information above and add
				// it to roads
				// You should also set up incidentRoads for each vertex

				Edge newEdge = new Edge(length, vtx1, vtx2);
				Edge reverseEdge = new Edge(length, vtx2, vtx1);

				vtx1.addIncidentRoad(newEdge);
				vtx2.addIncidentRoad(reverseEdge);

				roads.add(newEdge);
			}

			sc.close();

			// Add your code here if approparite
		} catch (Exception e) {
			e.printStackTrace();
			places.clear();
			roads.clear();
		}
	}

	// Return the shortest path between two given vertex, with charging stations on
	// each itermediate vertex.
	public ArrayList<Vertex> shortestPathWithChargingStations(Vertex startVertex, Vertex endVertex) {

		// Initialize an empty path
		ArrayList<Vertex> path = new ArrayList<Vertex>();

		// Sanity check for the case where the start vertex and the end vertex are the
		// same
		if (startVertex.getIndex() == endVertex.getIndex()) {
			path.add(startVertex);
			return path;
		}

		// Add your code here
		else{
			
			Set<Vertex> visitedVerticies= new HashSet<Vertex>();
			Set<Vertex> unvisitedVerticies = new HashSet<Vertex>();

			visitedVerticies.add(startVertex);
			path.add(startVertex);
			int currentVertexIndex = startVertex.getIndex();

			for (Vertex v : places) unvisitedVerticies.add(v);

			while (unvisitedVerticies.size() > 0 || !(path.contains(endVertex))){


				Vertex currentVertex = places.get(currentVertexIndex);
				int noOfEdges = currentVertex.getIncidentRoads().size();


				PriorityQueue<Edge> adjacentEdges = new PriorityQueue<Edge>(noOfEdges, new Comparator<Edge>(){
					public int compare(Edge e1, Edge e2){
						//comparitor added to order by length of edge
						if (e1.getLength() < e2.getLength()) return -1;
						if (e1.getLength() > e2.getLength()) return 1;
						return 0;
					}
				});

				//add edges to priority queue 
				for (Edge e: startVertex.getIncidentRoads()){
					adjacentEdges.add(e);
				}

				//print statement to debug and ensure that each vertex is in correct order 
				System.out.println(adjacentEdges);
				Edge edgeToUse = adjacentEdges.peek();
				
				//take the edge to use and compare the current vertex to the two stored in the edge
				if (edgeToUse.getFirstVertex() != currentVertex){

					if (edgeToUse.getFirstVertex() == endVertex){

						path.add(edgeToUse.getFirstVertex());

					}else{

						path.add(edgeToUse.getFirstVertex());
						currentVertexIndex = edgeToUse.getFirstVertex().getIndex();

					}
				}else{
					if (edgeToUse.getSecondVertex() == endVertex){

						path.add(edgeToUse.getSecondVertex());

					}else{

						path.add(edgeToUse.getSecondVertex());
						currentVertexIndex = edgeToUse.getSecondVertex().getIndex();

					}
				}
			}
			return path;
		}
	}

	void depthSearch(int startVertex){
		
		boolean[] vertexVisited = new boolean[numPlaces()];

		dfsRecursion(startVertex, vertexVisited);

	}

	private void dfsRecursion(int currentVertex, boolean[] vertexVisited) {

		vertexVisited[currentVertex] = true;

		Vertex vertex = places.get(currentVertex);

		int noOfEdges = vertex.getIncidentRoads().size();

		PriorityQueue<Edge> adjacentEdges = new PriorityQueue<Edge>(noOfEdges, new Comparator<Edge>(){
			public int compare(Edge e1, Edge e2){
				//comparitor added to order by length of edge
				if (e1.getLength() < e2.getLength()) return -1;
				if (e1.getLength() > e2.getLength()) return 1;
				return 0;
			}
		});

		for (Edge e : adjacentEdges){
			if ((e.getFirstVertex() == vertex) & !vertexVisited[e.getSecondVertex().getIndex()]){

				dfsRecursion(e.getSecondVertex().getIndex(), vertexVisited);
				
			} else if(!vertexVisited[e.getFirstVertex().getIndex()]){

				dfsRecursion(e.getFirstVertex().getIndex(), vertexVisited);

			}
		}
	}

	// Check if two vertices are connected by a path with charging stations on each
	// itermediate vertex.
	// Return true if such a path exists; return false otherwise.
	// The worst-case time complexity of your algorithm should be no worse than O(v + e),
	// where v and e are the number of vertices and the number of edges in the graph.
	public boolean isConnectedWithChargingStations(Vertex startVertex, Vertex endVertex) {
		// Sanity check
		if (startVertex.getIndex() == endVertex.getIndex()) {
			return true;
		}
		// Add your code here
		else{
			int currentVertexIndex = startVertex.getIndex();
			
			int isConnectedWithChargingStationsStatus = 0;

			while (currentVertexIndex != endVertex.getIndex()){

				Vertex currentVertex = places.get(currentVertexIndex);
				
				int noOfEdges = currentVertex.getIncidentRoads().size();
				int x = 0;

				//initilaize array to store boolean values 
				List<Boolean> charingStationList = new ArrayList<Boolean>();

				//Array to store the vertex visited
				Integer[] vertexVisited = new Integer[numPlaces()];
				vertexVisited[x] = currentVertex.getIndex();
				x++;

				ArrayList<Edge> currentVertexEdges = new ArrayList<Edge>(currentVertex.getIncidentRoads());

				//for loop to iterate through each edge on the vertex
				//Could use for (Edge edge : currentVertex.getIncidentRoads()){}; 
				for (int i=0; i < noOfEdges; i++){

					Edge currentEdge = currentVertexEdges.get(i);

					//array to list to check value
					List<Integer> vertexVisitedList = Arrays.asList(vertexVisited);

					if (x == 0 || !(vertexVisitedList.contains(currentEdge.getSecondVertex().getIndex()))){

						if(currentEdge.getSecondVertex().hasChargingStation() == true){ 
								
							if (currentEdge.getSecondVertex().getIndex() == endVertex.getIndex()){

								if (charingStationList.contains(false)){
									//sanity check 
									isConnectedWithChargingStationsStatus = 0;
									//to ensure exit out of loop
									currentVertexIndex = endVertex.getIndex();
								} else{
									isConnectedWithChargingStationsStatus = 1;
									//to ensure exit out of loop
									currentVertexIndex = endVertex.getIndex();
								}
							}
							else{
								//If the edge has a vertex with charging station make currentVertex the destination Vertex
								charingStationList.add(true);
								vertexVisited[x] = currentVertex.getIndex();
								x++;
								currentVertex = currentEdge.getSecondVertex();
								currentVertexIndex = currentEdge.getSecondVertex().getIndex();
							}
						} else if (i == noOfEdges - 1) {
							charingStationList.add(false);
							vertexVisited[x] = currentVertex.getIndex();
							x++;
							isConnectedWithChargingStationsStatus = 0;
							//to ensure exit out of loop
							currentVertexIndex = endVertex.getIndex();
						}
					} 
				}
			}
		return isConnectedWithChargingStationsStatus == 1;
		}
	}

	public void printMap() {
		System.out.println("The map contains " + this.numPlaces() + " places and " + this.numRoads() + " roads");
		System.out.println();

		System.out.println("Places:");

		for (Vertex v : places) {
			System.out.println("- name: " + v.getName() + ", charging station: " + v.hasChargingStation());
		}

		System.out.println();
		System.out.println("Roads:");

		for (Edge e : roads) {
			System.out.println("- (" + e.getFirstVertex().getName() + ", " + e.getSecondVertex().getName()
					+ "), length: " + e.getLength());
		}
	}

	public void printPath(ArrayList<Vertex> path) {
		System.out.print("(  ");

		for (Vertex v : path) {
			System.out.print(v.getName() + "  ");
		}

		System.out.println(")");
	}

	public int numPlaces() {
		return places.size();
	}

	public int numRoads() {
		return roads.size();
	}

	public Vertex getPlace(int idx) {
		return places.get(idx);
	}

	private ArrayList<Vertex> places;
	private ArrayList<Edge> roads;
}
