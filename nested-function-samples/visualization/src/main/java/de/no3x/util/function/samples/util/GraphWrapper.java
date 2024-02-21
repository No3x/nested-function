package de.no3x.util.function.samples.util;

import com.google.common.graph.EndpointPair;
import com.google.common.graph.MutableNetwork;
import com.google.common.graph.Network;
import com.google.common.graph.NetworkBuilder;

import java.util.function.IntSupplier;

@SuppressWarnings("UnstableApiUsage")
public class GraphWrapper<N> {

	private final MutableNetwork<N, Integer> graph;
	private N rootNode;
	private N lastInsertedNode;

	IntSupplier edgeFactory = new IntSupplier() {
		int n = 0;

		@Override
		public int getAsInt() {
			return n++;
		}
	};

	public GraphWrapper() {
		graph = NetworkBuilder.directed().build();
	}

	public void addNode(N n) {
		if (rootNode == null) {
			rootNode = n;
		}
		if (lastInsertedNode == null) {
			lastInsertedNode = n;
			graph.addNode(n);
			return;
		}
		graph.addEdge(EndpointPair.ordered(lastInsertedNode, n), edgeFactory.getAsInt());
		lastInsertedNode = n;
	}

	public void addNode(N existingNode, N newNode) {
		graph.addNode(newNode);
		lastInsertedNode = newNode;
		graph.addEdge(EndpointPair.ordered(existingNode, newNode), edgeFactory.getAsInt());
	}

	public Network<N, Integer> getGraph() {
		return graph;
	}

}
