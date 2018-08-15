package me.brandon.ai.sim.evolve.world.creature;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static me.brandon.ai.util.Options.*;

public class BrainFactory
{

	public static BrainFactory factory()
	{
		return new BrainFactory(inputNodeSize, outputNodeSize);
	}

	private List<Node> nodes;
	private List<Connection> connections;

	private int inputSize;
	private int outputSize;

	private int generation;

	public BrainFactory(int inputSize, int outputSize)
	{
		this.inputSize = inputSize;
		this.outputSize = outputSize;

		nodes = new ArrayList<>();
		connections = new ArrayList<>();
		for (int i = 0; i < inputSize; i++)
		{
			addNode().type = NodeType.Input;
		}
		for (int i = 0; i < outputSize; i++)
		{
			addNode().type = NodeType.Output;
		}

	}

	public void generateRandomBrain()
	{
		int outLim = inputSize + outputSize;
		for (int in = 0; in < inputSize; in++)
		{
			for (int out = inputSize; out < outLim; out++)
			{
				if (rand.nextFloat() < 0.5)
				{
					Connection con = new Connection();
					con.in = nodes.get(in);
					con.out = nodes.get(out);
					con.weight += (rand.nextFloat() - 0.5f) * brain_weight_init_mag;
					connections.add(con);
				}
			}
		}

		setConnections();

		mutate();

	}

	private void setConnections()
	{
		for (Node node : nodes)
		{
			node.reset();
		}
		for (Connection con : connections)
		{
			con.set();
		}
	}

	public Connection getRandomConnection()
	{
		return connections.get(rand.nextInt(connections.size()));
	}

	public void mutate()
	{
		determineLayers(2);


		if (generation > 2)
		{
			if (rand.nextFloat() < mut_brain_connectionSplit)
			{
				splitConnection(getRandomConnection());
				determineLayers(2);
			}
			if (rand.nextFloat() < mut_brain_connection_removal)
			{
				removeConnection(getRandomConnection());
			}
			if (rand.nextFloat() < mut_brain_connection_addition)
			{
				addRandomConnection();
			}
		}

		for (Connection con : connections)
		{
			con.weight += rand.nextFloat() * mut_brain_wight_amnt;
		}

		setConnections();

//		purgeNullNodes();

	}

	public void purgeNullNodes()
	{
		Iterator<Node> it = nodes.iterator();
		while (it.hasNext())
		{
			Node n = it.next();
			if (n.type == NodeType.Hidden)
			{
				if (n.inputs.size() == 0 || n.outputs.size() == 0)
				{
					it.remove();
				}
			}
		}
	}

	private void addRandomConnection()
	{
		Connection con = new Connection();
		Node node = getRandomNode();
		boolean uol = rand.nextBoolean();
		if (node.type == NodeType.Input || uol)
		{
			con.in = node;
			Node o = getRandomUpper(node);
			if (o != null)
				con.out = o;
		}
		else
		{
			con.out = node;
			Node o = getRandomLower(node);
			if (o != null)
				con.in = o;
		}

		if (con.out.id != con.in.id)
		{
			if (!connections.contains(con))
			{
				con.weight = (rand.nextFloat() - 0.5f) * brain_weight_init_mag * 2f;
				con.set();
				connections.add(con);
			}
		}
	}

	public void removeConnection(Connection con)
	{
		Node out = con.out;
		Node in = con.in;
		if ((out.type == NodeType.Output && out.inputs.size() <= 2) || in.type == NodeType.Input && in.outputs.size() <= 1)
		{
			return;
		}
		out.inputs.remove(con);
		in.outputs.remove(con);
		connections.remove(con);
	}

	public Node addNode()
	{
		Node node = new Node();
		node.type = NodeType.Hidden;
		node.id = nodes.size();
		nodes.add(node);
		return node;
	}

	public CreatureBrain generate(Creature creature)
	{
		CreatureBrain brain = new CreatureBrain(creature);
		brain.neurons = new CreatureBrain.Neuron[nodes.size()];
		brain.connections = new CreatureBrain.BrainConnection[connections.size()];

		determineLayers(1);

		brain.layerCount = 0;
		for (int i = 0; i < nodes.size(); i++)
		{
			CreatureBrain.Neuron n = brain.neurons[i] = new CreatureBrain.Neuron();
			brain.layerCount = Math.max(n.layer = nodes.get(i).layer, brain.layerCount);
		}

		brain.layerCount++;

		for (int i = 0; i < connections.size(); i++)
		{
			brain.connections[i] = connections.get(i).make();
		}

		brain.fin();

		return brain;
	}

	private Node getRandomNode()
	{
		return nodes.get(rand.nextInt(nodes.size()));
	}

	private int layerCount;
	private boolean layerCheck;

	private void determineLayers(int space)
	{
		layerCheck = !layerCheck;
		setConnections();
		layerCount = 0;
		for (Node node : nodes)
		{
			if (node.type == NodeType.Input)
			{
				node.layer = 0;

				calcChildLayers(node, space);
			}
		}

		for (Node node : nodes)
		{
			if (node.type == NodeType.Output)
			{
				node.layer = layerCount;
			}
		}
		layerCount++;
	}

	private void calcChildLayers(Node node, int space)
	{
		for (Connection con : node.outputs)
		{
			if (con.layerCheck != layerCheck)
			{
				con.layerCheck = layerCheck;
				Node n = con.out;
				n.layer = Math.max(n.layer, node.layer + space);
				if (n.type != NodeType.Output)
				{
					calcChildLayers(n, space);
				}
				layerCount = Math.max(layerCount, n.layer);
			}
		}
	}

	private Node splitConnection(Connection con)
	{

		int upper = con.out.layer;
		int lower = con.in.layer;
		if (upper - 1 == lower)
		{
			return null;
		}
		Node node = addNode();
		node.layer = (int) ((upper - lower + 1) * rand.nextFloat() + lower);
		node.type = NodeType.Hidden;

		Connection newCon = new Connection();
		newCon.weight = rand.nextFloat() - 0.5f;
		newCon.out = con.out;
		newCon.in = node;
		con.out = node;

		connections.add(newCon);

		return node;
	}

	private Node getRandomUpper(Node node)
	{
		Node ret;
		do
			ret = getRandomNode();
		while (ret.layer > node.layer);
		return ret;
	}

	private Node getRandomLower(Node node)
	{
		Node ret;
		do
			ret = getRandomNode();
		while (ret.layer < node.layer);
		return ret;
	}

	public void createFrom(CreatureBrain b1, CreatureBrain b2)
	{
		boolean same = false;
		if (b2 == null)
		{
			b2 = b1;
			same = true;
		}
		generation = Math.max(b1.creature.generation, b2.creature.generation) + 1;
		int nodeSize = Math.max(b1.neurons.length, b2.neurons.length);
		for (int i = nodes.size(); i < nodeSize; i++)
		{
			addNode();
		}

		for (CreatureBrain.BrainConnection bc : b1.connections)
		{
			Connection con = new Connection();
			con.in = nodes.get(bc.in);
			con.out = nodes.get(bc.out);
			if (!connections.contains(con))
			{
				con.weight = bc.weight;
				connections.add(con);
			}
		}
		if (!same)
		{
			for (CreatureBrain.BrainConnection bc : b2.connections)
			{
				Connection con = new Connection();
				con.in = nodes.get(bc.in);
				con.out = nodes.get(bc.out);
				if (!connections.contains(con))
				{
					con.weight = bc.weight;
					connections.add(con);
				}
				else if (rand.nextBoolean())
				{
					con = getConnection(con);
					if (con != null)
					{
						con.weight = bc.weight;
					}
				}
			}
		}
	}

	private Connection getConnection(int in, int out)
	{
		for (Connection con : connections)
			if (con.in.id == in && con.out.id == out)
				return con;
		return null;
	}

	private Connection getConnection(Connection c)
	{
		for (Connection con : connections)
			if (con.in.equals(c.in) && con.out.equals(c.out))
				return con;
		return null;
	}

	private class Node
	{
		int id;
		int layer;
		boolean enabled;

		NodeType type;

		List<Connection> inputs = new ArrayList<>();
		List<Connection> outputs = new ArrayList<>();

		void reset()
		{
			inputs.clear();
			outputs.clear();
		}

		public boolean equals(Object obj)
		{
			return obj instanceof Node && ((Node) obj).id == id;
		}
	}

	private class Connection
	{

		boolean layerCheck;

		Node in;
		Node out;

		float weight;

		CreatureBrain.BrainConnection make()
		{
			CreatureBrain.BrainConnection con = new CreatureBrain.BrainConnection();
			con.in = in.id;
			con.out = out.id;
			con.weight = weight;
			return con;
		}

		void set()
		{
			in.outputs.add(this);
			out.inputs.add(this);
		}

		public boolean equals(Object obj)
		{
			return obj instanceof Connection && ((Connection) obj).in.id == in.id && ((Connection) obj).out.id == out.id;
		}

	}

	private enum NodeType
	{
		Input, Output, Hidden;
	}

}
