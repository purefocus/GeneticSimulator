package me.brandon.ai.evolve.world;

import me.brandon.ai.AIMain;
import me.brandon.ai.config.ConfigOption;
import me.brandon.ai.evolve.GeneticSimulator;
import me.brandon.ai.evolve.world.creature.Creature;
import me.brandon.ai.ui.Viewport;
import me.brandon.ai.util.Options;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;


public class World implements WorldEntity
{

	public static Creature selectedCreature;

	@ConfigOption(option = "world_water_level")
	public static float waterLevel = 0.5f;

	@ConfigOption(option = "world_size_width")
	public static int worldWidth = 20;

	@ConfigOption(option = "world_size_height")
	public static int worldHeight = 30;

	@ConfigOption(option = "tile_size")
	public static int tileSize = 100;


	private int time;

	private List<Tile> updating;

	private Tile[][] worldTiles;

	private final List<Creature> creatures;
	private final List<Creature> birthQueue;

	public Viewport view;

	private BufferedImage img;

	GeneticSimulator sim;

	private static World world;

	public static void setUpdating(Tile tile)
	{
		tile.updating = true;
		GeneticSimulator.world().updating.add(tile);
	}

	public World(GeneticSimulator sim)
	{
		World.world = world;
		this.sim = sim;
		creatures = new ArrayList<>();
		birthQueue = new ArrayList<>();
		updating = new ArrayList<>();

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

	public Tile getTileAt(int col, int row)
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
	}


	public void draw(Graphics2D g, Viewport view)
	{
		if (GeneticSimulator.updateRendering)
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

			synchronized (creatures)
			{
				// only render the visible creatures
				for (Creature creature : creatures)
				{
					if (creature != null && renderSection.intersects(creature.getBounds()))
						creature.draw(g, view);
				}
			}
		}
	}

	public int getTime()
	{
		return time;
	}

	public synchronized int getPopulationCount()
	{
		return creatures.size() + birthQueue.size();
	}


	@Override
	public void tick(int time)
	{

		this.time = time;

		synchronized (creatures)
		{
			creatures.addAll(birthQueue);
			birthQueue.clear();
		}

		if (view != null)
		{

			if (time == 20 || time % 15 == 0)
			{

				for (Tile tile : updating)
				{
					if (tile != null)
						tile.tick(time);
				}

				try
				{
					updating.removeIf(t -> !t.updating);
				} catch (Exception e)
				{

				}
				if (time == 20)
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

		}

		synchronized (creatures)
		{
			creatures.removeIf(c -> !c.isAlive());
		}

		if (getPopulationCount() < Options.min_population)
		{
			addCreature(new Creature(this));
		}

		if (selectedCreature == null || !selectedCreature.isAlive())
		{
			if (creatures.size() > 0)
			{
				selectedCreature = creatures.get(0);
			}
		}

		if (selectedCreature != null)
		{
			AIMain.renderCreatureData();
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
