package me.brandon.ai.ui;

import me.brandon.ai.config.WorldSave;
import me.brandon.ai.sim.evolve.GeneticSimulator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SimulatorUI extends JFrame implements ActionListener
{

	private GraphicsPanel graphicsPanel;

	private WorldGenerationUI worldGenerationUI;

	public SimulatorUI()
	{
		setupUI();
		pack();
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setVisible(true);
	}

	private void setupUI()
	{

		JPanel contentPane = new JPanel(new BorderLayout(10, 10));

		graphicsPanel = new GraphicsPanel();
		contentPane.add(graphicsPanel, BorderLayout.CENTER);


		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = menuBar.add(new JMenu("File"));
		fileMenu.add("Save").addActionListener(this);
		fileMenu.add("Load").addActionListener(this);

		JMenu worldMenu = menuBar.add(new JMenu("World"));
		worldMenu.add("Generator").addActionListener(this);

		setJMenuBar(menuBar);

		JToolBar tb = new JToolBar();
		contentPane.add(tb, BorderLayout.NORTH);
		((JButton) tb.add(new JButton("Render World"))).addActionListener(e ->
		{
			GeneticSimulator.updateRendering = !GeneticSimulator.updateRendering;
		});
		((JButton) tb.add(new JButton("Slow down"))).addActionListener(e ->
		{
			GeneticSimulator.ticksPerSecond -= 5;
		});

		((JSlider) tb.add(new JSlider(1, 1000, 20))).addChangeListener(e ->
		{
			GeneticSimulator.ticksPerSecond = ((JSlider) e.getSource()).getValue();
		});


		setContentPane(contentPane);
	}

	public GraphicsPanel getGraphicsPanel()
	{
		return graphicsPanel;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		switch (e.getActionCommand())
		{
			case "Save":
				WorldSave.saveWorld(GeneticSimulator.instance.getWorld());
				break;
			case "Load":
				WorldSave.loadWorld(GeneticSimulator.instance.getWorld());
				break;
			case "Generator":
				if (worldGenerationUI != null)
				{
					getContentPane().remove(worldGenerationUI);
				}
				else
				{
					worldGenerationUI = new WorldGenerationUI();
					getContentPane().add(worldGenerationUI, BorderLayout.WEST);
				}
				SwingUtilities.updateComponentTreeUI(this);
				break;
		}
	}
}
