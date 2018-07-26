package me.brandon.ai.gensim.world;

import me.brandon.ai.config.ConfigOption;
import me.brandon.ai.ui.Drawable;
import me.brandon.ai.ui.Viewport;

import static me.brandon.ai.gensim.world.World.*;

import java.awt.*;

public class Tile implements Drawable
{
	@ConfigOption
	public static float tile_maxFood = 100f;


	public static double borderSize = 0;

	protected Rectangle bounds;

	protected int tileX;
	protected int tileY;

	protected float foodLevel = 0.5f; // brightness
	protected float temperature; // saturation
	protected float foodType; // hue

	protected float growthRate;
	protected float maxGrowth;

	protected Color foodColor;
	protected Color tileColor = Color.BLACK;
	protected Color borderColor = Color.WHITE;

	protected double genValue;

	public Tile(int row, int col)
	{
		tileX = col;
		tileY = row;

		maxGrowth = tile_maxFood;

		bounds = new Rectangle(World.tileSize * col, World.tileSize * row, World.tileSize, World.tileSize);
	}

	public Tile(int row, int col, float temperature, float foodType, float foodLevel)
	{
		this(row, col);
		this.temperature = temperature;
		this.foodLevel = foodLevel;
		this.foodType = foodType;
	}

	public void tick(int time)
	{
		if (foodLevel < maxGrowth)
		{
			foodLevel += growthRate;
		}

		tileColor = Color.getHSBColor(
				foodType,
				temperature >= waterLevel ? Math.max(foodLevel, 0) : 0,
				temperature >= waterLevel ? temperature : 0
		);

		borderColor = Color.getHSBColor(
				0,
				(float) 0,
				temperature > waterLevel ? 0.1f : 0
		);

//		borderColor = new Color(borderColor.getRed(), borderColor.getGreen(), borderColor.getBlue(), 100);
	}

	public synchronized float consume(float request)
	{
		float consumed = Math.max(request, foodLevel);
		foodLevel -= consumed;

		return consumed;
	}

	@Override
	public void draw(Graphics2D g, Viewport view)
	{

		Rectangle region = new Rectangle();
		region.x = (int) ((bounds.x - view.x) / view.PxToWorldScale);
		region.y = (int) ((bounds.y - view.y) / view.PxToWorldScale);
		region.width = (int) (bounds.width / view.PxToWorldScale);
		region.height = (int) (bounds.height / view.PxToWorldScale);


//		g.setColor(borderColor);
		g.setColor(tileColor);
		g.fillRect(region.x, region.y, region.width + 1, region.height + 1);

		g.fillRect((int) (region.x + borderSize), (int) (region.y + borderSize), (int) (region.width - 2 * borderSize), (int) (region.height - 2 * borderSize));


	}

	public float getTemperature()
	{
		return temperature;
	}

	public float getFoodType()
	{
		return foodType;
	}

	public float getFoodLevel()
	{
		return foodLevel;
	}
}
