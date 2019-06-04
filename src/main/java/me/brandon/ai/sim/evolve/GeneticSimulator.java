package me.brandon.ai.sim.evolve;

import me.brandon.ai.AIMain;
import me.brandon.ai.ui.GraphicsPanel;
import me.brandon.ai.config.ConfigOption;
import me.brandon.ai.sim.evolve.world.creature.Creature;
import me.brandon.ai.ui.Drawable;
import me.brandon.ai.sim.evolve.world.World;
import me.brandon.ai.ui.Viewport;

import java.awt.*;
import java.util.List;
import java.util.concurrent.Semaphore;


public class GeneticSimulator implements Drawable, Runnable
{

	public static GeneticSimulator instance;

	public static World world()
	{
		return instance.world;
	}


	@ConfigOption
	public static int thread_chunk_size_min = 10;

	public static boolean updateRendering = true;

	@ConfigOption
	public static float ticksPerSecond = 30.0f;

	protected World world;

	Semaphore sem = new Semaphore(1);

	private ThreadManager threadManager;
	private Thread thread;

	private int time;

	public static GraphicsPanel graphicsPanel;

	private float actualTickRate;

	private long lastTickTime;

	private boolean running;

	public GeneticSimulator()
	{
		instance = this;
		threadManager = new ThreadManager();
		threadManager.init();
	}

	public void init(GraphicsPanel graphicsPanel)
	{
		this.graphicsPanel = graphicsPanel;

		world = new World(this);
		world.generateWorld();
	}

	public void addCreature(Creature creature)
	{
		try
		{
			sem.acquire();
			world.addCreature(creature);
			sem.release();
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}

	public void startSimulation()
	{
		if (!running)
		{
			running = true;
			thread = new Thread(this);
			thread.start();
		}

	}

	public void stopSimulation()
	{
		if (running)
		{
			running = false;
		}
	}

	private int chunkStartIndex;
	private int remainingSize;

	private interface ChunkedThreadRunner
	{
		void execute(int pos);
	}

	private void runChunkedThreads(int remainingSize, ChunkedThreadRunner threadRunner)
	{
		try
		{
			int chunkStart = 0;
			int chunkSize = 0;
			while (remainingSize > 0)
			{
				sem.acquire();
				if (remainingSize > 0)
				{
					chunkStart = chunkStartIndex;
					chunkSize = Math.max(remainingSize / (ThreadManager.NUM_THREADS * 2), thread_chunk_size_min);
					chunkSize = Math.min(remainingSize, chunkSize);

					remainingSize -= chunkSize;
				}
				sem.release();

				if (chunkSize > 0)
				{
					for (int pos = chunkStart, end = pos + chunkSize; pos < end; pos++)
					{
						threadRunner.execute(pos);
					}
				}
			}
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}

	public void run()
	{


		lastTickTime = System.currentTimeMillis();
		while (running)
		{
			long tickTime = System.currentTimeMillis();
			actualTickRate = 1000 / (tickTime - lastTickTime + 1);
			lastTickTime = tickTime;

			time++;

			graphicsPanel.render();

			List<Creature> creatures = world.getCreatures();
			int size = creatures.size();


			threadManager.runParallelTask(() -> runChunkedThreads(size, p -> creatures.get(p).updateSensors()));
			threadManager.waitForAll();

			threadManager.runParallelTask(() -> runChunkedThreads(size, p -> creatures.get(p).calculateBrain()));
			threadManager.waitForAll();

			threadManager.runParallelTask(() -> runChunkedThreads(size, p -> creatures.get(p).tick(time)));
			threadManager.waitForAll();

			world.tick(time);

			sleepDelay();
		}

	}

	private void sleepDelay()
	{
		try
		{
			long wait = (long) (1000 / ticksPerSecond);
			if (wait > 0)
			{
				Thread.sleep(wait);
			}
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void draw(Graphics2D g, Viewport view)
	{
		if (updateRendering)
		{
			graphicsPanel.draw(world);
		}

		AIMain.renderCreatureData();
	}

	public World getWorld()
	{
		return world;
	}
}
