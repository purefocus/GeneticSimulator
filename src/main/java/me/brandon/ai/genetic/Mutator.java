package me.brandon.ai.genetic;

import me.brandon.ai.genetic.genetypes.Gene;

public interface Mutator<T extends Gene<T>>
{

	/**
	 * function used to mutate a gene
	 */
	void mutate(T gene);

}
