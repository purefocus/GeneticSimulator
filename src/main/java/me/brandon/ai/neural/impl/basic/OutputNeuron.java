package me.brandon.ai.neural.impl.basic;

import me.brandon.ai.neural.Connection;
import me.brandon.ai.neural.NeuronType;
import me.brandon.ai.neural.impl.NNeuron;

public class OutputNeuron extends NNeuron
{

	public OutputNeuron()
	{
		super(NeuronType.Output, null);
	}

	@Override
	public float compute()
	{
		if (!enabled)
			return value;

		value = 0;

		for (Connection con : inputs)
			value += con.compute();

		return value;
	}
}
