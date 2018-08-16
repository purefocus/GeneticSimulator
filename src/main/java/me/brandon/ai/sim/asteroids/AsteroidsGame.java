package me.brandon.ai.sim.asteroids;

import me.brandon.ai.api.game.GameSimulator;
import me.brandon.ai.api.genetic.population.Depopulator;
import me.brandon.ai.api.genetic.population.PopUtil;
import me.brandon.ai.api.genetic.population.Populator;

import java.awt.event.KeyEvent;

public class AsteroidsGame extends GameSimulator<AsteroidsPlayer>
{

	public static final int windowWidth = 400;
	public static final int windowHeight = 400;

	public static boolean renderVision;

	public static void main(String[] args)
	{
		AsteroidsGame game = new AsteroidsGame(windowWidth * 2, windowHeight * 2, 20);
		renderVision = true;
		game.init();
		game.start();

	}

	public AsteroidsGame(int width, int height, int popSize)
	{
		super(width, height, popSize);
	}

	@Override
	public Depopulator<AsteroidsPlayer> getDepopulator()
	{
		return PopUtil.weightedFitness(3);
	}

	@Override
	public Populator<AsteroidsPlayer> getPopulator()
	{
		return PopUtil.simplePopulator(AsteroidsPlayer.class);
	}

	@Override
	public void keyPressed(KeyEvent e)
	{
		super.keyPressed(e);
		if(e.isControlDown() && e.getKeyCode() == KeyEvent.VK_V)
		{
			renderVision = !renderVision;
		}
	}
}
