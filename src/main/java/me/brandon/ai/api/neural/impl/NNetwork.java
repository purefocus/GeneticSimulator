package me.brandon.ai.api.neural.impl;

import me.brandon.ai.api.genetic.Genome;
import me.brandon.ai.util.Chance;
import me.brandon.ai.api.genetic.genetypes.Gene;
import me.brandon.ai.api.neural.Network;
import me.brandon.ai.api.neural.NetworkFactory;
import me.brandon.ai.api.neural.Neuron;
import me.brandon.ai.api.neural.util.NetworkRenderer;

import java.awt.*;

// TODO: Add options for network training

public class NNetwork extends AbstractNetwork<BasicNeuron> implements Genome<NNetwork>
{

	private static int _id;
	private int id = _id++;

	public NNetwork()
	{

	}

	@Override
	public <M extends NNetwork> M makeChild(M otherParent)
	{
		M child = (M) createClone().cross(otherParent);
		child.mutate();
		return child;
	}

	/**
	 * Combines another network with itself
	 */
	@Override
	public NNetwork cross(NNetwork other)
	{
		for (int i = 0; i < network.length; i++)
		{
			for (int j = 0; j < network[i].length; j++)
			{
				BasicNeuron o = other.network[i][j];
				BasicNeuron n = network[i][j];

				int size = Math.min(n.inputs.length, o.inputs.length);

				for (int k = 0; k < size; k++)
				{
					NConnection i1 = o.inputs[k];
					NConnection i2 = n.inputs[k];

					if (i1.enabled != i2.enabled)
					{
						i1.enabled = Chance.chance(0.2f);
					}
					else if (!i1.enabled)
					{
						i1.enabled = Chance.chance(0.05f);
					}
					else
					{
						i1.enabled = Chance.chance(0.9f);
					}


				}
			}
		}
		mutate();
		return this;
	}

	/**
	 * randomly generates a network
	 */
	@Override
	public NNetwork generateRandom()
	{
		return this;
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
	public int getIdentifier()
	{
		return id;
	}

	@Override
	public boolean matches(Genome<?> other)
	{
		return other instanceof NNetwork;
	}

	public void copy(NNetwork other)
	{
		network = ((NNetwork) NetworkFactory.buildFromExisting(other)).getNetwork();

		inputCount = other.inputCount;
		outputCount = other.outputCount;
		outputLayer = other.outputLayer;
		output = new float[outputCount];
		id = other.id;
		reset();
	}

	@Override
	public Object get()
	{
		return this;
	}

	@Override
	public <M extends NNetwork> M createClone()
	{
		M clone = null;
		try
		{
			clone = (M) getClass().newInstance();
			clone.copy(this);
		} catch (InstantiationException e)
		{
			e.printStackTrace();
		} catch (IllegalAccessException e)
		{
			e.printStackTrace();
		}
		return clone;
	}


}
