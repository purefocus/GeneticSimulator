package me.brandon.ai.genetic.population;

import me.brandon.ai.genetic.GeneticEntity;

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
		sort();
		return population.get(0);
	}

	/**
	 * @return the entity with the best fitness value
	 */
	public T getBestAlive()
	{
		T ret = null;
		float max = 0;
		for (T ent : population)
		{
			if (!ent.isDead() && ent.getFitness() >= max)
			{
				ret = ent;
				max = ent.getFitness();
			}
		}
		return ret;
	}

	/**
	 * attempts to populate the population up to the max population count
	 */
	public void populate()
	{
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

	public void addCreature(T creature)
	{
		population.add(creature);
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

	double i = 0;

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


	/**
	 * object that is responsible for generating the population
	 */
	public interface Populator<T extends GeneticEntity>
	{
		void populate(Population<T> population, int amount);
	}


}
