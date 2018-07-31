package me.brandon.ai.neural.impl;

import me.brandon.ai.neural.Connection;
import me.brandon.ai.neural.Neuron;

public class NConnection implements Connection
{

	private static int _id;
	private final int id = _id++;

	private float weight;

	private Neuron source;

	private boolean enabled;

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
	public Neuron source()
	{
		return source;
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

	void setEnabled(boolean enabled)
	{
		this.enabled = enabled;
	}

	void setSource(Neuron source)
	{
		this.source = source;
	}

	void setWeight(float weight)
	{
		this.weight = weight;
	}
}
