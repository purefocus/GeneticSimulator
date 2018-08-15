package me.brandon.ai.config;

import me.brandon.ai.evolve.world.Tile;
import me.brandon.ai.evolve.world.World;

import java.io.*;

public class WorldSave
{
	private static File saveFile = new File("./saves/world.dat");

	public static File getSaveFile()
	{
		return new File("./saves/world_" + World.worldWidth + "_" + World.worldHeight + ".dat");
	}

	public static boolean hasSaveFile()
	{
		return getSaveFile().exists();
	}

	public static void saveWorld(World world)
	{
		try
		{

			File saveFile = getSaveFile();

			Tile[][] tiles = world.getWorldTiles();

			if (saveFile.exists() || saveFile.createNewFile())
			{
				OutputStream outputStream = new FileOutputStream(saveFile);
				DataOutput out = new DataOutputStream(outputStream);

				int rows = tiles.length;
				int cols = tiles[0].length;

				out.writeInt(rows);//num cols
				out.writeInt(cols); // num rows
				out.writeFloat(World.waterLevel);

				for (int r = 0; r < rows; r++)
				{
					for (int c = 0; c < cols; c++)
					{
						Tile tile = tiles[r][c];

						out.writeFloat(tile.getTemperature());
						out.writeFloat(tile.getFoodType());
						out.writeFloat(tile.getFoodLevel());
					}
				}

				outputStream.flush();
				outputStream.close();


			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public static void loadWorld(World world)
	{

		try
		{
			File saveFile = getSaveFile();
			Tile[][] tiles;

			if (saveFile.exists() || saveFile.createNewFile())
			{
				InputStream inputStream = new FileInputStream(saveFile);
				DataInput in = new DataInputStream(inputStream);

				int rows = in.readInt();
				int cols = in.readInt();
				World.waterLevel = in.readFloat();

				tiles = new Tile[rows][cols];


				for (int r = 0; r < rows; r++)
				{
					for (int c = 0; c < cols; c++)
					{

						float temperature = in.readFloat();
						float foodType = in.readFloat();
						float foodLevel = in.readFloat();
						tiles[r][c] = new Tile(r, c, temperature, foodType, foodLevel);

					}
				}

				inputStream.close();

				world.setWorldTiles(tiles);

			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
