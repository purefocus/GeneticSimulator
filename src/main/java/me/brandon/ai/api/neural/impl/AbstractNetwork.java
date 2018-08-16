package me.brandon.ai.api.neural.impl;

import me.brandon.ai.api.neural.Network;
import me.brandon.ai.api.neural.Neuron;
import me.brandon.ai.api.neural.util.NetworkRenderer;

import java.awt.*;

public class AbstractNetwork<Neuron extends BasicNeuron> implements Network
{

	protected Neuron[][] network;
	protected float[] output;

	protected int layerCount;
	protected int inputCount;
	protected int outputCount;
	protected int outputLayer;
	protected boolean reset;

	private NetworkRenderer renderer;

	@Override
	public void setInput(float... inputs)
	{
		for (int i = 0; i < inputs.length; i++)
		{
			network[0][i].setValue(inputs[i]);
		}
	}

	@Override
	public void compute()
	{
		for (int i = 0; i < outputCount; i++)
		{
			me.brandon.ai.api.neural.Neuron neuron = network[outputLayer][i];
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
		return output;
	}

	@Override
	public float[] getValues(int layer)
	{
		float[] values = new float[network[layer].length];
		for (int i = 0; i < outputCount; i++)
		{
			me.brandon.ai.api.neural.Neuron neuron = network[layer][i];
			if (neuron.enabled())
			{
				output[i] = neuron.value();
			}
		}
		return values;
	}

	@Override
	public int getLayerCount()
	{
		return network.length;
	}


	/**
	 * @return the values of each indiviual neuron within the network
	 */
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

	/**
	 * Sets the neurons within the network
	 */
	public void setNeuronNetwork(Neuron network[][])
	{
		this.network = network;
		this.inputCount = network[0].length;
		this.outputLayer = network.length - 1;
		this.outputCount = network[outputLayer].length;

		this.output = new float[outputCount];
	}

	public Neuron[] getLayer(int layer)
	{
		return network[layer];
	}

	/**
	 * Renders the all neurons with connections and neuron values
	 *
	 * @param g      - graphics object
	 * @param width  - the width to render
	 * @param height - the height to render
	 */
	@Override
	public void renderNetwork(Graphics g, int width, int height)
	{
		if (renderer == null)
		{
			renderer = new NetworkRenderer(this);
		}

		g.drawImage(renderer.render(width, height), 0, 0, width, height, null);
	}

	/**
	 * @return the neurons within the network
	 */
	public <T extends me.brandon.ai.api.neural.Neuron> T[][] getNetwork()
	{
		return (T[][]) network;
	}


	public void forEachNeuron(NetworkIteration<BasicNeuron> iter)
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

	public void forEachLayer(NetworkIteration<BasicNeuron[]> iter)
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
