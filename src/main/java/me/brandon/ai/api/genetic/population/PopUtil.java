package me.brandon.ai.api.genetic.population;

import me.brandon.ai.sim.asteroids.AsteroidsPlayer;
import me.brandon.ai.util.Chance;
import me.brandon.ai.api.genetic.GeneticEntity;

import java.util.List;

public final class PopUtil
{
	private PopUtil()
	{

	}

	/**
	 * Depopulates all entites randomly
	 */
	public static <T extends GeneticEntity> Depopulator<T> randomDepopulator()
	{
		return population ->
		{
			List<T> pop = population.getPopulation();
			pop.forEach(ent -> ent.setFlagForRemoval(Chance.rand() < 0.5f));
		};
	}

	/**
	 * Depopulates any entities below a certain threshold of fitness
	 *
	 * @param count - amount of best seeds to keep
	 */
	public static <T extends GeneticEntity> Depopulator<T> bestFitness(int count)
	{
		return pop ->
		{
			pop.sort();
			List<T> population = pop.getPopulation();
			for (int i = count - 1; i < population.size(); i++)
			{
				population.get(i).setFlagForRemoval(true);
			}
		};
	}


	/**
	 * Randomly depopulates entities using the rank as the probability for removal
	 *
	 * @param startIndex - amount of best seeds to keep
	 */
	public static <T extends GeneticEntity> Depopulator<T> weightedFitness(int startIndex)
	{
		return pop ->
		{
			pop.sort();
			List<T> population = pop.getPopulation();
			float size = population.size();
			population.forEach(ent -> ent.setFlagForRemoval(Chance.chance(ent.getRank() / size)));
		};
	}

	public static <T extends GeneticEntity> Populator<T> simplePopulator(Class<T> clz)
	{
		return (pop, cnt) ->
		{
			// if no population exists, populate with random entries
			if (pop.getPopulation().isEmpty())
			{
				for (int i = 0; i < cnt; i++)
				{
					try
					{
						pop.addCreature(clz.newInstance()).firstGeneration();
					} catch (InstantiationException | IllegalAccessException e)
					{
						e.printStackTrace();
					}
				}
			}
			else // otherwise start doCross breeding
			{
				T best = pop.getBest();
				T par;
				int startSize = pop.getSize();
				for (int i = 0; i < cnt; i++)
				{
					par = pop.getRandom(startSize);
					pop.addCreature((T) best.makeChild(par));
				}
			}
		};

	}

}
