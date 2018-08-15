package me.brandon.ai.util;

import java.awt.*;

public final class GraphicUtils
{

	public static void drawLine(Graphics g, double x1, double y1, double x2, double y2)
	{
		g.drawLine((int) x1, (int) y1, (int) x1, (int) x1);
	}

	public static void drawLine(Graphics g, Vector v1, Vector v2)
	{
		g.drawLine((int) v1.x, (int) v1.y, (int) v2.x, (int) v2.y);
	}

	public static void drawRect(Graphics g, double x, double y, double w, double h)
	{
		g.drawRect((int) x, (int) y, (int) w, (int) h);
	}

	public static void fillRect(Graphics g, double x, double y, double w, double h)
	{
		g.fillRect((int) x, (int) y, (int) w, (int) h);
	}

	public static void drawOval(Graphics g, double x, double y, double w, double h)
	{
		g.drawOval((int) (x - w / 2), (int) (y - h / 2), (int) w, (int) h);
	}

	public static void fillOval(Graphics g, double x, double y, double w, double h)
	{
		g.fillOval((int) (x - w / 2), (int) (y - h / 2), (int) w, (int) h);
	}

	public static void drawCircle(Graphics g, double x, double y, double r)
	{
		g.drawOval((int) (x - r / 2), (int) (y - r / 2), (int) r, (int) r);
	}

	public static void fillCircle(Graphics g, double x, double y, double r)
	{
		g.fillOval((int) (x - r / 2), (int) (y - r / 2), (int) r, (int) r);
	}


	public static void drawCircle(Graphics g, Vector v, double r)
	{
		g.drawOval((int) (v.x - r / 2), (int) (v.y - r / 2), (int) r, (int) r);
	}

	public static void fillCircle(Graphics g, Vector v, double r)
	{
		g.fillOval((int) (v.x - r / 2), (int) (v.y - r / 2), (int) r, (int) r);
	}


}
