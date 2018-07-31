package me.brandon.ai.neural.impl;

import me.brandon.ai.neural.Network;
import me.brandon.ai.neural.Neuron;
import me.brandon.ai.neural.util.NetworkRenderer;

import java.awt.*;

// TODO: Add options for network training

public class NNetwork implements Network
{

	private float output[];

	private boolean reset;

	private NNeuron network[][];
	private int inputCount;
	private int outputCount;
	private int outputLayer;


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

	public Neuron[][] getNetwork()
	{
		return network;
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

}
