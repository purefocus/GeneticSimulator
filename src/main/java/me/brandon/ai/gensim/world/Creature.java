package me.brandon.ai.gensim.world;

import me.brandon.ai.config.ConfigOption;

public class Creature
{

	@ConfigOption(option = "creature_min_size")
	public static int minSize = 30;


	protected float remainingLife;

	protected float size;

	protected float energy;
	protected float consumeRate;

	protected float needleExtension;


}
