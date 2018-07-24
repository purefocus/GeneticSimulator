package me.brandon.ai.gensim.world;

import me.brandon.ai.config.ConfigOption;
import me.brandon.ai.ui.Viewport;

import javax.swing.text.View;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;


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

	private List<Creature> creatures;

	private Viewport view;

	public World()
	{
		creatures = new ArrayList<>();
	}

	public void generateWorld()
	{
		worldTiles = WorldGenerator.generateWorld();
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

	public List<Creature> getCreatures()
	{
		return creatures;
	}

	public Creature getCreatureAtPosition(double px, double py)
	{
		for (Creature creature : creatures)
		{
			if (creature.atPosition(px, py))
			{
				return creature;
			}
		}
		return null;
	}


	public void draw(Graphics2D g, Viewport view)
	{
		if(worldTiles == null)
		{
			return;
		}
		this.view = view;
		g.setColor(Color.DARK_GRAY);
		g.fillRect(0, 0, view.viewWidth, view.viewHeight);

		// Only render the tiles that are visible on the screen
		int minCol = Math.max(0, view.x / tileSize);
		int minRow = Math.max(0, view.y / tileSize);
		int maxCol = Math.min((view.x + view.width) / tileSize + 1, worldWidth - 1);
		int maxRow = Math.min((view.y + view.height) / tileSize + 1, worldHeight - 1);

		Tile tile;
		for (int row = minRow; row < maxRow; row++)
		{
			for (int col = minCol; col < maxCol; col++)
			{
				tile = worldTiles[row][col];
				if (tile != null) // make sure the tile is visible
				{
					tile.draw(g, view);
				}
			}
		}
	}

	@Override
	public void tick(int time)
	{

		int minCol = Math.max(0, view.x / tileSize);
		int minRow = Math.max(0, view.y / tileSize);
		int maxCol = Math.min((view.x + view.width) / tileSize + 1, worldWidth - 1);
		int maxRow = Math.min((view.y + view.height) / tileSize + 1, worldHeight - 1);

		Tile tile;
		for (int row = minRow; row < maxRow; row++)
		{
			for (int col = minCol; col < maxCol; col++)
			{
				tile = worldTiles[row][col];
				if (tile != null) // make sure the tile is visible
				{
					tile.tick(time);
				}
			}
		}
	}
}
