package me.brandon.ai.genetic;

import java.util.List;

public class Population<T extends GeneticEntity>
{

	private List<T> population;

	private List<Species> species;


	public List<T> getPopulation()
	{
		return population;
	}

	public List<Species> getSpecies()
	{
		return species;
	}
}
