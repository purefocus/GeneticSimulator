package me.brandon.ai.api.neural.impl;

import me.brandon.ai.api.neural.Connection;
import me.brandon.ai.api.neural.Neuron;

public class NConnection implements Connection
{

	private static int _id;
	protected int id = _id++;

	protected float weight;

	protected Neuron source;

	protected boolean enabled;

	private float value;

	public NConnection()
	{

	}

	public NConnection(Neuron source, float weight)
	{
		this.source = source;
		this.weight = weight;
		this.enabled = source.enabled();
	}

	@Override
	public float weight()
	{
		return weight;
	}

	@Override
	public <T extends Neuron> T source()
	{
		return (T) source;
	}

	@Override
	public float compute()
	{
		if (enabled)
		{
			return value = source.compute() * weight();
		}
		return value;
	}

	@Override
	public boolean enabled()
	{
		return enabled;
	}

	public int getId()
	{
		return id;
	}

	public void setEnabled(boolean enabled)
	{
		this.enabled = enabled;
	}

	public void setSource(Neuron source)
	{
		this.source = source;
	}

	public void setWeight(float weight)
	{
		this.weight = weight;
	}

	public int sourceLayer()
	{
		return ((NNeuron) source).getLayer();
	}

	public int sourceIndex()
	{
		return ((NNeuron) source).getIndex();
	}
}
