package me.brandon.ai.api.genetic;

import me.brandon.ai.api.genetic.genetypes.Gene;
import me.brandon.ai.ui.renderer.Renderer;
import me.brandon.ai.util.GraphicsWrapper;

public abstract class GeneticEntity<T extends GeneticEntity> implements Renderer
{

	protected int rank;

	private float lastFit; // used to determine the change in fitness values
	private float deltaFit; // the amount of change in fitness values
	protected float fitness; // the fitness of the entity
	protected Gene<?> dna; // the dna of the entity
	protected boolean dead; // is the entity dead

	private boolean flagForRemoval; // should the entity be eliminated from the population

	/**
	 * @return the current fitness value of the entity
	 */
	public float getFitness()
	{
		return fitness;
	}

	/**
	 * @return is the entity dead
	 */
	public boolean isDead()
	{
		return this.dead;
	}

	/**
	 * Reset all values so that the entity could be reused
	 */
	public abstract void reset();

	/**
	 * @return ticks the entity, updating all values
	 */
	public abstract boolean tick();

	public Gene getDna()
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

	/**
	 * @param flag - should the entity be removed from the population
	 */
	public void setFlagForRemoval(boolean flag)
	{
		flagForRemoval = flag;
	}


	/**
	 * @return should the entity be removed from the population
	 */
	public boolean isFlaggedForRemoval()
	{
		return flagForRemoval;
	}

	/**
	 * Creates a new GeneticEntity based on a doCross between
	 * this entity and another parent entity
	 */
	public abstract T makeChild(T par);

	/**
	 * @return the rank of this creature compared to others (sorted index)
	 */
	public int getRank()
	{
		return rank;
	}

	public void setRank(int rank)
	{
		this.rank = rank;
	}


	public void draw(GraphicsWrapper g, int w, int h)
	{

	}

	public abstract void firstGeneration();
}
