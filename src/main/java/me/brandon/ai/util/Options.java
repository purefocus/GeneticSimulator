package me.brandon.ai.util;

import java.util.Random;

public class Options
{

	public static final Random rand = new Random(10);

	// rendering
	public static boolean showCreatureBounds = true;
	public static boolean showCreatureFeelers = true;

	// genetics
	public static int maxFeelers = 6;
	public static int maxMemory = 4;

	public static float minSize = 5f;

	// breeding mutation rates
	public static float mut_size = 0.1f;
	public static float mut_size_amount = 0.1f;
	public static float mut_feelerCount = 0.005f;
	public static float mut_feelerLength = 0.01f;
	public static float mut_feelerAngle = 0.01f;
	public static float mut_feelerAngle_amount = 1f;

	public static float mut_amount = 0.1f;

	public static float mut_brain_neuronAddition = 0.005f;


}
