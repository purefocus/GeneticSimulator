package me.brandon.ai.api.neural.impl;

import me.brandon.ai.util.Chance;
import me.brandon.ai.api.genetic.genetypes.Gene;
import me.brandon.ai.api.neural.Network;
import me.brandon.ai.api.neural.NetworkFactory;
import me.brandon.ai.api.neural.Neuron;
import me.brandon.ai.api.neural.util.NetworkRenderer;

import java.awt.*;

// TODO: Add options for network training

public class NNetwork extends Gene<NNetwork> implements Network
{

	private float output[];

	private boolean reset;

	private NNeuron network[][];
	private int inputCount;
	private int outputCount;
	private int outputLayer;

	public NNetwork()
	{
		super(1.0f);
	}


	@Override
	public void setInput(float... inputs)
	{
		for (int i = 0; i < inputCount; i++)
		{
			network[0][i].setValue(inputs[i]);
		}
	}

	@Override
	public void compute()
	{
		reset = false;
		reset();
		for (int i = 0; i < outputCount; i++)
		{
			Neuron neuron = network[outputLayer][i];
			if (neuron.enabled())
			{
				output[i] = neuron.compute();
			}
		}
	}

	@Override
	public void reset()
	{
		if (!reset)
		{
			reset = true;
			for (int i = 0; i < outputLayer; i++)
			{
				for (int j = 0; j < network[i].length; j++)
				{
					network[i][j].reset();
				}
			}
		}
	}

	@Override
	public float[] getOutput()
	{
		reset();
		return output;
	}

	@Override
	public float[][] getAllValues()
	{
		float[][] output = new float[network.length][];
		for (int i = 0; i < network.length; i++)
		{
			float[] out = new float[network[i].length];
			for (int j = 0; j < network[i].length; j++)
			{
				out[j] = network[i][j].value();
			}
			output[i] = out;
		}
		return output;
	}

	public void setNeuronNetwork(NNeuron network[][])
	{
		this.network = network;
		this.inputCount = network[0].length;
		this.outputLayer = network.length - 1;
		this.outputCount = network[outputLayer].length;

		this.output = new float[outputCount];
	}

	public <T extends Neuron> T[][] getNetwork()
	{
		return (T[][]) network;
	}

	public int getLayerCount()
	{
		return network.length;
	}

	public NNeuron[] getLayer(int layer)
	{
		return network[layer];
	}

	public float[] getValues(int layer)
	{
		float vals[] = new float[network[layer].length];
		for (int i = 0; i < vals.length; i++)
		{
			vals[i] = network[layer][i].value();
		}
		return vals;
	}

	private NetworkRenderer renderer;

	@Override
	public void renderNetwork(Graphics g, int width, int height)
	{
		if (renderer == null)
		{
			renderer = new NetworkRenderer(this);
		}

		g.drawImage(renderer.render(width, height), 0, 0, width, height, null);

	}

	@Override
	public NNetwork cross(NNetwork other)
	{
		for (int i = 0; i < network.length; i++)
		{
			for (int j = 0; j < network[i].length; j++)
			{
				NNeuron o = other.network[i][j];
				NNeuron n = network[i][j];
				for (int k = 0; k < n.inputs.length; k++)
				{
					NConnection i1 = o.inputs[k];
					NConnection i2 = n.inputs[k];

					if (dominance == other.dominance)
					{
						i2.weight = (i2.weight + i1.weight) / 2;
					}
					else
					{
						i2.weight = (i2.weight * dominance + i1.weight * other.dominance) / (dominance + other.dominance);
					}

				}
			}
		}
		attemptMutate();
		return this;
	}

	@Override
	public NNetwork generateRandom()
	{
		return null;
	}

	@Override
	public float geneticDistance(NNetwork other)
	{
		return 0;
	}

	@Override
	public void mutate()
	{
		for (int i = 0; i < network.length; i++)
		{
			for (int j = 0; j < network[i].length; j++)
			{
				for (int k = 0; k < network[i][j].inputs.length; k++)
				{
					network[i][j].inputs[k].weight += Chance.randVal(0.1f);
					if (Chance.rand() < 0.25)
					{
						network[i][j].inputs[k].weight += Chance.randVal(0.5f);
					}
				}
			}
		}
	}

	@Override
	protected void copy(NNetwork other)
	{
		network = ((NNetwork) NetworkFactory.buildFromExisting(other)).getNetwork();

		inputCount = other.inputCount;
		outputCount = other.outputCount;
		outputLayer = other.outputLayer;
		output = new float[outputCount];
		reset();

	}

	@Override
	public Object get()
	{
		return this;
	}

	public void forEachNeuron(NetworkIteration<NNeuron> iter)
	{
		for (int layer = 0; layer < network.length; layer++)
		{
			int layerSize = network[layer].length;
			for (int index = 0; index < layerSize; index++)
			{
				iter.item(layer, index, network[layer][index]);
			}
		}
	}

	public void forEachLayer(NetworkIteration<NNeuron[]> iter)
	{
		for (int layer = 0; layer < network.length; layer++)
		{
			iter.item(layer, 0, network[layer]);
		}
	}

	public interface NetworkIteration<T>
	{
		void item(int layer, int index, T neuron);
	}


}
