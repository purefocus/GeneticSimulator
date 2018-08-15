package me.brandon.ai.api.genetic.population;

import me.brandon.ai.util.Chance;
import me.brandon.ai.api.genetic.GeneticEntity;

import java.util.List;

public class Depop
{

	/**
	 * Depopulates all entites randomly
	 */
	public static Depopulator randomDepopulator()
	{
		return population ->
		{
			List<GeneticEntity<?>> pop = population.getPopulation();
			pop.forEach(ent -> ent.setFlagForRemoval(Chance.rand() < 0.5f));
		};
	}

	/**
	 * Depopulates any entities below a certain threshold of fitness
	 *
	 * @param count - amount of best seeds to keep
	 */
	public static Depopulator bestFitness(int count)
	{
		return pop ->
		{
			pop.sort();
			List<GeneticEntity<?>> population = pop.getPopulation();
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
	public static Depopulator weightedFitness(int startIndex)
	{
		return pop ->
		{
			pop.sort();
			List<GeneticEntity<?>> population = pop.getPopulation();
			float size = population.size();
			population.forEach(ent->ent.setFlagForRemoval(Chance.chance(ent.getRank() / size)));
		};
	}

}
