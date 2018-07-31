package me.brandon.ai.ui;

import me.brandon.ai.AIMain;
import me.brandon.ai.gensim.GeneticSimulator;
import me.brandon.ai.gensim.world.Tile;
import me.brandon.ai.gensim.world.World;
import me.brandon.ai.gensim.world.WorldGenerator;
import me.brandon.ai.util.Options;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import static me.brandon.ai.gensim.world.WorldGenerator.generateTerrain;

public class WorldGenerationUI extends JToolBar
{

	private static void forceRender()
	{
		Options.forceRender = true;
		World world = GeneticSimulator.world();
		Tile[][] tile = world.getWorldTiles();
		for (int i = 0; i < tile.length; i++)
		{
			for (int j = 0; j < tile[i].length; j++)
			{
				tile[i][j].tick(0);
			}
		}

		GeneticSimulator.graphicsPanel.render();
		Options.forceRender = false;
	}

	public WorldGenerationUI()
	{

		setOrientation(JToolBar.VERTICAL);

		((JButton) add(new JButton("Clear World"))).addActionListener(e ->
		{
			WorldGenerator.generateWorld(GeneticSimulator.world());
			forceRender();
		});

		((JButton) add(new JButton("Terrain"))).addActionListener(e ->
		{
			generateTerrain();
			forceRender();
		});

		((JButton) add(new JButton("Food"))).addActionListener(e ->
		{
			WorldGenerator.generateFoodTypes();
			forceRender();
		});

		((JButton) add(new JButton("Food Level"))).addActionListener(e ->
		{
			WorldGenerator.generateFoodLevels();
			forceRender();
		});

		add(new JLabel("Water Level:"));
		((JSlider) add(new JSlider(JSlider.HORIZONTAL, 0, 1000, (int) (World.waterLevel * 1000)))).addChangeListener(e ->
		{

			World.waterLevel = ((JSlider) e.getSource()).getValue() / 1000f;
			forceRender();
		});

		add(new JLabel("Roughness"));
		((JSlider)

				add(new JSlider(JSlider.HORIZONTAL, 0, 100, 20))).

				addChangeListener(e ->
						WorldGenerator.world_generator_roughness_factor = ((JSlider) e.getSource()).

								getValue() / 10000f);


		((JButton)

				add(new JButton("Water Border"))).

				addActionListener(e ->
				{
					WorldGenerator.generateWaterBorder();
					forceRender();
				});


	}

}
