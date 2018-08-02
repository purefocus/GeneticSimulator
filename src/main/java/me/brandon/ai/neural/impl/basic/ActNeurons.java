package me.brandon.ai.neural.impl.basic;

import me.brandon.ai.neural.Connection;
import me.brandon.ai.neural.impl.NNeuron;
import me.brandon.ai.util.FastMath;

public class ActNeurons
{
	public static abstract class ActivationNeuron extends NNeuron
	{
		@Override
		public float compute()
		{
			if (computed || !enabled)
				return value;

			value = 0;

			for (Connection con : inputs)
				value += con.compute();

			computed = true;

			value = activate(value);

			return value;
		}

		abstract float activate(float value);
	}

	public static class StepNeuron extends ActivationNeuron
	{
		@Override
		float activate(float value)
		{
			return value < 0 ? 0 : 1;
		}
	}

	public static class MaxNeuron extends NNeuron
	{
		@Override
		public float compute()
		{
			if (computed || !enabled)
				return value;

			value = Float.MIN_VALUE;

			for (Connection con : inputs)
				value = Math.max(con.compute(), value);

			computed = true;

			return value;
		}
	}

	public static class MinNeuron extends NNeuron
	{
		@Override
		public float compute()
		{
			if (computed || !enabled)
				return value;

			value = Float.MAX_VALUE;

			for (Connection con : inputs)
				value = Math.min(con.compute(), value);

			computed = true;

			return value;
		}
	}

	public static class ExpNeuron extends ActivationNeuron
	{
		@Override
		float activate(float value)
		{
			return (float) Math.exp(value);
		}
	}

	public static class SigmoidNeuron extends ActivationNeuron
	{

		@Override
		float activate(float value)
		{
			return (float) (1.0f / (1.0f + FastMath.exp(-value)));
		}
	}

	public static class RampNeuron extends ActivationNeuron
	{

		@Override
		float activate(float value)
		{
			return value > 0 ? value : 0;
		}
	}
}