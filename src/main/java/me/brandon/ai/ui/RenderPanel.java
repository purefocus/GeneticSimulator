package me.brandon.ai.ui;

import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.image.BufferStrategy;

public class RenderPanel extends Canvas implements ComponentListener
{

	public RenderPanel(int width, int height)
	{
		setPreferredSize(new Dimension(width, height));
		addComponentListener(this);
	}

	private Renderer renderer;

	public void render(Renderer renderer)
	{
		this.renderer = renderer;
		render();
	}

	public void render()
	{
		BufferStrategy bs = this.getBufferStrategy();

		if (bs == null)
		{
			createBufferStrategy(3);
			return;
		}

		Graphics2D g = (Graphics2D) bs.getDrawGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		if (renderer != null)
		{
			renderer.draw(g, getWidth(), getHeight());
		}


		g.dispose();
		bs.show();
	}

	@Override
	public void componentResized(ComponentEvent e)
	{
		render();
	}

	@Override
	public void componentMoved(ComponentEvent e)
	{

	}

	@Override
	public void componentShown(ComponentEvent e)
	{

	}

	@Override
	public void componentHidden(ComponentEvent e)
	{

	}

	public interface Renderer
	{
		void draw(Graphics g, int width, int height);
	}

}
