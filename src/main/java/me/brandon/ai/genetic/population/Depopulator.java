package me.brandon.ai.genetic.population;

import me.brandon.ai.genetic.GeneticEntity;

/**
 * object that is responsible for determining what entities to delete
 */
public interface Depopulator<T extends GeneticEntity>
{
	void depopulate(Population<T> population);


}
