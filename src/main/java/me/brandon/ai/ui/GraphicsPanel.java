package me.brandon.ai.ui;

import me.brandon.ai.AIMain;
import me.brandon.ai.config.ConfigOption;
import me.brandon.ai.config.WorldSave;
import me.brandon.ai.evolve.GeneticSimulator;
import me.brandon.ai.evolve.world.World;
import me.brandon.ai.evolve.world.creature.Creature;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

public class GraphicsPanel extends Canvas implements MouseListener, MouseWheelListener, KeyListener, MouseMotionListener
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
		view.PxToWorldScale = 1.0f;

		addMouseListener(this);
		addMouseMotionListener(this);
		addMouseWheelListener(this);
		addKeyListener(this);


//		getGraphicsConfiguration().

//		setDoubleBuffered(true);
	}

	public void initDisplay()
	{
		display = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_3BYTE_BGR);

		Graphics2D g = (Graphics2D) display.getGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		view.viewWidth = getWidth();
		view.viewHeight = getHeight();

		view.fitToScreen();
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
		}

		Graphics2D g = (Graphics2D) display.getGraphics();

		drawable.draw(g, view);

//		SwingUtilities.invokeLater(this::repaint);

		render();

	}

	public void render()
	{
		// TODO Auto-generated method stub
		BufferStrategy bs = this.getBufferStrategy();

		if (bs == null)
		{
			createBufferStrategy(3);
			return;
		}

		Graphics g = bs.getDrawGraphics();
		//////////////////////////////

		g.setColor(Color.DARK_GRAY);
		g.fillRect(0, 0, getWidth(), getHeight());
		if (display != null && GeneticSimulator.updateRendering)
		{
			g.drawImage(display, 0, 0, getWidth(), getHeight(), this);
		}

		World world = GeneticSimulator.world();

		world.draw((Graphics2D) g, view);

		g.setColor(Color.WHITE);
		g.drawString(String.format("view: [%2.1f, %2.1f, %2.1f, %2.1f, %2.2f]", view.x, view.y, view.width, view.height, view.PxToWorldScale), (int) 10, (int) 15);
		if (mouseLocation != null)
		{
			g.drawString(String.format("mouse: [%d, %d]", mouseLocation.x, mouseLocation.y), (int) 10, (int) 15);
		}

		g.drawString(String.format("world: [time: %2.2f pop: %d]", world.getTime() / 100f, world.getPopulationCount()), 10, 30);

		g.drawRect(view.pixelX, view.pixelY, view.pixelWidth, view.pixelHeight);
//		p.render(g);
//		c.render(g);


		//g.drawImage(player,100,100,this);

		//////////////////////////////
		g.dispose();
		bs.show();
	}


//	public void paint(Graphics g)
//	{
////		super.paintComponent(g);
//
//
////		BufferStrategy bufferStrategy =
//
//		BufferedImage img = new BufferedImage(getWidth(), getHeight(), display.getType());
//		Graphics2D g2 = (Graphics2D) img.getGraphics();
//		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//
//		if (display != null)
//		{
//			g2.drawImage(display, 0, 0, getWidth(), getHeight(), this);
//		}
//
//		g2.setColor(Color.WHITE);
//		g2.drawString(String.format("view: [%2.1f, %2.1f, %2.1f, %2.1f, %2.2f]", view.x, view.y, view.width, view.height, view.PxToWorldScale), (int) 10, (int) 30);
//		if (mouseLocation != null)
//		{
//			g2.drawString(String.format("mouse: [%d, %d]", mouseLocation.x, mouseLocation.y), (int) 10, (int) 55);
//		}
//
//		g2.drawRect(view.pixelX, view.pixelY, view.pixelWidth, view.pixelHeight);
//
//		g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
//	}


	private Point mouseLocation;
	private Point pressedLoc;

	@Override
	public void mouseClicked(MouseEvent e)
	{
		World world = GeneticSimulator.world();
		Point p = world.view.PxToWorld(e.getX(), e.getY());

		Creature c = world.getCreatureAtPosition(p.x, p.y);
		if (c != null)
		{
			World.selectedCreature = c;
			AIMain.renderCreatureData();
		}
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
		mouseLocation = e.getPoint();
	}

	@Override
	public void mouseEntered(MouseEvent e)
	{
		requestFocus();
		mouseLocation = e.getPoint();
	}

	@Override
	public void mouseExited(MouseEvent e)
	{
		mouseLocation = null;
	}

	@Override
	public void keyTyped(KeyEvent e)
	{

	}

	@Override
	public void keyPressed(KeyEvent e)
	{
		if (e.isControlDown() && e.isShiftDown() && e.getKeyCode() == KeyEvent.VK_S)
		{
			World world = GeneticSimulator.instance.getWorld();

			WorldSave.saveWorld(world);
		}
		else if (e.isControlDown() && e.isShiftDown() && e.getKeyCode() == KeyEvent.VK_L)
		{
			World world = GeneticSimulator.instance.getWorld();

			WorldSave.loadWorld(world);
		}
		else if (e.isControlDown() && e.isShiftDown() && e.getKeyCode() == KeyEvent.VK_G)
		{
			World world = GeneticSimulator.instance.getWorld();
			world.generateWorld();
		}
		else if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_P)
		{
			GeneticSimulator.instance.stopSimulation();
		}
		else if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_R)
		{
			GeneticSimulator.instance.startSimulation();
		}
	}

	@Override
	public void keyReleased(KeyEvent e)
	{

	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e)
	{
		double mx = (e.getX() * view.PxToWorldScale);
		double my = (e.getY() * view.PxToWorldScale);

		float zoom = 0.01f * e.getWheelRotation();
//		if (zoom < 0.01f)
//		{
//			zoom = 0.01f;
//		}

		view.PxToWorldScale += zoom;

		view.width = getWidth() * view.PxToWorldScale;
		view.height = getHeight() * view.PxToWorldScale;


		view.x -= mx * zoom;
		view.y -= my * zoom;


		view.ensureOnScreen();

		render();

	}

	@Override
	public void mouseDragged(MouseEvent e)
	{
		Point pos = e.getPoint();

		int dx = pressedLoc.x - pos.x;
		int dy = pressedLoc.y - pos.y;
		pressedLoc = pos;

		view.x += dx * view.PxToWorldScale;
		view.y += dy * view.PxToWorldScale;

		mouseLocation = e.getPoint();
		view.ensureOnScreen();

		render();
	}

	@Override
	public void mouseMoved(MouseEvent e)
	{

		mouseLocation = e.getPoint();
	}
}
