package me.brandon.ai.ui;

import javax.swing.*;
import java.awt.*;

public class SimulatorUI extends JFrame
{

	GraphicsPanel graphicsPanel;

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


		setContentPane(contentPane);
	}

	public GraphicsPanel getGraphicsPanel()
	{
		return graphicsPanel;
	}
}
