package me.brandon.ai.gensim.world;

import me.brandon.ai.config.ConfigOption;

import java.awt.*;

public class World implements WorldEntity
{

	@ConfigOption(option = "world_size_width")
	public static int worldWidth = 200;

	@ConfigOption(option = "world_size_height")
	public static int worldHeight = 120;

	@ConfigOption(option = "tile_size")
	public static int tileSize = 100;

	public static double renderScale = 1.0D;

	private int time;

	private Tile[][] worldTiles;

	public World()
	{

	}

	public void generateWorld()
	{
		worldTiles = new Tile[worldWidth][worldHeight];
	}

	public Tile getTileAt(int row, int col)
	{

		if (row >= worldHeight || col >= worldWidth || row < 0 || col < 0)
		{
			return null;
		}
		return worldTiles[row][col];
	}

	public Tile getTileFromPosition(double px, double py)
	{
		return getTileAt((int) (px / tileSize), (int) (py / tileSize));
	}

	@Override
	public boolean isVisible(Rectangle bounds)
	{
		return true;
	}

	public void draw(Graphics2D g, Rectangle bounds)
	{
		// Only render the tiles that are visible on the screen
		int minCol = bounds.x / tileSize;
		int minRow = bounds.y / tileSize;
		int maxCol = bounds.width / tileSize;
		int maxRow = bounds.height / tileSize;

		Tile tile;
		for (int row = minRow; row < maxRow; row++)
		{
			for (int col = minCol; col < maxCol; col++)
			{
				tile = worldTiles[row][col];
				if (tile != null && tile.isVisible(bounds)) // make sure the tile is visible
				{
					tile.draw(g, bounds);
				}
			}
		}
	}

	@Override
	public void tick(int time)
	{

	}
}
