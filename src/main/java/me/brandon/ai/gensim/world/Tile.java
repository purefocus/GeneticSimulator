package me.brandon.ai.gensim.world;

import me.brandon.ai.config.ConfigOption;
import me.brandon.ai.ui.Drawable;
import me.brandon.ai.ui.Viewport;

import java.awt.*;

public class Tile implements Drawable
{
	@ConfigOption
	public static float tile_maxFood = 100f;

	protected Rectangle bounds;

	protected int tileX;
	protected int tileY;

	protected float foodLevel; // brightness
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

	public void tick(int time)
	{
		if (foodLevel < maxGrowth)
		{
			foodLevel += growthRate;
		}


		tileColor = Color.getHSBColor(Math.abs(temperature / 2), 1, foodLevel);
//		tileColor = Color.getHSBColor(0, (float) 0, (float) genValue);
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
		region.x = (int) ((bounds.x - view.x) * view.scale);
		region.y = (int) ((bounds.y - view.y) * view.scale);
		region.width = (int) (bounds.width * view.scale);
		region.height = (int) (bounds.height * view.scale);

		g.setColor(tileColor);
		g.fillRect(region.x, region.y, region.width, region.height);

//		g.setColor(borderColor);
//		g.drawRect(region.x + 1, region.y + 1, region.width - 2, region.height - 2);

	}
}
