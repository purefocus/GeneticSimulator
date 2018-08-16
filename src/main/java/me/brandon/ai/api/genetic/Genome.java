package me.brandon.ai.api.genetic;

import me.brandon.ai.api.genetic.genetypes.Gene;

public interface Genome<T extends Genome<T>>
{

	/**
	 * Creates a new gene that is the child between two parents
	 */
	<M extends T> M makeChild(M otherParent);

	/**
	 * Creates a new gene whose value is this gene crossed with another
	 */
	T cross(T other);

	/**
	 * Generates a random value for the gene
	 */
	T generateRandom();

	/**
	 * Applies a random mutation to the value
	 */
	void mutate();

	/**
	 * An identifier used to determine if two genes are of the same trait
	 */
	int getIdentifier();

	/**
	 * @return true if the other gene can doCross with this gene
	 */
	boolean matches(Genome<?> other);

	/**
	 * Copies all the values from the other gene to this one
	 */
	void copy(T other);

	/**
	 * Attempts to get the value stored in the gene
	 */
	Object get();

	/**
	 * Creates a new object that is an exact copy of the gene
	 */
	<M extends T> M createClone();

}
