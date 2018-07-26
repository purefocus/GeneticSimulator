package me.brandon.ai.ui;

import me.brandon.ai.gensim.GeneticSimulator;
import me.brandon.ai.gensim.world.Tile;
import me.brandon.ai.gensim.world.World;
import me.brandon.ai.gensim.world.WorldGenerator;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import static me.brandon.ai.gensim.world.WorldGenerator.generateTerrain;

public class WorldGenerationUI extends JToolBar
{

	public WorldGenerationUI()
	{

		setOrientation(JToolBar.VERTICAL);

		((JButton) add(new JButton("Clear World"))).addActionListener(e ->
				WorldGenerator.generateWorld(GeneticSimulator.world()));

		((JButton) add(new JButton("Terrain"))).addActionListener(e -> generateTerrain());

		((JButton) add(new JButton("Food"))).addActionListener(e ->
				WorldGenerator.generateFoodTypes());

		((JButton) add(new JButton("Food Level"))).addActionListener(e ->
				WorldGenerator.generateFoodLevels());

		add(new JLabel("Water Level:"));
		((JSlider) add(new JSlider(JSlider.HORIZONTAL, 0, 1000, (int) (World.waterLevel * 1000)))).addChangeListener(e ->
				World.waterLevel = ((JSlider) e.getSource()).getValue() / 1000f);

		add(new JLabel("Roughness"));
		((JSlider) add(new JSlider(JSlider.HORIZONTAL, 0, 100, 20))).addChangeListener(e ->
				WorldGenerator.world_generator_roughness_factor = ((JSlider) e.getSource()).getValue() / 10000f);


		((JButton) add(new JButton("Water Border"))).addActionListener(e ->
				WorldGenerator.generateWaterBorder());


	}

}
