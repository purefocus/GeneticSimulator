package me.brandon.ai.api.neural.impl.basic;

import me.brandon.ai.api.neural.NeuronType;
import me.brandon.ai.api.neural.impl.BasicNeuron;

public class InputNeuron extends BasicNeuron
{

	public InputNeuron()
	{
		super(NeuronType.Input, null);
	}

	@Override
	public float compute()
	{
		return value;
	}

	public void setEnabled(boolean enabled)
	{
		this.enabled = enabled;
	}
}
