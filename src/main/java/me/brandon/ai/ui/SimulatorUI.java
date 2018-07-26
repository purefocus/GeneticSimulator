package me.brandon.ai.ui;

import me.brandon.ai.config.WorldSave;
import me.brandon.ai.gensim.GeneticSimulator;

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
