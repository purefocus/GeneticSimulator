package me.brandon.ai.api.genetic.population;

import me.brandon.ai.api.genetic.GeneticEntity;

/**
 * object that is responsible for determining what entities to delete
 */
public interface Depopulator<T extends GeneticEntity>
{
	void depopulate(Population<T> population);


}
