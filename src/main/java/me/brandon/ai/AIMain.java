package me.brandon.ai;

import me.brandon.ai.config.Configuration;
import me.brandon.ai.config.WorldSave;
import me.brandon.ai.gensim.GeneticSimulator;
import me.brandon.ai.gensim.ThreadManager;
import me.brandon.ai.gensim.world.creature.Creature;
import me.brandon.ai.gensim.world.Tile;
import me.brandon.ai.gensim.world.World;
import me.brandon.ai.gensim.world.WorldGenerator;
import me.brandon.ai.gensim.world.creature.CreatureBrain;
import me.brandon.ai.ui.CreatureDataPanel;
import me.brandon.ai.ui.GraphicsPanel;
import me.brandon.ai.ui.SimulatorUI;

import javax.swing.*;
import java.awt.*;

public class AIMain implements Runnable
{


	public static void main(String[] args)
	{
		new AIMain();
	}

	private ThreadManager threadManager;

	public AIMain()
	{
		Configuration.loadConfigurations(GeneticSimulator.class, GraphicsPanel.class, WorldGenerator.class, ThreadManager.class, World.class, Tile.class);

		ui = new SimulatorUI();
		simulator = new GeneticSimulator();
		new Thread(this).start();


		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		JPanel conentPane = new JPanel();
		conentPane.add(graphics = new CreatureDataPanel());
		frame.setContentPane(conentPane);
		frame.pack();
		frame.setVisible(true);

		graphics.render();


	}

	private SimulatorUI ui;
	private GeneticSimulator simulator;

	public void run()
	{
		simulator.init(ui.getGraphicsPanel());

		WorldSave.loadWorld(simulator.getWorld());

		simulator.addCreature(new Creature(simulator.getWorld()));

		simulator.startSimulation();


	}

	private static CreatureDataPanel graphics;

	public static void renderCreatureData()
	{
		try
		{
			graphics.render();
		} catch (Exception e)
		{

		}
	}


}
