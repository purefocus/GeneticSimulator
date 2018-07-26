package me.brandon.ai.util;

import me.brandon.ai.ui.Viewport;

import java.awt.*;

public class ConvertedGraphics
{

	public static void drawRect(Graphics2D g, double x, double y, double width, double height, Viewport view)
	{
		Point converted = view.worldToPx(x, y);
		g.drawRect(converted.x, converted.y, view.worldToPx(width), view.worldToPx(height));
	}

	public static void fillRect(Graphics2D g, double x, double y, double width, double height, Viewport view)
	{
		Point converted = view.worldToPx(x, y);
		g.fillRect(converted.x, converted.y, view.worldToPx(width), view.worldToPx(height));
	}

	public static void drawCircle(Graphics2D g, double x, double y, double r, Viewport view)
	{
		Point converted = view.worldToPx(x, y);
		int rad = view.worldToPx(r);
		g.drawOval(converted.x - rad, converted.y - rad, rad * 2, rad * 2);
	}

	public static void fillCircle(Graphics2D g, double x, double y, double r, Viewport view)
	{
		Point converted = view.worldToPx(x, y);
		int rad = view.worldToPx(r);
		g.fillOval(converted.x - rad, converted.y - rad, rad * 2, rad * 2);
	}

	public static void drawLine(Graphics2D g, double x1, double y1, double x2, double y2, Viewport view)
	{
		Point c1 = view.worldToPx(x1, y1);
		Point c2 = view.worldToPx(x2, y2);
		g.drawLine(c1.x, c1.y, c2.x, c2.y);
	}

	public static void drawLineOff(Graphics2D g, double x, double y, double offX, double offY, Viewport view)
	{
		Point c = view.worldToPx(x, y);
		g.drawLine(c.x, c.y, c.x + view.worldToPx(offX), c.y + view.worldToPx(offY));
	}

}
