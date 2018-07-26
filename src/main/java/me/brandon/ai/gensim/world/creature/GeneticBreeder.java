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
		Creature creature = new Creature(GeneticSimulator.world());
		if (p2 == null)
			p2 = p1;

		creature.size = mutate(cross(p1.size, p2.size), mut_size, mut_size_amount);
		int feelerCount = Math.max(p1.feelers.length, p2.feelers.length);
		feelerCount = (int) Math.min(maxFeelers, mutate(feelerCount, mut_feelerCount, 2));
		creature.feelers = new Creature.FeatureExtension[feelerCount];
		creature.initFeelers();

		for (int i = 0; i < feelerCount; i++)
		{
			if (p1.feelers.length >= i && p2.feelers.length >= i)
			{
				creature.feelers[i].length = cm(p1.feelers[i].length, p2.feelers[i].length, mut_feelerLength, mut_amount);
				creature.feelers[i].angle = cm(p1.feelers[i].angle, p2.feelers[i].angle, mut_feelerLength, mut_feelerAngle_amount);
			}
//			creature.feelers[i].
		}

		return null;
	}

	private static float cm(float v1, float v2, float rate, float amount)
	{
		return mutate(cross(v1, v2), rate, amount);
	}

	private static float mutate(float value, float rate, float amount)
	{
		if (rand.nextFloat() < rate)
		{
			return (rand.nextFloat() - 0.5f) * amount;
		}
		return value;
	}

	private static float cross(float v1, float v2)
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
