package me.brandon.ai.gensim;

import me.brandon.ai.config.ConfigOption;

public class ThreadManager
{


	@ConfigOption
	public static int NUM_THREADS = 2;

	private Thread[] threads;

	public ThreadManager()
	{

	}

	public void init()
	{
		threads = new Thread[NUM_THREADS - 1];
	}


	public void runParallelTask(Runnable runnable)
	{
		for (int i = 0; i < threads.length; i++)
		{
			threads[i] = new Thread(() ->
			{
				try
				{
					runnable.run();
				} catch (Exception e)
				{
					e.printStackTrace();
				}
			});
		}

		for (Thread thread : threads)
		{
			thread.start();
		}

		try
		{
			runnable.run();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}

	public void waitForAll()
	{
		for (Thread thread : threads)
		{
			if (thread != null)
			{
				try
				{
					thread.join();
				} catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
		}
	}

}
