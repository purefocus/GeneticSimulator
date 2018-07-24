package me.brandon.ai.gensim.world;

import java.util.Random;

import static me.brandon.ai.gensim.world.World.worldHeight;
import static me.brandon.ai.gensim.world.World.worldWidth;

public class WorldGenerator
{

	private static double[][] foodType = new double[worldHeight][worldWidth];
	private static double[][] terrain = new double[worldHeight][worldWidth];
	private static Random rand = new Random(10);

	public static Tile[][] generateWorld()
	{
		Tile[][] worldTiles = new Tile[worldHeight][worldWidth];


		for (int r = 0; r < worldHeight; r++)
		{
			for (int c = 0; c < worldWidth; c++)
			{
				worldTiles[r][c] = new Tile(r, c);
//				values[r][c] = rand.nextGaussian();
			}
		}

		initialise(foodType);
		initialise(terrain);


		for (int r = 0; r < worldHeight; r++)
		{
			for (int c = 0; c < worldWidth; c++)
			{
				if (terrain[r][c] > -0.1)
				{
					worldTiles[r][c].foodType = (float) foodType[r][c] + 0.5f;
					worldTiles[r][c].foodLevel = 1f;
					worldTiles[r][c].temperature = (float) terrain[r][c] + 0.2f;
				}
			}
		}

		return worldTiles;
	}

	public static void initialise(double[][] values)
	{
		int xh = values.length - 1;
		int yh = values[0].length - 1;

		// set the corner points
		values[0][0] = rand.nextFloat() - 0.5f;
		values[0][yh] = rand.nextFloat() - 0.5f;
		values[xh][0] = rand.nextFloat() - 0.5f;
		values[xh][yh] = rand.nextFloat() - 0.5f;

		// generate the fractal
		generate(values, 0, 0, xh, yh);
		generate(values, 0, 0, xh, yh);
	}


	// Add a suitable amount of random displacement to a point
	private static double roughen(double v, int l, int h)
	{
		return v + 0.005 * (float) (rand.nextGaussian() * (h - l));
	}

	// generate the fractal
	private static void generate(double[][] values, int xl, int yl, int xh, int yh)
	{
		int xm = (xl + xh) / 2;
		int ym = (yl + yh) / 2;
		if ((xl == xm) && (yl == ym)) return;

		values[xm][yl] = 0.5 * (values[xl][yl] + values[xh][yl]);
		values[xm][yh] = 0.5 * (values[xl][yh] + values[xh][yh]);
		values[xl][ym] = 0.5 * (values[xl][yl] + values[xl][yh]);
		values[xh][ym] = 0.5 * (values[xh][yl] + values[xh][yh]);

		double v = roughen(0.5 * (values[xm][yl] + values[xm][yh]), xl + yl, yh + xh);
		values[xm][ym] = v;
		values[xm][yl] = roughen(values[xm][yl], xl, xh);
		values[xm][yh] = roughen(values[xm][yh], xl, xh);
		values[xl][ym] = roughen(values[xl][ym], yl, yh);
		values[xh][ym] = roughen(values[xh][ym], yl, yh);

		generate(values, xl, yl, xm, ym);
		generate(values, xm, yl, xh, ym);
		generate(values, xl, ym, xm, yh);
		generate(values, xm, ym, xh, yh);
	}
}
