package me.brandon.ai.neural.impl;

import me.brandon.ai.neural.Connection;
import me.brandon.ai.neural.Neuron;
import me.brandon.ai.neural.NeuronType;

public class NNeuron implements Neuron
{

	private static int _id;
	private final int id = _id++;

	protected NConnection inputs[];

	protected float value;

	protected boolean computed;

	protected NeuronType type;

	protected boolean enabled;

	public NNeuron()
	{
		this(NeuronType.Input);
	}

	public NNeuron(NeuronType type)
	{
		this(type, new NConnection[]{});
	}

	public NNeuron(NeuronType type, NConnection inputs[])
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
		this.enabled = enabled;
		for (NConnection in : inputs)
		{
			in.setEnabled(enabled);
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
}
