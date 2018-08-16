package me.brandon.ai.api.genetic.population;

import me.brandon.ai.api.genetic.GeneticEntity;

/**
 * object that is responsible for generating the population
 */
public interface Populator<T extends GeneticEntity>
{
	void populate(Population<T> population, int amount);
}
