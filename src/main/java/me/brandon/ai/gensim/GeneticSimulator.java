package me.brandon.ai.gensim;

import me.brandon.ai.GraphicsPanel;
import me.brandon.ai.config.ConfigOption;
import me.brandon.ai.gensim.world.Drawable;
import me.brandon.ai.gensim.world.World;

import java.awt.*;

public class GeneticSimulator implements Drawable
{

	private static boolean updateRendering = true;

	@ConfigOption
	public static float tickRate = 1.0f;

	protected World world;

	private ThreadManager threadManager;

	private int time;

	private GraphicsPanel graphicsPanel;

	public GeneticSimulator()
	{
		threadManager = new ThreadManager();
		threadManager.init();
	}

	public void run()
	{
		if (updateRendering)
		{
			graphicsPanel.draw(world);
		}

		threadManager.runParallelTask(() ->
		{
			//TODO: collect sensor data
		});
		threadManager.waitForAll();
		threadManager.runParallelTask(() ->
		{
			//TODO: compute brain functions
		});
		threadManager.waitForAll();
		threadManager.runParallelTask(() ->
		{
			//TODO: update positions
		});
		threadManager.waitForAll();
		threadManager.runParallelTask(() ->
		{
			//TODO: breed
		});
		threadManager.waitForAll();


	}

	@Override
	public boolean isVisible(Rectangle bounds)
	{
		return true;
	}

	@Override
	public void draw(Graphics2D g, Rectangle bounds)
	{

	}
}
