package me.brandon.ai.gensim.world;

import me.brandon.ai.config.ConfigOption;
import me.brandon.ai.ui.Viewport;

import java.awt.*;

public class Creature implements WorldEntity
{

	@ConfigOption(option = "creature_min_size")
	public static int minSize = 30;


	protected float remainingLife;

	protected float size;

	protected float energy;
	protected float consumeRate;

	protected float needleExtension;

	protected boolean alive;

	public void updateSensors()
	{

	}

	public void calculateBrain()
	{

	}


	public boolean atPosition(double px, double py)
	{
		return false;
	}

	@Override
	public void tick(int time)
	{

	}

	@Override
	public void draw(Graphics2D g, Viewport view)
	{

	}
}
