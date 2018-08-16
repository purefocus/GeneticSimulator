package me.brandon.ai.api.game;

import me.brandon.ai.api.genetic.GeneticEntity;
import me.brandon.ai.api.genetic.population.Depopulator;
import me.brandon.ai.api.genetic.population.Population;
import me.brandon.ai.api.genetic.population.Populator;
import me.brandon.ai.api.neural.NeuralEntity;
import me.brandon.ai.ui.renderer.VisComp;
import me.brandon.ai.ui.renderer.RenderPanel;
import me.brandon.ai.ui.renderer.Renderer;
import me.brandon.ai.util.GraphicsWrapper;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

public abstract class GameSimulator<T extends GeneticEntity>
		implements Runnable, Renderer, KeyListener, ActionListener, ChangeListener, MouseListener, MouseMotionListener
{

	protected RenderPanel renderPanel;

	protected Population<T> population;
	protected T focusedEntity;

	protected boolean running;
	protected boolean stopped;
	protected long tickDelay;
	protected long computeStart;

	protected boolean render;
	protected boolean continuousRun;

	protected int populationSize;

	protected int generation;

	protected int gameWindowWidth;
	protected int gameWindowHeight;
	protected int offX;
	protected int offY;

	private VisComp brainComponent;
	private VisComp statusComponent;
	private VisComp gameComponent;

	private VisComp focusedComponent;

	private VisComp visualComponents[];

	private Point p;

	public GameSimulator(int width, int height, int populationSize)
	{
		this.populationSize = populationSize;

		tickDelay = 10;
		renderPanel = new RenderPanel(width, height, "Game Simulator");
		renderPanel.addKeyListener(this);
		renderPanel.makeFrame();
		renderPanel.getContentPane().add(setupControlBar(), BorderLayout.NORTH);
		renderPanel.displayFrame();
		renderPanel.addMouseListener(this);
		renderPanel.addMouseMotionListener(this);


		statusComponent = new VisComp("Status", 10, 10, 300, 80, true, false);
		brainComponent = new VisComp("Brain", 10, 100, 300, 300, true, true);
		gameComponent = new VisComp("Game", 0, 0, width, height, false, false);

		visualComponents = new VisComp[]{statusComponent, brainComponent, gameComponent};


		offX = 0;
		offY = 0;
		gameWindowWidth = width;
		gameWindowHeight = height;
		render = true;
		continuousRun = false;

		stopped = true;
		running = false;
		brainComponent.visible = true;

	}

	public void init()
	{
		population = new Population<>(populationSize);
		population.setPopulator(getPopulator());
		population.setDepopulator(getDepopulator());
		renderPanel.render(this);
	}

	public void start()
	{
		if (stopped && !running)
		{
			running = true;
			stopped = false;
			new Thread(this).start();
		}
	}

	public void stop()
	{
		running = false;
//		while (!stopped) ; // waits until thread has stopped
	}

	public abstract Depopulator<T> getDepopulator();

	public abstract Populator<T> getPopulator();

	public void newGeneration()
	{
		generation++;
		population.kill();
		population.populate();

		render = !continuousRun;

		if (!render)
		{
			renderPanel.render(this);
		}
	}

	public void run()
	{
		computeStart = System.currentTimeMillis();
		while (running)
		{

			if (population.isAllDead())
			{
				focusedEntity = population.getBest();
				newGeneration();
			}
			population.tick();
			if (focusedEntity == null || focusedEntity.isDead())
			{
				focusedEntity = population.getBestAlive();
			}

			if (render)
			{
				renderPanel.render(this);
				try
				{

					long delayTime = tickDelay - (System.currentTimeMillis() - computeStart);
					if (delayTime > 0)
					{
						Thread.sleep(delayTime);
					}
					computeStart = System.currentTimeMillis();
				} catch (Exception e)
				{
					e.printStackTrace();
				}
			}

		}
		stopped = true;
	}

	public void draw(GraphicsWrapper g, int width, int height)
	{
		g.setAntialias();
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, width, height);
		if (render)
		{
			if (focusedEntity != null)
			{
				gameComponent.draw(g.getGraphics());
				if (gameComponent.visible)
				{
					focusedEntity.draw(g.wrap(gameComponent.px + gameComponent.width / 2, gameComponent.py + gameComponent.height / 2), gameComponent.width / 2, gameComponent.height / 2);
				}
				brainComponent.draw(g.getGraphics());
				if (brainComponent.visible && focusedEntity instanceof NeuralEntity)
				{

					BufferedImage img = new BufferedImage(brainComponent.width, brainComponent.height, BufferedImage.TYPE_4BYTE_ABGR);
					GraphicsWrapper g2 = GraphicsWrapper.wrap((Graphics2D) img.getGraphics());
					g2.setAntialias();
					((NeuralEntity) focusedEntity).getNeuralNetwork().renderNetwork(g2.getGraphics(), img.getWidth(), img.getHeight());

					g.drawImage(img, brainComponent.px, brainComponent.py, img.getWidth(), img.getHeight());

				}
			}
		}

		renderStatus(g);
	}

	private void renderStatus(GraphicsWrapper g)
	{
		statusComponent.draw(g.getGraphics());
		if (statusComponent.visible)
		{
			int px = statusComponent.px + 5;
			int py = statusComponent.py + 5;
			statusComponent.draw(g.getGraphics());
			int strSpread = 15;
			g.setColor(Color.WHITE);
			g.drawString(px, py += strSpread, "Generation: %d", generation);
			g.drawString(px, py += strSpread, "Population: %d/%d", population.getAliveCount(), population.getSize());
			if (focusedEntity != null)
			{
				g.drawString(px, py += strSpread, "Fitness: %2.1f", focusedEntity.getFitness());
			}
		}

	}

	@Override
	public void keyTyped(KeyEvent e)
	{

	}

	@Override
	public void keyPressed(KeyEvent e)
	{
		if (e.isControlDown())
		{
			switch (e.getKeyCode())
			{
				case KeyEvent.VK_F:
					render = !render;
					break;
				case KeyEvent.VK_D:
					continuousRun = !continuousRun;
					render = false;
					break;
				case KeyEvent.VK_G:
					populationSize += 3;
					population.setPopulationSize(populationSize);
					break;
				case KeyEvent.VK_S:
					populationSize -= 3;
					population.setPopulationSize(populationSize);
					break;
				case KeyEvent.VK_B:
					brainComponent.visible = !brainComponent.visible;
					break;
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e)
	{

	}

	public void mouseClicked(MouseEvent e)
	{
	}

	public void mousePressed(MouseEvent e)
	{

		Point p = e.getPoint();
		for (VisComp comp : visualComponents)
		{
			if (comp.click(p, e.getClickCount() == 2, e.isShiftDown()))
			{
				focusedComponent = comp;
				break;
			}
		}
	}

	public void mouseReleased(MouseEvent e)
	{
		if (focusedComponent != null)
			focusedComponent.release(e.getPoint());
	}

	public void mouseEntered(MouseEvent e)
	{
	}

	public void mouseExited(MouseEvent e)
	{
	}

	public void mouseDragged(MouseEvent e)
	{
		if (focusedComponent != null)
			focusedComponent.move(e.getPoint());
	}

	/**
	 * Invoked when the mouse cursor has been moved onto a component
	 * but no buttons have been pushed.
	 */
	public void mouseMoved(MouseEvent e)
	{
		p = e.getPoint();
	}

	/**
	 * Process toolbar actions
	 */
	public void actionPerformed(ActionEvent e)
	{
		switch (e.getActionCommand())
		{
			case "Run":
				start();
				((JButton) e.getSource()).setText("Stop");
				break;
			case "Stop":
				stop();
				((JButton) e.getSource()).setText("Run");
				break;

		}
	}

	public void stateChanged(ChangeEvent e)
	{
		if (e.getSource().equals(popControlSlider))
		{
			populationSize = popControlSlider.getValue();
			popLabel.setText(String.format("Population (%d): ", populationSize));
			population.setPopulationSize(populationSize);
		}
		else if (e.getSource().equals(speedControlSlider))
		{
			tickDelay = speedControlSlider.getValue();
			speedLabel.setText(String.format("Speed (%d): ", tickDelay));
		}
	}

	private JLabel popLabel;
	private JLabel speedLabel;
	private JSlider popControlSlider;
	private JSlider speedControlSlider;

	private JToolBar setupControlBar()
	{
		JToolBar toolBar = new JToolBar();

		((JButton) toolBar.add(new JButton("Stop"))).addActionListener(this);
		popControlSlider = new JSlider(1, 500, populationSize);
		popControlSlider.addChangeListener(this);
		speedControlSlider = new JSlider(1, 999, (int) tickDelay);
		speedControlSlider.addChangeListener(this);


		toolBar.add(popLabel = new JLabel(String.format("Population (%d): ", populationSize)));
		toolBar.add(popControlSlider);
		toolBar.add(speedLabel = new JLabel(String.format("Speed (%d): ", tickDelay)));
		toolBar.add(speedControlSlider);


		return toolBar;
	}
}
