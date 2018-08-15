package me.brandon.ai.ui;

import me.brandon.ai.evolve.world.World;

import java.awt.*;

public class Viewport
{
	private float viewPadding = 50;
	public double x;
	public double y;
	public double width;
	public double height;

	public int pixelX;
	public int pixelY;
	public int pixelWidth;
	public int pixelHeight;

	public int viewWidth;
	public int viewHeight;

	public float PxToWorldScale;

	public Rectangle worldBounds = new Rectangle();
	public Rectangle pxBounds = new Rectangle();

	public int xPx()
	{
		return (int) (x / PxToWorldScale);
	}

	public int yPx()
	{
		return (int) (y / PxToWorldScale);
	}

	public void ensureOnScreen()
	{
		int xPx = (int) -(x / PxToWorldScale);
		int yPx = (int) -(y / PxToWorldScale);
		int widthPx = (int) ((World.worldWidth * World.tileSize) / PxToWorldScale);
		int heightPx = (int) ((World.worldHeight * World.tileSize) / PxToWorldScale);

		if (viewWidth - xPx < viewPadding)
		{
			x = (int) -((viewWidth - viewPadding) * PxToWorldScale);
		}
		else if (xPx + widthPx < viewPadding)
		{
			x = (int) -((viewPadding - widthPx) * PxToWorldScale);
		}

		if (viewHeight - yPx < viewPadding)
		{
			y = (int) -((viewHeight - viewPadding) * PxToWorldScale);
		}
		else if (yPx + heightPx < viewPadding)
		{
			y = (int) -((viewPadding - heightPx) * PxToWorldScale);
		}


		int worldWidth = World.worldWidth * World.tileSize;
		int worldHeight = World.worldHeight * World.tileSize;

		pixelX = (int) (-x / PxToWorldScale);
		pixelY = (int) (-y / PxToWorldScale);
		pixelWidth = (int) (worldWidth / PxToWorldScale);
		pixelHeight = (int) (worldHeight / PxToWorldScale);

		calculateBounds();
	}

	public void fitToScreen()
	{

		int worldWidth = World.worldWidth * World.tileSize;
		int worldHeight = World.worldHeight * World.tileSize;

		PxToWorldScale = Math.max((float) worldWidth / ((float) viewWidth - viewPadding), (float) worldHeight / ((float) viewHeight - viewPadding));

		width = viewWidth * PxToWorldScale;
		height = viewHeight * PxToWorldScale;

		pixelWidth = worldToPx(worldWidth);//int) (worldWidth / PxToWorldScale);
		pixelHeight = worldToPx(worldHeight);//(int) (worldHeight / PxToWorldScale);

		pixelX = (viewWidth - pixelWidth) / 2;
		pixelY = (viewHeight - pixelHeight) / 2;

		x = -pixelX * PxToWorldScale;
		y = -pixelY * PxToWorldScale;

		calculateBounds();

	}

	private void calculateBounds()
	{
		worldBounds.x = (int) x;
		worldBounds.y = (int) y;
		worldBounds.width = (int) width;
		worldBounds.height = (int) height;

		pxBounds.x = pixelX;
		pxBounds.y = pixelY;
		pxBounds.width = pixelWidth;
		pxBounds.height = pixelHeight;
	}

	public int worldToPx(double px)
	{
		return (int) (px / PxToWorldScale);
	}

	public double pxToWorld(int px)
	{
		return px * PxToWorldScale;
	}

	public Point worldToPx(double wx, double wy)
	{
		return new Point((int) ((wx - x) / PxToWorldScale), (int) ((wy - y) / PxToWorldScale));
	}

	public Point PxToWorld(double px, double py)
	{
		return new Point((int) (px * PxToWorldScale + x), (int) (py * PxToWorldScale + y));
	}

	public Rectangle getPxBounds()
	{
		return pxBounds;
	}

	public Rectangle getWorldBounds()
	{
		return worldBounds;
	}

	public Rectangle worldToPx(Rectangle rect)
	{
		return new Rectangle((int) ((rect.x - x) / PxToWorldScale),
				(int) ((rect.y - y) / PxToWorldScale),
				(int) (rect.width / PxToWorldScale),
				(int) (rect.height / PxToWorldScale));
	}

	public boolean isVisible(Rectangle bounds)
	{
		return pxBounds.intersects(worldToPx(bounds));
	}
}
