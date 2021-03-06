package me.brandon.ai.api.neural.impl.basic;

import me.brandon.ai.api.neural.Connection;
import me.brandon.ai.api.neural.NeuronType;
import me.brandon.ai.api.neural.impl.BasicNeuron;

public class OutputNeuron extends BasicNeuron
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
