package me.brandon.ai.gensim.world.creature;

import me.brandon.ai.gensim.GeneticSimulator;

import static me.brandon.ai.util.Options.*;

public class GeneticBreeder
{


	public static Creature breed(Creature parent)
	{
		return breed(parent, parent);
	}

	public static Creature breed(Creature p1, Creature p2)
	{
		if (p2 == null)
			p2 = p1;

		int gen =  Math.max(p1.generation, p2.generation) + 1;
		Creature creature = new Creature(GeneticSimulator.world(), gen);

		creature.generation = gen;
		creature.size = mutate(cross(p1.size, p2.size), mut_size, mut_size_amount);
		creature.maxSize = mutate(cross(p1.maxSize, p2.maxSize), mut_size, mut_size_amount);
		creature.maxSpeed = mutate(cross(p1.maxSpeed, p2.maxSpeed), mut_speed, mut_speed_amount);

		creature.foodType = mutate(cross(p1.foodType, p2.foodType), mut_speed, mut_speed_amount);
		creature.foodTypeRange = mutate(cross(p1.foodTypeRange, p2.foodTypeRange), mut_speed, mut_speed_amount);

		creature.speciesHue = (p1.speciesHue + p2.speciesHue) / 2;

		creature.numFeelers = Math.max(p1.feelers.length, p2.feelers.length);
//		creature.numFeelers = (int) Math.min(maxFeelers, mutate(creature.numFeelers, mut_feelerCount, 2));
//		creature.numFeelers = Math.max(1, Math.min(maxFeelers, creature.numFeelers));
		creature.initFeelers();

		for (int i = 0; i < creature.numFeelers; i++)
		{
			if (p1.numFeelers >= i && p2.numFeelers >= i)
			{
				creature.feelers[i].length = cm(p1.feelers[i].length, p2.feelers[i].length, mut_feelerLength, mut_amount);
				creature.feelers[i].angle = cm(p1.feelers[i].angle, p2.feelers[i].angle, mut_feelerLength, mut_feelerAngle_amount);
			}
			else if (p1.numFeelers >= i)
			{
				creature.feelers[i].length = mutate(p1.feelers[i].length, mut_feelerLength, mut_amount);
				creature.feelers[i].angle = mutate(p1.feelers[i].angle, mut_feelerLength, mut_feelerAngle_amount);
			}
			else if (p2.numFeelers >= i)
			{
				creature.feelers[i].length = mutate(p2.feelers[i].length, mut_feelerLength, mut_amount);
				creature.feelers[i].angle = mutate(p2.feelers[i].angle, mut_feelerLength, mut_feelerAngle_amount);
			}
			else
			{
				creature.feelers[i].length = rand.nextFloat() * creature.size * mut_amount;
				creature.feelers[i].angle = rand.nextFloat() * 360;
			}
		}

		BrainFactory factory = BrainFactory.factory();
		factory.createFrom(p1.brain, p2.brain);
		factory.mutate();
		creature.brain = factory.generate(creature);


		creature.pos = p1.pos.clone();

		creature.fin();

		return creature;
	}

	public static float cm(float v1, float v2, float rate, float amount)
	{
		return mutate(cross(v1, v2), rate, amount);
	}

	public static float mutate(float value, float rate, float amount)
	{
		if (rand.nextFloat() < rate)
		{
			return (rand.nextFloat() - 0.5f) * amount;
		}
		return value;
	}

	public static float cross(float v1, float v2)
	{
		if (v1 != v2)
		{
			float ratio = rand.nextFloat();
			return ratio * v1 + (1 - ratio) * v2;
		}

		return v1;
	}

	public class BreedingEntry
	{
		Creature parent1;
		Creature parent2;

		public BreedingEntry(Creature parent1, Creature parent2)
		{
			this.parent1 = parent1;
			this.parent2 = parent2;
		}

		public Creature createChild()
		{
			return breed(parent1, parent2);
		}
	}

}
