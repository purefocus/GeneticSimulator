package me.brandon.ai.genetic.genetypes;

import static me.brandon.ai.genetic.Genetic.*;

public class NumericGene extends Gene<NumericGene>
{
	protected float value;
	protected float mutAmount;
	protected float min;
	protected float max;

	public NumericGene()
	{
		this(0.1f, 0.1f, 1.0f, -1.0f);
	}

	public NumericGene(float mutRate, float mutAmount, float max, float min)
	{
		super(mutRate);
		this.mutAmount = mutAmount;
		this.max = max;
		this.min = min;
	}

	@Override
	public NumericGene cross(NumericGene other)
	{
		value = (value * dominance + other.value * other.dominance) / (dominance + other.dominance);
		return this;
	}

	@Override
	public NumericGene generateRandom()
	{
		NumericGene gene = createClone();
		gene.value = randVal(max, min);
		this.dominance = 1f;
		return gene;
	}

	@Override
	public float geneticDistance(NumericGene other)
	{
		return Math.abs(value - other.value);
	}

	@Override
	public void mutate()
	{
		value += randVal(mutAmount);
		value = Math.max(min, Math.min(max, value));
	}

	@Override
	protected void copy(NumericGene other)
	{
		this.max = other.max;
		this.min = other.min;
		this.mutAmount = other.mutAmount;
		this.value = other.value;
	}

	@Override
	public Object get()
	{
		return value;
	}

	public float getValue()
	{
		return value;
	}
}
