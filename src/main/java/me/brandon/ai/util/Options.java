package me.brandon.ai.util;

import java.util.Random;

public class Options
{

	public static final Random rand = new Random(10);

	public static int min_population = 3;

	public static boolean forceRender = false;

	// rendering
	public static boolean showCreatureBounds = false;
	public static boolean showCreatureFeelers = true;

	// focusedCreature life
	public static float rod_neg_energy = 5.0f;
	public static float size_energy_consumption = 0.0012f;
	public static float rod_energy = 0.005f;
	public static float impulse_energy_consumption = 0.005f;

	// genetics
	public static int maxFeelers = 6;
	public static int maxMemory = 4;

	public static float minSize = 5f;

	public static int inputNodeSize = maxMemory
			+ 3 * maxFeelers // feelers
			+ 1 // age
			+ 1 // energy
			+ 1; // constant

	public static int outputNodeSize = maxMemory
			+ 1 // needle length
			+ 2 // main feeler length and angle
			+ 2 // movement
			+ 1 // want to breed
			;

	// breeding mutation rates
	public static float mut_size = 0.1f;
	public static float mut_size_amount = 0.1f;

	public static float mut_feelerCount = 0.15f;
	public static float mut_feelerLength = 0.20f;
	public static float mut_feelerAngle = 0.20f;
	public static float mut_feelerAngle_amount = 2f;

	public static float mut_speed = 0.1f;
	public static float mut_speed_amount = 0.02f;


	public static float mut_speciesHue = 0.05f;
	public static float mut_speciesHue_amnt = 0.04f;

	public static float mut_food = 0.1f;
	public static float mut_food_amount = 0.02f;

	public static float mut_amount = 0.1f;

	public static float mut_brain_connectionSplit = 0.05f;
	public static float mut_brain_connection_removal = 0.3f;
	public static float mut_brain_connection_addition = 0.3f;

	public static float mut_brain_wight_amnt = 0.005f;

	public static float brain_weight_init_mag = 1f;


}
