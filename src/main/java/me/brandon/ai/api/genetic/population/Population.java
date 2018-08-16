package me.brandon.ai.api.genetic.population;

import me.brandon.ai.api.genetic.GeneticEntity;
import me.brandon.ai.util.Chance;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Population<T extends GeneticEntity>
{

	/**
	 * max population (or initial)
	 */
	private int populationCount;

	/**
	 * A list of the total population
	 */
	private List<T> population;

	/**
	 * object that is responsible for generating the population
	 */
	private Populator<T> populator;

	private float bestFitness;

	/**
	 * object that is responsible for determining what entities to delete
	 */
	private Depopulator<T> depopulator;

	public Population(int populationCount)
	{
		this.populationCount = populationCount;
		this.population = new ArrayList<>();
	}

	/**
	 * A list of the total population
	 */
	public List<T> getPopulation()
	{
		return population;
	}

	/**
	 * sorts the population by fitness value
	 */
	public void sort()
	{
		population.sort(Collections.reverseOrder(Comparator.comparingDouble(GeneticEntity::getFitness)));
		for (int i = 0; i < population.size(); i++)
		{
			population.get(i).setRank(i);
		}
	}

	/**
	 * @return the entity with the best fitness value
	 */
	public T getBest()
	{
		T ret = null;
		float bestFit = 0;
		for (T ent : population)
		{
			if (ent.getFitness() >= bestFit)
			{
				ret = ent;
				bestFit = ent.getFitness();
			}
		}
		if (bestFit > bestFitness)
		{
			bestFitness = bestFit;
		}
		return ret;
	}

	/**
	 * @return the entity with the best fitness value
	 */
	public T getBestAlive()
	{
		T ret = null;
		float bestFit = 0;
		for (T ent : population)
		{
			if (!ent.isDead() && ent.getFitness() >= bestFit)
			{
				ret = ent;
				bestFit = ent.getFitness();
			}
		}
		return ret;
	}

	public T getRandom(int maxIndex)
	{
		return population.get((int) Chance.randMax(maxIndex));
	}

	/**
	 * attempts to populate the population up to the max population count
	 */
	public void populate()
	{
		sort();
		if (populator != null)
		{
			populator.populate(this, populationCount - population.size());
		}
		reset();
	}

	private int aliveCount;

	public void tick()
	{
		int alive = 0;
		for (GeneticEntity ent : population)
		{
			if (ent.tick())
			{
				alive++;
			}
		}
		aliveCount = alive;
	}

	public int getSize()
	{
		return population.size();
	}

	public int getAliveCount()
	{
		return aliveCount;
	}

	public T getNextAlive()
	{
		if (aliveCount > 0)
		{
			for (T ent : population)
			{
				if (!ent.isDead())
				{
					return ent;
				}
			}
		}
		return null;
	}

	public T addCreature(T creature)
	{
		population.add(creature);
		return creature;
	}

	public T get(int index)
	{
		return population.get(index);
	}


	public void setPopulator(Populator<T> populator)
	{
		this.populator = populator;
	}

	public void setDepopulator(Depopulator<T> depopulator)
	{
		this.depopulator = depopulator;
	}

	public boolean isAllDead()
	{
		for (GeneticEntity ent : population)
		{
			if (!ent.isDead())
			{
				return false;
			}
		}
		return true;
	}

	public void kill()
	{
		if (!population.isEmpty())
		{
			if (depopulator != null)
			{
				depopulator.depopulate(this);
			}
			else
			{
				population.removeIf(c -> c.getFitness() <= 0);
				sort();

				int killCnt = (int) (population.size() / 2);
				for (int i = 0; i < killCnt; i++)
				{
					population.remove(0);
				}
			}
			population.removeIf(GeneticEntity::isFlaggedForRemoval);
		}
	}

	public void reset()
	{
		population.forEach(GeneticEntity::reset);
	}

	public float[] boxNWhiskers()
	{
		sort();
		float median = population.get(population.size() / 2).getFitness();
		float max = 0;
		float min = Float.MAX_VALUE;
		float sum = 0;
		float average;

		int medPos = 0;
		int i = 0;

		for (T ent : population)
		{
			float fit = ent.getFitness();
			sum += fit;
			max = Math.max(max, fit);
			min = Math.min(min, fit);

			if (fit == median)
			{
				medPos = i;
			}
			i++;
		}

		float lowerQ = population.get(medPos / 2).getFitness();
		float upperQ = population.get(medPos + (population.size() - medPos) / 2).getFitness();

		average = sum / population.size();

		return new float[]{average, max, upperQ, median, lowerQ, min};
	}

	public void setPopulationSize(int populationSize)
	{
		this.populationCount = populationSize;
	}


}
