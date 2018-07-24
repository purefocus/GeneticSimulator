package me.brandon.ai;

import me.brandon.ai.config.Configuration;
import me.brandon.ai.gensim.GeneticSimulator;
import me.brandon.ai.gensim.ThreadManager;
import me.brandon.ai.gensim.world.Tile;
import me.brandon.ai.gensim.world.World;

public class AIMain
{


	public static void main(String[] args)
	{
		new AIMain().run();

	}

	private ThreadManager threadManager;

	public AIMain()
	{
		Configuration.loadConfigurations(GeneticSimulator.class, ThreadManager.class, World.class, Tile.class);

	}


	public void run()
	{

		//TODO: Render

		threadManager.runParallelTask(() ->
		{
			//TODO: collect sensor data
		});

		threadManager.runParallelTask(() ->
		{
			//TODO: compute brain functions
		});

		threadManager.runParallelTask(() ->
		{
			//TODO: update positions
		});

		threadManager.runParallelTask(() ->
		{
			//TODO: breed
		});


	}

}
