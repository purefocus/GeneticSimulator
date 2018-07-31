package me.brandon.ai.neural;

import me.brandon.ai.neural.impl.NNetwork;
import me.brandon.ai.neural.impl.basic.ActNeurons;
import me.brandon.ai.neural.util.NetworkRenderer;
import me.brandon.ai.ui.RenderPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Test
{

	public static void main(String[] args)
	{

		Network network = NetworkFactory.createFactory()

				// setup layers
				.input(3)   // 0 - input
				.layer(2)   // 1
				.output(2)  // 2 - output

				// setup connections
				.fullyConnect(0, 1) // 0->1
				.fullyConnect(1, 2) // 1->2

				// set weights
				.weightRandom()


				//build
				.build();

		float[] input = {1.0f, 2.0f, 1.0f};

		network.setInput(input);
		network.compute();

		float[][] full = network.getAllValues();

		System.out.print("Network:\n");
		for (int i = 0; i < full.length; i++)
		{
			System.out.printf("\t%d {", i);
			for (int j = 0; j < full[i].length; j++)
			{
				System.out.printf("%2.2f, ", full[i][j]);
			}
			System.out.print("}\n");
		}

		RenderPanel renderPanel = new RenderPanel(300, 300);


		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(renderPanel, BorderLayout.CENTER);
		frame.setContentPane(panel);
		frame.pack();
		frame.setVisible(true);

		new Thread(() ->
		{
			for (int i = 0; i < 100; i++)
			{
				try
				{
					Thread.sleep(100);
				} catch (InterruptedException e)
				{
					e.printStackTrace();
				}
				renderPanel.render(network::renderNetwork);
			}


		}).start();


	}
}
