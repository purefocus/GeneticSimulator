package me.brandon.ai.sim.balance;

import me.brandon.ai.api.genetic.population.PopUtil;
import me.brandon.ai.api.genetic.population.Population;
import me.brandon.ai.ui.RenderPanel_old;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;


public class BalanceCreatureSimulator implements RenderPanel_old.Renderer, Runnable, KeyListener
{

	public RenderPanel_old renderer;

	public BalanceCreature focusedCreature;
	Population<BalanceCreature> population;

	int generation = 0;

	float[] graphData = null;

	public static void main(String[] args)
	{
		new BalanceCreatureSimulator();
	}

	public BalanceCreatureSimulator()
	{
		renderer = new RenderPanel_old(500, 500);
		renderer.makeFrame();
		renderer.addKeyListener(this);

		population = new Population<>(100);
		population.kill();
		population.setPopulator((pop, count) ->
		{
			pop.sort();
			List<BalanceCreature> p = pop.getPopulation();

			int cur = p.size();
			if (cur > 1)
			{
				BalanceCreature best = pop.getBest();
				count--;
				p.add(best);
//				int s = (int) (count / 1.2);
				for (int i = 0; i < count; i++)
				{
					if (cur - i - 1 < 0)
					{
						best = p.get((int) (Math.random() * cur));
					}
					else
					{
						BalanceCreature p2 = p.get(cur - i - 1);
						BalanceCreature n = best.makeChild(p2);
						p.add(n);
					}
				}
			}
			cur = p.size();
			for (int i = cur; i < count; i++)
			{
				BalanceCreature n = new BalanceCreature();
				n.initNetwork();
				p.add(n);
			}


		});

		population.setDepopulator(PopUtil.weightedFitness(3));

		population.populate();

		focusedCreature = population.getBest();

		new Thread(this).start();
	}


	@Override
	public void draw(Graphics g, int width, int height)
	{
		g.setColor(Color.DARK_GRAY);
		g.fillRect(0, 0, width, height);

		if (focusedCreature != null)
		{
			focusedCreature.draw(g);


			if (focusedCreature.isDead())
			{
				int pad = 2;
				g.setColor(Color.RED);
				g.drawRect(pad, pad, width - pad * 2, height - pad * 2);
			}
		}

		g.setColor(Color.WHITE);
		g.drawString(String.format("Creatures Remaining: %d", population.getAliveCount()), 10, 475);
		g.drawString(String.format("Generation: %d", generation), 10, 490);


		if (graphData != null)
		{
			int posY = 450;
			int posMax = 20;
			int posMin = 480;
			int posX = posMax;

			float maxV = graphData[1];
			float minV = graphData[5];
			g.drawLine(posMax, posY, posMin, posY);
			//	graphData = {average, max, upperQ, median, lowerQ, min};

			g.drawLine(posX, posY - 10, posX, posY + 10);
			g.drawString(String.format("%2.0f", maxV), posX - 5, posY - 10);
			posX = posMin;
			g.drawLine(posX, posY - 10, posX, posY + 10);
			g.drawString(String.format("%2.0f", minV), posX - 5, posY - 10);

			// median
			g.setColor(Color.RED);
			posX = (int) scale(graphData[3], maxV, minV, (float) posMax, (float) posMin);
			g.drawLine(posX, posY - 10, posX, posY + 10);

			// quartiles
			g.setColor(Color.ORANGE);
			posX = (int) scale(graphData[2], maxV, minV, (float) posMax, (float) posMin);
			g.drawLine(posX, posY - 10, posX, posY + 10);
			posX = (int) scale(graphData[4], maxV, minV, (float) posMax, (float) posMin);
			g.drawLine(posX, posY - 10, posX, posY + 10);

			// average
			g.setColor(Color.GREEN);
			posX = (int) scale(graphData[0], maxV, minV, (float) posMax, (float) posMin);
			g.drawLine(posX, posY - 10, posX, posY + 10);


		}

	}

	private float scale(float v, float cmax, float cmin, float nmax, float nmin)
	{
		return (v - cmin) / (cmax - cmin) * (nmax - nmin) + nmin;
	}


	@Override
	public void run()
	{
		while (true)
		{
			if (render)
			{
				try
				{
					Thread.sleep(10);
				} catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}

			if (focusedCreature == null || focusedCreature.isDead())
			{
				focusedCreature = population.getBestAlive();
			}

			if (focusedCreature == null)
			{
				onNewGeneration();
			}

			population.tick();

			if (render && cont)
				renderer.render(this);
		}
	}

	private void onNewGeneration()
	{
		graphData = population.boxNWhiskers();
		focusedCreature = population.getBest();
		population.kill();
		population.populate();
		generation++;
		render = cont;


		population.reset();

		population.tick();

		if (!cont)
			renderer.render(this);
	}

	@Override
	public void keyTyped(KeyEvent e)
	{

	}

	boolean render = true;
	boolean cont = true;

	@Override
	public void keyPressed(KeyEvent e)
	{
		if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_F)
		{
			render = !render;
		}
		if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_D)
		{
			cont = !cont;
			render = cont;
		}
	}

	@Override
	public void keyReleased(KeyEvent e)
	{

	}
}
