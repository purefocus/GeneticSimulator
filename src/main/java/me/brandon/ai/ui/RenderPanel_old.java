package me.brandon.ai.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferStrategy;

public class RenderPanel_old extends Canvas implements ComponentListener
{

	public RenderPanel_old(int width, int height)
	{
		setPreferredSize(new Dimension(width, height));
		addComponentListener(this);
	}

	public void makeFrame()
	{
		JFrame frame = new JFrame("Render Panel");
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(this, BorderLayout.CENTER);
		frame.setContentPane(panel);
		frame.pack();
		frame.setVisible(true);
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
