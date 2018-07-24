package me.brandon.ai.gensim.world;

import me.brandon.ai.config.ConfigOption;
import org.w3c.dom.css.Rect;

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

	protected Color tileColor;
	protected Color borderColor;

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

		tileColor = Color.getHSBColor(foodType, temperature, foodLevel);
	}

	public synchronized float consume(float request)
	{
		float consumed = Math.max(request, foodLevel);
		foodLevel -= consumed;

		return consumed;
	}

	@Override
	public boolean isVisible(Rectangle bounds)
	{
		return this.bounds.intersects(bounds);
	}

	@Override
	public void draw(Graphics2D g, Rectangle drawBounds)
	{
		double scale = World.renderScale;

		Rectangle region = new Rectangle();
		region.x = (int) ((bounds.x - drawBounds.x) * scale);
		region.y = (int) ((bounds.y - drawBounds.y) * scale);
		region.width = (int) (bounds.width * scale);
		region.height = (int) (bounds.height * scale);

		g.setColor(tileColor);
		g.fillRect(region.x, region.y, region.width, region.height);

		g.setColor(borderColor);
		g.drawRect(region.x, region.y, region.width, region.height);

	}
}
