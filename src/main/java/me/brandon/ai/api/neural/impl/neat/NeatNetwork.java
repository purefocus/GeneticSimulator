package me.brandon.ai.api.neural.impl.neat;

import me.brandon.ai.api.genetic.Genome;
import me.brandon.ai.api.neural.impl.AbstractNetwork;

public class NeatNetwork extends AbstractNetwork<NeatNeuron> implements Genome<NeatNetwork>
{


	public NeatNetwork()
	{

	}

	public NeatNetwork(int inputs, int outputs)
	{
		inputCount = inputs;
		outputCount = outputs;

		output = new float[outputs];
	}

	@Override
	public <M extends NeatNetwork> M makeChild(M otherParent)
	{
		return null;
	}

	@Override
	public NeatNetwork cross(NeatNetwork other)
	{
		return null;
	}

	@Override
	public NeatNetwork generateRandom()
	{
		return null;
	}

	@Override
	public void mutate()
	{

	}

	@Override
	public int getIdentifier()
	{
		return 0;
	}

	@Override
	public boolean matches(Genome<?> other)
	{
		return false;
	}

	@Override
	public void copy(NeatNetwork other)
	{

	}

	@Override
	public Object get()
	{
		return this;
	}

	@Override
	public <M extends NeatNetwork> M createClone()
	{
		M clone = null;
		try
		{
			clone = (M) getClass().newInstance();
			clone.inputCount = inputCount;
			clone.outputCount = outputCount;
			clone.layerCount = layerCount;

		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return clone;
	}
}
