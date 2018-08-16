package me.brandon.ai.api.neural.impl.neat;

import me.brandon.ai.api.neural.Neuron;
import me.brandon.ai.api.neural.NeuronType;
import me.brandon.ai.api.neural.impl.BasicNeuron;

public class NeatNeuron extends BasicNeuron
{
	@Override
	public float compute()
	{
		return 0;
	}

	@Override
	public float value()
	{
		return 0;
	}

	@Override
	public void reset()
	{

	}

	@Override
	public boolean enabled()
	{
		return false;
	}

	@Override
	public void setValue(float value)
	{

	}

	@Override
	public NeuronType type()
	{
		return null;
	}
}
