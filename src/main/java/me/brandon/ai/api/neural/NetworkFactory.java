package me.brandon.ai.api.neural;

import me.brandon.ai.api.neural.impl.NConnection;
import me.brandon.ai.api.neural.impl.NNetwork;
import me.brandon.ai.api.neural.impl.BasicNeuron;
import me.brandon.ai.api.neural.impl.basic.InputNeuron;
import me.brandon.ai.api.neural.impl.basic.OutputNeuron;
import me.brandon.ai.util.Chance;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NetworkFactory
{

	private static final Random rand = new Random();

	public static NetworkFactory createFactory()
	{
		return new NetworkFactory();
	}

	public static NetworkFactory createFromExisting(NNetwork network)
	{
		return createFactory().fromExisting(network);
	}

	public static <T extends Network> T buildFromExisting(NNetwork network)
	{
		return createFactory().fromExisting(network).build();
	}

	private int _nId;

	private List<List<FactoryNeuron>> layers;

	private Constructor<? extends BasicNeuron> neuronClass;
	private Object[] neuronClassParameters;

	public NetworkFactory()
	{
		layers = new ArrayList<>();
	}

	/**
	 * generates the final compressed network as specified from this factory
	 *
	 * @return the network
	 */
	public <T extends Network> T build()
	{
		BasicNeuron neuronNetwork[][] = new BasicNeuron[layers.size()][];
		for (int i = 0; i < layers.size(); i++)
		{
			List<FactoryNeuron> layer = layers.get(i);
			neuronNetwork[i] = new BasicNeuron[layer.size()];
			for (int j = 0; j < layer.size(); j++)
			{
				neuronNetwork[i][j] = layer.get(j).makeNeuron();
			}
		}

		NNetwork network = new NNetwork();
		network.setNeuronNetwork(neuronNetwork);

		return (T) network;
	}

	/**
	 * Adds the input nodes for the network
	 *
	 * @param inputSize - the number of input nodes needed
	 */
	public NetworkFactory input(int inputSize)
	{
		int lc = layers.size();
		List<FactoryNeuron> layer = new ArrayList<>();
		for (int i = 0; i < inputSize; i++)
		{
			layer.add(new FactoryNeuron(new InputNeuron(), lc, i));
		}
		layers.add(layer);
		return this;
	}


	/**
	 * Adds the output nodes for the network
	 *
	 * @param outputSize - the number of output nodes needed
	 */
	public NetworkFactory output(int outputSize)
	{
		int lc = layers.size();
		List<FactoryNeuron> layer = new ArrayList<>(outputSize);
		for (int i = 0; i < outputSize; i++)
		{
			layer.add(new FactoryNeuron(new OutputNeuron(), lc, i));
		}
		layers.add(layer);
		return this;
	}

	/**
	 * Adds a hidden layer to the network
	 *
	 * @param layerSize - the size of the hidden layer
	 */
	public NetworkFactory layer(int layerSize)
	{
		int lc = layers.size();
		List<FactoryNeuron> layer = new ArrayList<>(layerSize);
		for (int i = 0; i < layerSize; i++)
		{
			layer.add(new FactoryNeuron(makeNewNeuron(NeuronType.Hidden), lc, i));
		}
		layers.add(layer);
		return this;
	}

	/**
	 * Manually adds a neuron to a specified layer
	 *
	 * @param neuron - neuron object to add
	 * @param layer  - the layer to add the neuron
	 */
	public NetworkFactory addNeuron(BasicNeuron neuron, int layer)
	{
		layers.get(layer).add(new FactoryNeuron(neuron, layer, layers.size()));
		return this;
	}

	/**
	 * Adds every possible connection between two layers in the direction
	 * of input(from) -> output(to)
	 *
	 * @param fromLayer - the input layer
	 * @param toLayer   - the target layer
	 */
	public NetworkFactory fullyConnect(int fromLayer, int toLayer)
	{
		List<FactoryNeuron> from = layers.get(fromLayer);
		List<FactoryNeuron> to = layers.get(toLayer);
		FactoryConnection con;
		for (int i = 0; i < from.size(); i++)
		{
			for (int j = 0; j < to.size(); j++)
			{
				con = new FactoryConnection();
				con.from = from.get(i);
				con.to = to.get(j);
				con.set();
			}
		}
		return this;
	}


	/**
	 * Adds random connections between two layers in the direction
	 * of input(from) -> output(to)
	 *
	 * @param fromLayer - the input layer
	 * @param toLayer   - the target layer
	 */
	public NetworkFactory randomConnect(int fromLayer, int toLayer, double chance)
	{
		List<FactoryNeuron> from = layers.get(fromLayer);
		List<FactoryNeuron> to = layers.get(toLayer);
		FactoryConnection con;
		for (int i = 0; i < from.size(); i++)
		{
			for (int j = 0; j < to.size(); j++)
			{
				if (rand.nextFloat() < chance)
				{
					con = new FactoryConnection();
					con.from = from.get(i);
					con.to = to.get(j);
					con.set();
				}
			}
		}

		return this;
	}

	public NetworkFactory addRandomConnections(int fromLayer, int toLayer, int count)
	{
		List<FactoryNeuron> from = layers.get(fromLayer);
		List<FactoryNeuron> to = layers.get(toLayer);
		for (int i = 0; i < count; i++)
		{
			FactoryNeuron f = from.get((int) Chance.randMax(from.size()));
			FactoryNeuron t = to.get((int) Chance.randMax(to.size()));
			new FactoryConnection(f, t, Chance.rand());
		}
		return this;
	}

	public NetworkFactory addRandomConnectionPath(int fromLayer, int toLayer, int count)
	{
		for (int i = 0; i < count; i++)
		{
			List<FactoryNeuron> from = layers.get(fromLayer);
			FactoryNeuron cur = from.get((int) Chance.randMax(from.size()));
			for (int layer = fromLayer + 1; layer < toLayer; layer++)
			{
				if (layer < toLayer - 1)
					layer = (int) (Chance.randMax(toLayer - layer) + layer);
				cur = createRandom(cur, layer);
			}
		}
		return this;
	}

	private FactoryNeuron createRandom(FactoryNeuron from, int targetLayer)
	{
		List<FactoryNeuron> to = layers.get(targetLayer);
		FactoryNeuron ret = to.get((int) Chance.randMax(to.size()));
		new FactoryConnection(from, ret, Chance.randVal());
		return ret;
	}


	/**
	 * Adds a connection with specified weight between two nodes
	 *
	 * @param fromLayer - the input layer
	 * @param fromNode  - the source node index within the source layer
	 * @param toLayer   - the target layer
	 * @param toNode    - the target node index within the target layer
	 */
	public NetworkFactory connect(int fromLayer, int fromNode, int toLayer, int toNode, float weight)
	{
		FactoryNeuron from = layers.get(fromLayer).get(fromNode);
		FactoryNeuron to = layers.get(toLayer).get(toNode);
		new FactoryConnection(from, to, weight);
		return this;
	}


	/**
	 * Manually sets the weight for a connection between two nodes
	 *
	 * @param fromLayer - the input layer
	 * @param fromNode  - the source node index within the source layer
	 * @param toLayer   - the target layer
	 * @param toNode    - the target node index within the target layer
	 */
	public NetworkFactory setWeight(int fromLayer, int fromNode, int toLayer, int toNode, float weight)
	{
		FactoryNeuron from = layers.get(fromLayer).get(fromNode);
		FactoryNeuron to = layers.get(toLayer).get(toNode);
		new FactoryConnection(from, to, weight);
		return this;
	}

	/**
	 * Sets the weight to a constant value for every connection between two layers
	 * <p>
	 * ** Does not affect connections that already has a weight value specified **
	 *
	 * @param fromLayer - the input layer
	 * @param toLayer   - the target layer
	 * @param weight    - the constant weight value
	 */
	public NetworkFactory weightConstant(int fromLayer, int toLayer, float weight)
	{
		layers.get(fromLayer).forEach(neuron ->
				neuron.outputs.forEach(con ->
				{
					if (con.to.layer == toLayer)
						con.setWeight(weight);
				}));
		return this;
	}

	/**
	 * Sets the weight to a constant value for every connection going away from a layer
	 * <p>
	 * ** Does not affect connections that already has a weight value specified **
	 *
	 * @param fromLayer - the input layer
	 * @param weight    - the constant weight value
	 */
	public NetworkFactory weightConstant(int fromLayer, float weight)
	{
		layers.get(fromLayer).forEach(neuron ->
				neuron.outputs.forEach(con ->
				{
					con.setWeight(weight);
				}));
		return this;
	}

	/**
	 * Sets the weight to a constant value for every connection in the network
	 * <p>
	 * ** Does not affect connections that already has a weight value specified **
	 *
	 * @param weight - the constant weight value
	 */
	public NetworkFactory weightConstant(float weight)
	{
		layers.forEach(layer -> layer.forEach(neuron ->
				neuron.outputs.forEach(con ->
				{
					con.setWeight(weight);
				})));
		return this;
	}

	/**
	 * Sets the weight value to a random number between a max and min for every
	 * connection between two layers
	 * <p>
	 * ** Does not affect connections that already has a weight value specified **
	 *
	 * @param fromLayer - the input layer
	 * @param toLayer   - the target layer
	 * @param max       - the max weight value
	 * @param min       - the min weight value
	 */
	public NetworkFactory weightRandom(int fromLayer, int toLayer, float min, float max)
	{
		layers.get(fromLayer).forEach(neuron ->
				neuron.outputs.forEach(con ->
				{
					if (con.to.layer == toLayer)
						con.setWeight(rand.nextFloat() * (max - min) + min);
				}));
		return this;
	}

	/**
	 * Sets the weight value to a random number between a max and min for every
	 * connection leaving the specified layer
	 * <p>
	 * ** Does not affect connections that already has a weight value specified **
	 *
	 * @param fromLayer - the input layer
	 * @param max       - the max weight value
	 * @param min       - the min weight value
	 */
	public NetworkFactory weightRandom(int fromLayer, float min, float max)
	{
		layers.get(fromLayer).forEach(neuron ->
				neuron.outputs.forEach(con ->
				{
					con.setWeight(rand.nextFloat() * (max - min) + min);
				}));
		return this;
	}


	/**
	 * Sets the weight value to a random number between a max and min for every
	 * connection in the network
	 * <p>
	 * ** Does not affect connections that already has a weight value specified **
	 *
	 * @param max - the max weight value
	 * @param min - the min weight value
	 */
	public NetworkFactory weightRandom(float min, float max)
	{
		layers.forEach(layer -> layer.forEach(neuron ->
				neuron.outputs.forEach(con ->
				{
					con.setWeight(rand.nextFloat() * (max - min) + min);
				})));
		return this;
	}

	/**
	 * Sets the weight value to a random number between 0 and 1 for every
	 * connection in the network
	 * <p>
	 * ** Does not affect connections that already has a weight value specified **
	 */
	public NetworkFactory weightRandom()
	{
		layers.forEach(layer -> layer.forEach(neuron ->
				neuron.outputs.forEach(con ->
				{
					con.setWeight((rand.nextFloat() - 0.5f) * 2);
				})));
		return this;
	}

	public NetworkFactory disableRandom(float chance)
	{
		layers.forEach(layer -> layer.forEach(neuron ->
		{
			neuron.enabled = !Chance.chance(chance);
			if (!neuron.enabled)
			{
				neuron.outputs.forEach(o -> o.enabled = false);
				neuron.inputs.forEach(o -> o.enabled = false);
			}
			else
			{
				neuron.outputs.forEach(o -> o.enabled = !Chance.chance(chance));
				neuron.inputs.forEach(o -> o.enabled = !Chance.chance(chance));
			}
		}));
		return this;
	}

	/**
	 * @param neuronClass - the class type of the neuron to be created when building the network
	 */
	public NetworkFactory neuronClass(Class<? extends BasicNeuron> neuronClass, Class<?>[] constructors, Object... params)
	{
		try
		{
			this.neuronClass = neuronClass.getConstructor(constructors);
			this.neuronClassParameters = params;
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return this;
	}

	/**
	 * @param neuronClass - the class type of the neuron to be created when building the network
	 */
	public NetworkFactory neuronClass(Class<? extends BasicNeuron> neuronClass)
	{
		try
		{
			this.neuronClass = neuronClass.getConstructor();
			this.neuronClassParameters = new Object[]{};
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return this;
	}

	private BasicNeuron makeNewNeuron(NeuronType type)
	{
		if (neuronClass != null)
		{
			try
			{
				BasicNeuron neuron = neuronClass.newInstance(neuronClassParameters);
				neuron.setType(type);

				return neuron;

			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		return new BasicNeuron(type);
	}

	private BasicNeuron fromNeuron(BasicNeuron n)
	{
		try
		{
			BasicNeuron neuron = n.getClass().newInstance();
			neuron.setType(n.type());

			return neuron;

		} catch (Exception e)
		{
			e.printStackTrace();
		}

		return new BasicNeuron();
	}

	public NetworkFactory fromExisting(NNetwork net)
	{

		net.forEachLayer((lay, ignore, neurons) ->
		{
			// add neurons to network
			List<FactoryNeuron> layer = getLayer(lay);
			for (int i = 0; i < neurons.length; i++)
			{
				BasicNeuron n = neurons[i];
				FactoryNeuron newNeuron = new FactoryNeuron(fromNeuron(n), lay, i);
				layer.add(newNeuron);

				NConnection[] cons = n.getInputs();
				for (NConnection con : cons)
				{
					FactoryNeuron from = getNeuron(con.sourceLayer(), con.sourceIndex());
					new FactoryConnection(from, newNeuron, con.weight());
				}
			}

		});

		return this;
	}

	private FactoryNeuron getNeuron(int layer, int index)
	{
		return layers.get(layer).get(index);
	}

	private List<FactoryNeuron> getLayer(int layer)
	{
		while (layers.size() <= layer)
		{
			layers.add(new ArrayList<>());
		}
		return layers.get(layer);
	}

	public NetworkFactory weightRandomizer()
	{
		//TODO: Add some sort of method for network mutations
		return this;
	}

	private class FactoryNeuron
	{
		final int id;
		NeuronType type;
		final int layer;
		BasicNeuron neuron;

		boolean enabled;

		List<FactoryConnection> inputs;
		List<FactoryConnection> outputs;


		FactoryNeuron(NeuronType type, int layer, int index)
		{
			this(layer);
			this.type = type;

			this.neuron = makeNewNeuron(type);
			this.neuron.layer = layer;
			this.neuron.setIndex(index);
			this.enabled = neuron.enabled();
		}

		FactoryNeuron(BasicNeuron neuron, int layer, int index)
		{
			this(layer);
			this.type = neuron.type();
			this.neuron = neuron;
			this.neuron.layer = layer;
			this.neuron.setIndex(index);
			this.enabled = neuron.enabled();
		}

		FactoryNeuron(int layer)
		{
			id = _nId++;
			this.layer = layer;
			inputs = new ArrayList<>();
			outputs = new ArrayList<>();
			this.enabled = true;
		}

		BasicNeuron makeNeuron()
		{

			if (type == NeuronType.Input || type == NeuronType.Output)
				enabled = true;

			NConnection connections[] = new NConnection[inputs.size()];
			for (int i = 0; i < inputs.size(); i++)
			{
				FactoryConnection con = inputs.get(i);
				connections[i] = new NConnection(con.from.neuron, con.weight);
				connections[i].setEnabled(con.enabled && con.to.enabled && con.from.enabled);
			}

			neuron.setConnections(connections);
			neuron.setEnabled(enabled);
			return neuron;
		}

	}

	private class FactoryConnection
	{
		FactoryNeuron to;
		FactoryNeuron from;

		float weight;
		boolean weightSet = false;
		boolean enabled = true;

		FactoryConnection()
		{

		}

		FactoryConnection(FactoryNeuron from, FactoryNeuron to, float weight)
		{
			this(from, to);
			this.weight = weight;
			this.weightSet = true;
			this.enabled = from.enabled && to.enabled;
		}

		FactoryConnection(FactoryNeuron from, FactoryNeuron to)
		{
			assert to.layer != from.layer;
			assert to.layer > from.layer;

			this.from = from;
			this.to = to;
			this.enabled = from.enabled && to.enabled;

			set();
		}

		void setWeight(float weight)
		{
			if (!weightSet)
				this.weight = weight;
			weightSet = true;
		}


		void set()
		{
			if (!to.inputs.contains(this))
			{
				to.inputs.add(this);
			}
			else
			{
				to.inputs.remove(this);
			}
			if (!from.outputs.contains(this))
			{
				from.outputs.add(this);
			}
			else
			{
				from.outputs.remove(this);
			}
		}

		public boolean equals(Object obj)
		{
			return obj instanceof FactoryConnection && ((FactoryConnection) obj).to.id == to.id && ((FactoryConnection) obj).from.id == from.id;
		}
	}

}
