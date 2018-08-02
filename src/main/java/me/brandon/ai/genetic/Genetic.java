package me.brandon.ai.genetic;

import java.util.Random;

public class Genetic
{

	public static Random rand;

	public static float randVal(float max, float min)
	{
		return (rand.nextFloat() * (max - min) + min);
	}

	public static float randMax(float max)
	{
		return rand.nextFloat() * max;
	}

	public static float randVal()
	{
		return (rand.nextFloat() - 0.5f) * 2f;
	}

	public static float randVal(float mag)
	{
		return randVal() * mag;
	}

	public static boolean weightedChance(float w1, float w2)
	{
		return randMax(w1 + w2) < w1;
	}

	public static boolean chance(float chance)
	{
		return rand.nextFloat() <= chance;
	}

}
