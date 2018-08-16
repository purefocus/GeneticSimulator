package me.brandon.ai.ui.renderer;

import me.brandon.ai.util.GraphicsWrapper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.BufferStrategy;

public class RenderPanel extends Canvas implements ComponentListener
{

	private String title;
	private JPanel contentPane;
	private JFrame frame;

	public RenderPanel(int width, int height)
	{
		this(width, height, "Render Panel");
	}

	public RenderPanel(int width, int height, String title)
	{
		this.title = title;
		setPreferredSize(new Dimension(width, height));
		addComponentListener(this);
	}

	public void makeFrame()
	{
		frame = new JFrame(title);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		contentPane = new JPanel(new BorderLayout());
		contentPane.add(this, BorderLayout.CENTER);
	}

	public JPanel getContentPane()
	{
		return contentPane;
	}

	public void displayFrame()
	{
		frame.setContentPane(contentPane);
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
		try
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
				renderer.draw(GraphicsWrapper.wrap(g), getWidth(), getHeight());
			}


			g.dispose();
			bs.show();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
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

}
