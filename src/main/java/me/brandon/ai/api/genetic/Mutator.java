package me.brandon.ai.api.genetic;

import me.brandon.ai.api.genetic.genetypes.Gene;

public interface Mutator<T extends Gene<T>>
{

	/**
	 * function used to mutate a gene
	 */
	void mutate(T gene);

}
