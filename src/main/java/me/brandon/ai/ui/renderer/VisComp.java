package me.brandon.ai.ui.renderer;

import java.awt.*;

public class VisComp
{
	public boolean focusable;
	public boolean focused;
	public boolean visible;

	public int px;
	public int py;
	public int width;
	public int height;

	private String title;
	private int hiddenWidth;
	private int hiddenHeight;

	private boolean resizeable;
	private boolean adjWidth;
	private boolean adjHeight;
	private boolean adjPos;

	private int lmx;
	private int lmy;

	public VisComp(String title, int px, int py, int width, int height)
	{
		this(title, px, py, width, height, true, false);
	}

	public VisComp(String title, int px, int py, int width, int height, boolean focusable, boolean resizeable)
	{
		this.focusable = focusable;
		this.px = px;
		this.py = py;
		this.width = width;
		this.height = height;
		this.visible = true;
		this.title = title;
		this.resizeable = resizeable;

	}

	public boolean contains(Point p)
	{
		if (visible)

			return p.x > px && p.y > py && p.x < px + width && p.y < py + height;

		return p.x > px && p.y > py && p.x < px + hiddenWidth && p.y < py + hiddenHeight;
	}

	public boolean click(Point p, boolean doubleClicked, boolean lock)
	{
		if (contains(p))
		{

			if (lock)
			{
				focusable = !focusable;
				return true;
			}
			if (!focusable)
			{
				return false;
			}
			if (doubleClicked)
			{
				System.out.println(title);
				visible = !visible;
				return true;
			}
			focused = true;
			lmx = p.x;
			lmy = p.y;

			if (resizeable)
			{
				adjWidth = p.x > px + width - 10;
				adjHeight = p.y > py + height - 10;
				adjPos = !(adjWidth || adjHeight);
			}
			else
			{
				adjPos = true;
			}

		}
		else
		{
			focused = false;
		}

		return focused;
	}

	public void move(Point p)
	{
		if (focused)
		{
			int dx = p.x - lmx;
			int dy = p.y - lmy;
			if (adjPos)
			{
				px += dx;
				py += dy;
			}
			else
			{
				if (adjWidth)
					width += dx;
				if (adjHeight)
					height += dy;
			}


			lmx = p.x;
			lmy = p.y;
		}
	}

	public void release(Point p)
	{
		if (focused)
		{
			focused = false;
		}
	}

	public void draw(Graphics g)
	{
		if (visible)
		{
			g.setColor(Color.DARK_GRAY);
			g.fillRect(px, py, width, height);
			g.setColor(Color.LIGHT_GRAY);
			g.drawRect(px, py, width, height);
		}
		else
		{
			if (hiddenWidth == 0)
			{
				FontMetrics m = g.getFontMetrics();
				hiddenWidth = m.stringWidth(title) + 4;
				hiddenHeight = m.getHeight() + 4;
			}
			g.setColor(Color.RED);
			g.fillRect(px, py, hiddenWidth, hiddenHeight);
			g.setColor(Color.LIGHT_GRAY);
			g.drawRect(px, py, hiddenWidth, hiddenHeight);
			g.setColor(Color.BLACK);
			g.drawString(title, px + 2, py + hiddenHeight / 2 + 4);
		}
	}


}
