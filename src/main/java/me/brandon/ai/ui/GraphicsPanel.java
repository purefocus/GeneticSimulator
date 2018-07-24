package me.brandon.ai.ui;

import me.brandon.ai.config.ConfigOption;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

public class GraphicsPanel extends JPanel implements MouseListener, MouseWheelListener, KeyListener, MouseMotionListener
{

	private BufferedImage display;

	@ConfigOption
	public static int ui_renderer_width = 1000;

	@ConfigOption
	public static int ui_renderer_height = 1000;

	private Viewport view;

	public GraphicsPanel()
	{
		setPreferredSize(new Dimension(ui_renderer_width, ui_renderer_height));
		view = new Viewport();
		view.width = view.viewWidth = ui_renderer_width;
		view.height = view.viewHeight = ui_renderer_height;
		view.x = 0;
		view.y = 0;
		view.scale = 1.0f;

		addMouseListener(this);
		addMouseMotionListener(this);
		addMouseWheelListener(this);
		addKeyListener(this);
	}

	public void initDisplay()
	{
		display = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_3BYTE_BGR);

		Graphics2D g = (Graphics2D) display.getGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	}

	public void clear()
	{

	}

	public void draw(Drawable drawable)
	{
		if (display == null)
		{
			initDisplay();
		}
		else if (display.getWidth() != getWidth() || display.getHeight() != getHeight())
		{
			initDisplay();
			view.viewWidth = getWidth();
			view.viewHeight = getHeight();
			view.width = (int) (getWidth() / view.scale);
			view.height = (int) (getHeight() / view.scale);
		}

		Graphics2D g = (Graphics2D) display.getGraphics();

		drawable.draw(g, view);

		SwingUtilities.invokeLater(this::repaint);
	}

	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);

		if (display != null)
		{
			g.drawImage(display, 0, 0, getWidth(), getHeight(), this);
		}

		g.setColor(Color.WHITE);
		g.drawString(String.format("[%d, %d]", view.width, view.height), (int) 10, (int) 30);
	}


	private Point pressedLoc;

	@Override
	public void mouseClicked(MouseEvent e)
	{

	}

	@Override
	public void mousePressed(MouseEvent e)
	{
		pressedLoc = e.getPoint();
	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
		pressedLoc = null;
	}

	@Override
	public void mouseEntered(MouseEvent e)
	{

	}

	@Override
	public void mouseExited(MouseEvent e)
	{

	}

	@Override
	public void keyTyped(KeyEvent e)
	{

	}

	@Override
	public void keyPressed(KeyEvent e)
	{

	}

	@Override
	public void keyReleased(KeyEvent e)
	{

	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e)
	{
		double mx = (e.getX() / view.scale);
		double my = (e.getY() / view.scale);

		float zoom = 0.01f * e.getWheelRotation();

		view.scale -= zoom;

		view.width = (int) (getWidth() / view.scale);
		view.height = (int) (getHeight() / view.scale);


		view.x -= mx * zoom;
		view.y -= my * zoom;

	}

	@Override
	public void mouseDragged(MouseEvent e)
	{
		Point pos = e.getPoint();

		int dx = pressedLoc.x - pos.x;
		int dy = pressedLoc.y - pos.y;
		pressedLoc = pos;

		view.x += dx / view.scale;
		view.y += dy / view.scale;


	}

	@Override
	public void mouseMoved(MouseEvent e)
	{

	}
}
