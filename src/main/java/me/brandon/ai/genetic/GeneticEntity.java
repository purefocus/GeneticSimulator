package me.brandon.ai.genetic;

public class GeneticEntity
{

	private float lastFit;
	private float fitness;
	private float deltaFit;
	private Genome dna;

	public float getFitness()
	{
		return fitness;
	}

	public Genome getDna()
	{
		return dna;
	}

	/**
	 * @return the change in fitness since the last time the function was called
	 */
	public float deltaFitness()
	{
		deltaFit = fitness - lastFit;
		lastFit = fitness;
		return deltaFit;
	}

}
