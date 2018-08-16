package me.brandon.ai.api.genetic.genetypes;

import me.brandon.ai.api.genetic.Genome;
import me.brandon.ai.api.genetic.Mutator;

import static me.brandon.ai.util.Chance.chance;

public abstract class Gene<T extends Gene<T>> implements Genome<T>, Comparable<Gene<?>>
{
	private static int _id;
	/**
	 * An identifier used to determine if two genes are of the same trait
	 */
	protected int identifier = _id;

	/**
	 * The probability of a mutation occurring
	 */
	protected float mutRate;

	/**
	 * Whether or not the gene is dormant (active or not)
	 * When one parent has it, but the other doesn't, then it could
	 * be set to dormant. Future generations could potentially
	 * get the trait again
	 */
	protected boolean dormant;

	/**
	 * the dominance of the trait.
	 * lower dominance can become dormant
	 * higher dominance make the trait carry
	 * <p>
	 * dominance is determined by how often it gets carried
	 * - initial value is random
	 * - also changes randomly between generations
	 */
	protected float dominance;

	/**
	 * Whether or not a gene is required for functionality
	 * if a gene is required, it will not be able to be set
	 * to dormant or be removed.
	 */
	protected boolean required;

	/**
	 * a mutator used to mutate a value in a more specific way
	 */
	protected Mutator<T> mutator;

	/**
	 * Can this trait doCross with any trait of the same type?
	 */
	protected boolean machAnyOfType;

	/**
	 * @param mutRate - the probability of a mutation occuring
	 */
	public Gene(float mutRate)
	{
		this(mutRate, false);
	}

	/**
	 * @param mutRate        - the probability of a mutation occuring
	 * @param matchAnyOfType - can doCross with any gene as long as they share the same type
	 */
	public Gene(float mutRate, boolean matchAnyOfType)
	{
		this(mutRate, null, matchAnyOfType);
	}

	/**
	 * @param mutRate - the probability of a mutation occuring
	 * @param mutator - uses the specified mutator for mutations
	 */
	public Gene(float mutRate, Mutator<T> mutator)
	{
		this(mutRate, mutator, false);
	}

	/**
	 * @param mutRate        - the probability of a mutation occuring
	 * @param mutator        - uses the specified mutator for mutations
	 * @param matchAnyOfType - can doCross with any gene as long as they share the same type
	 */
	public Gene(float mutRate, Mutator<T> mutator, boolean matchAnyOfType)
	{
		this.mutRate = mutRate;
		this.mutator = mutator;
		this.mutRate = mutRate;
		this.machAnyOfType = matchAnyOfType;
	}

	/**
	 * Creates a new gene that is the child between two parents
	 */
	public <M extends T> M makeChild(M otherParent)
	{
		M child = createClone();
		if (otherParent != null)
		{
			child.cross(otherParent);
		}
		attemptMutate();
		return child;
	}

	/**
	 * Creates a new gene whose value is this gene crossed with another
	 */
	public abstract T cross(T other);


	/**
	 * Generates a random value for the gene
	 */
	public abstract T generateRandom();

	/**
	 * Determine how far different this gene is from another gene
	 * (can be used to help determine if a new species has been created)
	 */
	protected float calcGeneticDistance(Gene<?> other)
	{
		return geneticDistance((T) other);
	}

	/**
	 * Determine how far different this gene is from another gene
	 * (can be used to help determine if a new species has been created)
	 */
	public abstract float geneticDistance(T other);

	/**
	 * Applies a random mutation to the value
	 */
	public abstract void mutate();

	/**
	 * Allows random mutation at a predetermined rate
	 */
	public void attemptMutate()
	{
		if (chance(mutRate)) // check to see if it should mutate
		{
			if (mutator != null) // if mutator exists, use it
			{
				mutator.mutate((T) this);
			}
			else    // if mutator does not exist, use the default method
			{
				mutate();
			}
		}
	}

	/**
	 * An identifier used to determine if two genes are of the same trait
	 */
	public int getIdentifier()
	{
		return identifier;
	}

	/**
	 * Whether or not the gene is dormant (active or not)
	 * When one parent has it, but the other doesn't, then it could
	 * be set to dormant. Future generations could potentially
	 * get the trait again
	 *
	 * @return true if the gene is dormant
	 */
	public boolean isDormant()
	{
		return dormant;
	}

	/**
	 * the dominance of the trait.
	 * lower dominance can become dormant
	 * higher dominance make the trait carry
	 * <p>
	 * dominance is determined by how often it gets carried
	 * - initial value is random
	 * - also changes randomly between generations
	 */
	public float getDominance()
	{
		return dominance;
	}

	/**
	 * Whether or not a gene is required for functionality
	 * if a gene is required, it will not be able to be set
	 * to dormant or be removed.
	 *
	 * @return true if required
	 */
	public boolean isRequired()
	{
		return required;
	}

	/**
	 * @return true if the other gene can doCross with this gene
	 */
	public boolean matches(Genome<?> other)
	{
		return identifier == other.getIdentifier() || (machAnyOfType && other.getClass().equals(getClass()));
	}

	/**
	 * Copies all the values from the other gene to this one
	 */
	protected abstract void copyData(T other);

	/**
	 * Copies all the values from the other gene to this one
	 */
	public void copy(T other)
	{
		// copy values shared by all genes
		this.mutator = other.mutator;
		this.mutRate = other.mutRate;
		this.dormant = other.dormant;
		this.required = other.required;
		this.identifier = other.identifier;
		this.dominance = other.dominance;

		// copy other values for the specific gene
		copyData(other);
	}

	/**
	 * Attempts to get the value stored in the gene
	 */
	public abstract Object get();

	/**
	 * Creates a new object that is an exact copy of the gene
	 */
	public <M extends T> M createClone()
	{
		try
		{
			Gene<T> newGene = ((T) this).getClass().newInstance();
			newGene.copy((M) this);

			return (M) newGene;

		} catch (InstantiationException | IllegalAccessException e)
		{
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * compares the identifier values of each gene (order of definition)
	 * used to sort the gene lists
	 */
	public int compareTo(Gene<?> o)
	{
		return identifier - o.identifier;
	}

}
