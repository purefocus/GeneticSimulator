package me.brandon.ai.util;

import java.util.Random;

import static me.brandon.ai.sim.evolve.world.World.worldHeight;
import static me.brandon.ai.sim.evolve.world.World.worldWidth;

public class FractalGenerator
{

	private static Random rand = new Random(System.currentTimeMillis());

	public static float[][] generateFractal(int width, int height, float roughnessFactor)
	{

		float[][] fractal = new float[height][width];
		int xh = fractal.length - 1;
		int yh = fractal[0].length - 1;

		// set the corner points
		fractal[0][0] = rand.nextFloat() - 0.5f;
		fractal[0][yh] = rand.nextFloat() - 0.5f;
		fractal[xh][0] = rand.nextFloat() - 0.5f;
		fractal[xh][yh] = rand.nextFloat() - 0.5f;

		// generate the fractal
		generate(fractal, 0, 0, xh, yh, roughnessFactor);


		return fractal;
	}

	public static void scaleMaxMins(float[][] values)
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

		for (int r = 0; r < worldHeight; r++)
		{
			for (int c = 0; c < worldWidth; c++)
			{
				values[r][c] = (values[r][c] - min) * (max - min);
			}
		}
	}


	// Add a suitable amount of random displacement to a point
	private static float roughen(float v, int l, int h, float roughness)
	{
		return v + roughness * (float) (rand.nextGaussian() * (h - l));
	}

	// generate the fractal
	private static void generate(float[][] values, int xl, int yl, int xh, int yh, float roughness)
	{
		int xm = (xl + xh) / 2;
		int ym = (yl + yh) / 2;
		if ((xl == xm) && (yl == ym)) return;

		values[xm][yl] = 0.5f * (values[xl][yl] + values[xh][yl]);
		values[xm][yh] = 0.5f * (values[xl][yh] + values[xh][yh]);
		values[xl][ym] = 0.5f * (values[xl][yl] + values[xl][yh]);
		values[xh][ym] = 0.5f * (values[xh][yl] + values[xh][yh]);

		float v = roughen(0.5f * (values[xm][yl] + values[xm][yh]), xl + yl, yh + xh, roughness);
		values[xm][ym] = v;
		values[xm][yl] = roughen(values[xm][yl], xl, xh, roughness);
		values[xm][yh] = roughen(values[xm][yh], xl, xh, roughness);
		values[xl][ym] = roughen(values[xl][ym], yl, yh, roughness);
		values[xh][ym] = roughen(values[xh][ym], yl, yh, roughness);

		generate(values, xl, yl, xm, ym, roughness);
		generate(values, xm, yl, xh, ym, roughness);
		generate(values, xl, ym, xm, yh, roughness);
		generate(values, xm, ym, xh, yh, roughness);
	}
}
