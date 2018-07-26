package me.brandon.ai.gensim.world;

import me.brandon.ai.config.ConfigOption;
import me.brandon.ai.gensim.GeneticSimulator;
import me.brandon.ai.gensim.world.creature.Creature;
import me.brandon.ai.ui.Viewport;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;


public class World implements WorldEntity
{
	@ConfigOption(option = "world_water_level")
	public static float waterLevel = 0.5f;

	@ConfigOption(option = "world_size_width")
	public static int worldWidth = 200;

	@ConfigOption(option = "world_size_height")
	public static int worldHeight = 120;

	@ConfigOption(option = "tile_size")
	public static int tileSize = 100;


	private int time;

	private Tile[][] worldTiles;

	private final List<Creature> creatures;
	private final Queue<Creature> birthQueue;

	private Viewport view;

	private BufferedImage img;

	GeneticSimulator sim;

	public World(GeneticSimulator sim)
	{
		this.sim = sim;
		creatures = new ArrayList<>();
		birthQueue = new LinkedBlockingQueue<>();

		img = new BufferedImage(worldWidth * tileSize, worldHeight * tileSize, BufferedImage.TYPE_INT_RGB);
	}

	public void generateWorld()
	{
		worldTiles = WorldGenerator.generateWorld();
		WorldGenerator.generateTerrain();
	}

	public Tile[][] getWorldTiles()
	{
		return worldTiles;
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

	public synchronized void addCreature(Creature creature)
	{
		birthQueue.add(creature);
		creature.birth(null);
	}


	public void draw(Graphics2D g, Viewport view)
	{
		if (worldTiles == null)
		{
			return;
		}

		this.view = view;
		g.setColor(Color.DARK_GRAY);
		g.fillRect(0, 0, view.viewWidth, view.viewHeight);

		// Only render the tiles that are visible on the screen
		int minCol = Math.max(0, (int) view.x / tileSize);
		int minRow = Math.max(0, (int) view.y / tileSize);
		int maxCol = Math.min((int) (view.x + view.width) / tileSize + 1, worldWidth);
		int maxRow = Math.min((int) (view.y + view.height) / tileSize + 1, worldHeight);


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

		Rectangle renderSection = new Rectangle(minCol * tileSize, minRow * tileSize,
				(maxCol - minCol) * tileSize, (maxRow - minRow) * tileSize);

		// only render the visible creatures
		for (Creature creature : creatures)
		{
			if (creature != null && renderSection.intersects(creature.getBounds()))
				creature.draw(g, view);
		}
	}

	public int getTime()
	{
		return time;
	}

	@Override
	public void tick(int time)
	{

		this.time = time;

		creatures.addAll(birthQueue);
		birthQueue.clear();

		if (view != null)
		{
			int minCol = Math.max(0, (int) view.x / tileSize);
			int minRow = Math.max(0, (int) view.y / tileSize);
			int maxCol = Math.min((int) (view.width - view.x) / tileSize + 1, worldWidth);
			int maxRow = Math.min((int) (view.height - view.y) / tileSize + 1, worldHeight);


			// only tick the visible tiles
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

	public void setWorldTiles(Tile[][] worldTiles)
	{
		this.worldTiles = worldTiles;
	}

	public Color getColorAt(double x, double y)
	{
		Tile tile = getTileFromPosition(x, y);
		if (tile != null)
			return tile.tileColor;
		return Color.BLACK;
	}
}
