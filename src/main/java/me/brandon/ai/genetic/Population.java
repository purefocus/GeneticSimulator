package me.brandon.ai.genetic;

import java.util.Comparator;
import java.util.List;

public class Population<T extends GeneticEntity>
{

	/**
	 * max population (or initial)
	 */
	private int populationCount;

	/**
	 * A list of the total population
	 */
	private List<T> population;

	/**
	 * A list of the species within the total population
	 */
	private List<Species> species;

	/**
	 * object that is responsible for generating the population
	 */
	private Populator<T> populator;

	/**
	 * A list of the total population
	 */
	public List<T> getPopulation()
	{
		return population;
	}

	/**
	 * A list of the species within the total population
	 */
	public List<Species> getSpecies()
	{
		return species;
	}

	/**
	 * sorts the population by fitness value
	 */
	public void sort()
	{
		population.sort(Comparator.comparingDouble(GeneticEntity::getFitness));
	}

	/**
	 * @return the entity with the best fitness value
	 */
	public GeneticEntity getBest()
	{
		return population.get(0);
	}

	/**
	 * attempts to populate the population up to the max population count
	 */
	public void populate()
	{
		if (populator != null)
		{
			populator.populate(this, populationCount - population.size());
		}
	}

	/**
	 * object that is responsible for generating the population
	 */
	public interface Populator<T extends GeneticEntity>
	{
		void populate(Population<T> population, int amount);
	}

}
