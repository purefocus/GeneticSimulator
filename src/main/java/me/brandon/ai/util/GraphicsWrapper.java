package me.brandon.ai.util;

import java.awt.*;
import java.awt.image.BufferedImage;

public class GraphicsWrapper
{

	public static GraphicsWrapper wrap(Graphics2D g)
	{
		return new GraphicsWrapper(g);
	}

	public static GraphicsWrapper wrap(Graphics2D g, double offX, double offY)
	{
		return new GraphicsWrapper(g, offX, offY);
	}

	public static boolean allowAntiAliasing = true;

	private final Graphics2D g;
	private final double offX;
	private final double offY;

	public GraphicsWrapper(Graphics2D g)
	{
		this(g, 0, 0);
	}

	public GraphicsWrapper(Graphics2D g, double offX, double offY)
	{
		this.g = g;
		this.offX = offX;
		this.offY = offY;
	}

	public Graphics2D getGraphics()
	{
		return g;
	}

	public GraphicsWrapper wrap(double offX, double offY)
	{
		return wrap(g, offX, offY);
	}

	public void setAntialias()
	{
		if (allowAntiAliasing)
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	}

	public void dispose()
	{
		g.dispose();
	}

	public void setColor(Color c)
	{
		g.setColor(c);
	}

	public void drawLine(double x1, double y1, double x2, double y2)
	{
		g.drawLine((int) (offX + x1), (int) (offY + y1), (int) (offX + x2), (int) (offY + y2));
	}

	public void drawLine(Vector v1, Vector v2)
	{
		g.drawLine((int) (offX + v1.x), (int) (offY + v1.y), (int) (offX + v2.x), (int) (offY + v2.y));
	}

	public void drawRect(double x, double y, double w, double h)
	{
		g.drawRect((int) (offX + x), (int) (offY + y), (int) w, (int) h);
	}

	public void drawRect(Vector pos, double w, double h)
	{
		g.drawRect((int) (offX + pos.x), (int) (offY + pos.y), (int) w, (int) h);
	}

	public void fillRect(double x, double y, double w, double h)
	{
		g.fillRect((int) (offX + x), (int) (offY + y), (int) w, (int) h);
	}

	public void fillBackground(double w, double h)
	{
		g.fillRect((int) (offX - w), (int) (offY - h), (int) w * 2, (int) h * 2);
	}

	public void fillRect(Vector pos, double w, double h)
	{
		g.fillRect((int) (offX + pos.x), (int) (offY + pos.y), (int) w, (int) h);
	}

	public void drawOval(double x, double y, double w, double h)
	{
		g.drawOval((int) (offX + x - w / 2), (int) (offY + y - h / 2), (int) w, (int) h);
	}

	public void drawOval(Vector pos, double w, double h)
	{
		g.drawOval((int) (offX + pos.x - w / 2), (int) (offY + pos.y - h / 2), (int) w, (int) h);
	}

	public void fillOval(double x, double y, double w, double h)
	{
		g.fillOval((int) (offX + x - w / 2), (int) (offY + y - h / 2), (int) w, (int) h);
	}

	public void fillOval(Vector pos, double w, double h)
	{
		g.fillOval((int) (offX + pos.x - w / 2), (int) (offY + pos.y - h / 2), (int) w, (int) h);
	}

	public void drawCircle(double x, double y, double r)
	{
		g.drawOval((int) (offX + x - r / 2), (int) (offY + y - r / 2), (int) r, (int) r);
	}

	public void drawCircle(Vector pos, double r)
	{
		g.drawOval((int) (offX + pos.x - r / 2), (int) (offY + pos.y - r / 2), (int) r, (int) r);
	}

	public void fillCircle(double x, double y, double r)
	{
		g.fillOval((int) (offX + x - r / 2), (int) (offY + y - r / 2), (int) r, (int) r);
	}

	public void fillCircle(Vector pos, double r)
	{
		g.fillOval((int) (offX + pos.x - r / 2), (int) (offY + pos.y - r / 2), (int) r, (int) r);
	}

	public void drawPolygon(Polygon player)
	{
		player.translate((int) offX, (int) (offY));
		g.drawPolygon(player);
	}

	public void drawString(String str, int x, int y)
	{
		g.drawString(str, x, y);
	}

	public void drawString(int x, int y, String format, Object... obj)
	{
		g.drawString(String.format(format, obj), x, y);
	}

	public void drawCenteredString(String str, int x, int y)
	{
		FontMetrics metrics = g.getFontMetrics();
		int w = metrics.stringWidth(str);
		int h = metrics.getHeight();
		g.drawString(str, x - w / 2, y + h / 2);
	}

	public void drawImage(BufferedImage img, int x, int y, int w, int h)
	{
		g.drawImage(img, x, y, w, h, null);
	}


	public void drawImage(BufferedImage img, int x, int y)
	{
		g.drawImage(img, x, y, img.getWidth(), img.getHeight(), null);
	}

}
