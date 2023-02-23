import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static java.lang.Math.min;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class project5 {
	private static class Edge {
		public int fromTheNode, toNode;
		public Edge residual;
		public int flow;
		public final int capacity;

		public Edge(int fromTheNode, int toNode, int capacity) {
			this.fromTheNode = fromTheNode;
			this.toNode = toNode;
			this.capacity = capacity;
		}


		public int remainingCapacity() {
			return capacity- flow;
		}

		public void augment(long btl) {
			flow+= btl;
			residual.flow -= btl;
		}

	}

	private static abstract class NetworkFlow{
		static final Long INF = Long.MAX_VALUE / 2;

		final int n, s, t;

		protected int visitedToken = 1;
		protected int[] visited;

		protected boolean solved;

		protected int maxFLow;

		protected List<Edge>[] graph;

		public NetworkFlow(int n, int s, int t) {
			this.n = n;
			this.s = s;
			this.t = t;
			initializeEmptyGraph();
			visited = new int[n];
		}

		private void initializeEmptyGraph() {
			graph = new List[n];
			for(int k=0;k<n;k++) {
				graph[k] = new ArrayList<Edge>();
			}
		}

		public void addEdge(int fromTheNode, int toNode, int capacity) {
			Edge first = new Edge(fromTheNode, toNode, capacity);
			Edge second = new Edge(toNode, fromTheNode,0);

			first.residual = second;
			second.residual = first;

			graph[fromTheNode].add(first);
			graph[toNode].add(second);
		}

		public List<Edge>[] getGraph(){
			execute();
			return graph;
		}

		public int getMaxFLow() {
			execute();
			return maxFLow;
		}
		private void execute() {
			if(solved) return;
			solved = true;
			solve();					
		}

		public abstract void solve();
	}

	private static class FordFulkerson extends NetworkFlow{
		public FordFulkerson(int n, int s, int t) {
			super(n, s, t);
		}

		public void solve() {
			//System.out.println(dfs(s, INF));
			for(long f = dfs(s, INF); f!=0; f=dfs(s, INF)) {
				visitedToken++;
				maxFLow+=f;
			}
			
		}

		private long dfs(int node, long flow) {
			if(node == t) return flow;
			visited[node] = visitedToken;

			List<Edge> edges = graph[node];

		
			for(Edge edge: edges) {

				if(edge.remainingCapacity() > 0 && visited[edge.toNode] != visitedToken) {
					long btl = dfs(edge.toNode, min(flow, edge.remainingCapacity()));

					if(btl >0) {
						edge.augment(btl);
						return btl;
					}
				}
			}
			return 0;
		}
		
	}
	
	public static int findIndex(String[] myarr, int value) {
		int count =0;
		for(int i=0; i<myarr.length; i++) {
			if(Integer.parseInt(myarr[i]) == value) {
				return count;	
			}
			count++;
		}
		return count;
	}

	public static void main(String[] args) throws IOException {
		//number of nodes
		//assign each node an index

		File inputFile = new File(args[0]);
		File outputFile = new File(args[1]);
		FileWriter writer = new FileWriter(outputFile);

		Scanner scanner = new Scanner(inputFile);

		ArrayList<String> toBeIndexed = new ArrayList<>();

		String[] sourceToNode = null ;

		int count = 1;
		int numOfNodes = 0;
		while(scanner.hasNextLine()) {
			if(count == 1) {
				int addThis = Integer.parseInt(scanner.nextLine());
				count++;
				numOfNodes+=addThis;
			}

			if(count == 2) {
				sourceToNode = scanner.nextLine().split(" ");
				numOfNodes+=sourceToNode.length;
				count++;
			}


			if(count >=3) {
				//Edges from source 
				String[] line = scanner.nextLine().split(" ");
				toBeIndexed.add(line[0]); 
				count++;
			}
		}
		numOfNodes+=2;
		toBeIndexed.add("KL");
		toBeIndexed.add("S");


		NetworkFlow solver = new FordFulkerson(numOfNodes, toBeIndexed.size()-1, toBeIndexed.size()-2);

		Scanner scanner2 = new Scanner(inputFile);

		for(int i=0; i<2;i++) {
			scanner2.nextLine();
		}
		
		while(scanner2.hasNextLine()) {
			String[] forGr = scanner2.nextLine().split(" ");

			//edges from source
			int source = toBeIndexed.size()-1;
			
			if(toBeIndexed.indexOf(forGr[0])<=5) {
				solver.addEdge(source, toBeIndexed.indexOf(forGr[0]), Integer.parseInt(sourceToNode[toBeIndexed.indexOf(forGr[0])]));
			}

			for(int i=0; i<forGr.length/2; i++) {
				solver.addEdge(toBeIndexed.indexOf(forGr[0]), toBeIndexed.indexOf(forGr[2*i+1]), Integer.parseInt(forGr[2*i+2]));	
			}
		}
		
		List<Edge>[] resultGraph = solver.getGraph();
		writer.write(Integer.toString(solver.getMaxFLow())+ "\n");
		
		
		for(List<Edge> edges : resultGraph) {
			for(Edge e: edges) {
				if(e.fromTheNode == toBeIndexed.size()-1) {				
					if(e.flow== e.capacity) {
						writer.write(String.valueOf(toBeIndexed.get(findIndex(sourceToNode, e.capacity))) + "\n");
						
					}
				}
			}
		}
		
		writer.close();
		

	}

}

