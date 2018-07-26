package me.brandon.ai.gensim.world;

import me.brandon.ai.config.ConfigOption;
import me.brandon.ai.gensim.GeneticSimulator;
import me.brandon.ai.util.FractalGenerator;

import java.util.Random;

import static me.brandon.ai.gensim.world.World.worldHeight;
import static me.brandon.ai.gensim.world.World.worldWidth;

public class WorldGenerator
{
	@ConfigOption
	public static float world_generator_roughness_factor = 0.002f;

	private static Random rand = new Random(10);

	public static Tile[][] generateWorld()
	{
		Tile[][] worldTiles = new Tile[worldHeight][worldWidth];


		for (int r = 0; r < worldHeight; r++)
		{
			for (int c = 0; c < worldWidth; c++)
			{
				worldTiles[r][c] = new Tile(r, c);
			}
		}

		return worldTiles;
	}

	public static void generateWorld(World world)
	{
		world.setWorldTiles(generateWorld());
	}

	public static void generateTerrain()
	{
		World world = GeneticSimulator.world();
		float[][] terrain = FractalGenerator.generateFractal(worldWidth, worldHeight, world_generator_roughness_factor);
		Tile[][] worldTiles = world.getWorldTiles();
		scaleMaxMins(terrain);
		for (int r = 0; r < worldHeight; r++)
		{
			for (int c = 0; c < worldWidth; c++)
			{
				worldTiles[r][c].temperature = terrain[r][c];
			}
		}

	}

	public static void generateFoodTypes()
	{
		World world = GeneticSimulator.world();
		float[][] foodTypes = FractalGenerator.generateFractal(worldWidth, worldHeight, world_generator_roughness_factor);
		Tile[][] worldTiles = world.getWorldTiles();
//		scaleMaxMins(foodTypes);
		for (int r = 0; r < worldHeight; r++)
		{
			for (int c = 0; c < worldWidth; c++)
			{
				worldTiles[r][c].foodType = Math.max(-1, Math.min(1f, foodTypes[r][c]));
			}
		}

	}

	public static void scaleMaxMins(float[][] values)
	{
		scaleMaxMins(values, 1f, 0);
	}

	public static void scaleMaxMins(float[][] values, float newMax, float newMin)
	{
		float min = Float.MAX_VALUE;
		float max = Float.MIN_VALUE;

		for (int r = 0; r < worldHeight; r++)
		{
			for (int c = 0; c < worldWidth; c++)
			{
				min = Math.min(min, values[r][c]);
				max = Math.max(max, values[r][c]);
			}
		}

		float range = max - min;
		float newRange = newMax - newMin;


		for (int r = 0; r < worldHeight; r++)
		{
			for (int c = 0; c < worldWidth; c++)
			{
				values[r][c] = (values[r][c] - min) / range * newRange + newMin;
			}
		}
	}

	public static void generateFoodLevels()
	{
		World world = GeneticSimulator.world();
		float[][] foodTypes = FractalGenerator.generateFractal(worldWidth, worldHeight, world_generator_roughness_factor);
		Tile[][] worldTiles = world.getWorldTiles();
		scaleMaxMins(foodTypes, 0.25f, 1.0f);
		for (int r = 0; r < worldHeight; r++)
		{
			for (int c = 0; c < worldWidth; c++)
			{
				worldTiles[r][c].foodLevel = Math.max(0.25f, Math.min(1f, foodTypes[r][c]));
			}
		}
	}

	public static void generateWaterBorder()
	{
		Tile[][] tiles = GeneticSimulator.world().getWorldTiles();

		for (int r = 0; r < worldHeight; r++)
		{
			tiles[r][0].temperature = tiles[r][worldWidth - 1].temperature = -1f;
		}
		for (int c = 0; c < worldWidth; c++)
		{
			tiles[0][c].temperature = tiles[worldHeight - 1][c].temperature = -1f;
		}

	}
}
