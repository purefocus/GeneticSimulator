package me.brandon.ai.api.neural.impl;

import me.brandon.ai.api.neural.Connection;
import me.brandon.ai.api.neural.Neuron;
import me.brandon.ai.api.neural.NeuronType;

public class BasicNeuron implements Neuron
{

	private static int _id;
	protected int id = _id++;

	public int layer;
	protected int index;

	protected NConnection inputs[];

	protected float value;

	protected boolean computed;

	protected NeuronType type;

	protected boolean enabled;

	public BasicNeuron()
	{
		this(NeuronType.Input);
	}

	public BasicNeuron(NeuronType type)
	{
		this(type, new NConnection[]{});
	}

	public BasicNeuron(NeuronType type, NConnection inputs[])
	{
		this.type = type;
		this.inputs = inputs;
		this.value = 0;
		this.computed = false;
		this.enabled = true;
	}

	@Override
	public float compute()
	{
		if (computed || !enabled)
			return value;

		value = 0;

		for (Connection con : inputs)
			value += con.compute();

		computed = true;

		return value;
	}

	@Override
	public float value()
	{
		return value;
	}

	@Override
	public void reset()
	{
		computed = false;
	}

	@Override
	public boolean enabled()
	{
		return enabled;
	}

	@Override
	public NeuronType type()
	{
		return type;
	}

	public NConnection[] getInputs()
	{
		return inputs;
	}

	public int getId()
	{
		return id;
	}

	public void setLayer(int layer)
	{
		this.layer = layer;
	}

	public void setIndex(int index)
	{
		this.index = index;
	}

	public void setType(NeuronType type)
	{
		this.type = type;
	}

	public void setValue(float value)
	{
		this.value = value;
	}

	public void setEnabled(boolean enabled)
	{
		if (!enabled && type != NeuronType.Hidden)
		{
			enabled = true;
		}
		this.enabled = enabled;
		for (NConnection in : inputs)
		{
			in.setEnabled(enabled && in.enabled);
		}

	}

	public void setConnections(NConnection[] inputs)
	{
		this.inputs = inputs;
	}

	public boolean isComputed()
	{
		return computed;
	}

	public int getLayer()
	{
		return layer;
	}

	public int getIndex()
	{
		return index;
	}
}
