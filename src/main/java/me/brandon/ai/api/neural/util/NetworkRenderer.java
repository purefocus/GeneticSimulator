package me.brandon.ai.api.neural.util;

import me.brandon.ai.api.neural.Neuron;
import me.brandon.ai.api.neural.NeuronType;
import me.brandon.ai.api.neural.impl.AbstractNetwork;
import me.brandon.ai.api.neural.impl.NConnection;
import me.brandon.ai.api.neural.impl.BasicNeuron;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class NetworkRenderer
{

	private final AbstractNetwork network;

	private NodeData data[][];
	private Map<Integer, NodeData> dataMap;

	private BufferedImage img;
	private boolean needsBaseUpdate;
	private boolean dataChanged;

	public NetworkRenderer(AbstractNetwork network)
	{
		this.network = network;
		this.dataMap = new HashMap<>();
		int layerCount = network.getLayerCount();
		data = new NodeData[layerCount][];

		for (int i = 0; i < layerCount; i++)
		{
			Neuron layer[] = network.getLayer(i);
			data[i] = new NodeData[layer.length];
			for (int j = 0; j < layer.length; j++)
			{
				data[i][j] = new NodeData();
				data[i][j].n = (BasicNeuron) layer[j];
				dataMap.put(((BasicNeuron) layer[j]).getId(), data[i][j]);
			}
		}

	}

	private void renderBase(int width, int height)
	{

		int padX = 5;
		int padY = 5;
		width -= padX * 2;
		height -= padY * 2;

		int layerCount = network.getLayerCount();
		int spacingX = width / layerCount;

		int posX = padX + spacingX / 2;
		for (int i = 0; i < layerCount; i++)
		{
			BasicNeuron layer[] = network.getLayer(i);
			int spacingY = height / (layer.length);
			int posY = spacingY / 2;
			for (int j = 0; j < layer.length; j++)
			{
				data[i][j].p = new Point(posX, posY);
				posY += spacingY;
			}
			posX += spacingX;
		}

	}

	private void renderValues(Graphics g)
	{
		dataMap.values().forEach(data ->
		{
			NConnection cons[] = data.n.getInputs();
			for (NConnection con : cons)
			{
				if (con.enabled())
				{
					g.setColor(getWeightColor(con.weight()));
					NodeData d = dataMap.get(((BasicNeuron) con.source()).getId());
					if (d != null)
					{
						g.drawLine(data.p.x, data.p.y, d.p.x, d.p.y);
					}
				}
			}
		});
		int nodeSize = 20;
		FontMetrics metrics = g.getFontMetrics();
		int h = metrics.getHeight() / 2;
		dataMap.values().forEach(d ->
		{
			if (d.n.enabled())
			{
				g.setColor(Color.DARK_GRAY);
				g.fillOval(d.p.x - nodeSize, d.p.y - nodeSize / 2, nodeSize * 2, nodeSize);
				g.setColor(getTypeColor(d.n.type()));
				g.drawOval(d.p.x - nodeSize, d.p.y - nodeSize / 2, nodeSize * 2, nodeSize);
				g.setColor(Color.WHITE);
				String str = String.format("%2.2f", d.n.value());
				int w = metrics.stringWidth(str) / 2;
				g.drawString(str, d.p.x - w, d.p.y + h - 3);
			}
		});
	}

	private Color getWeightColor(float weight)
	{
		weight = Math.max(Math.min(weight, 1.0f), -1.0f) * 255;
		if (weight < 0)
		{
			return new Color(255, 0, 0, (int) -weight);
		}
		return new Color(0, 255, 0, (int) weight);
	}

	private Color getTypeColor(NeuronType type)
	{
		switch (type)
		{
			case Output:
				return Color.CYAN;
			case Input:
				return Color.MAGENTA;
			case Hidden:
				return Color.WHITE;
		}
		return Color.WHITE;
	}

	Color bg = new Color(64, 64, 64, 64);

	public BufferedImage render(int width, int height)
	{
		if (img == null || img.getWidth() != width || img.getHeight() != height)
		{
			img = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
			needsBaseUpdate = true;
		}

		if (needsBaseUpdate)
		{
			renderBase(width, height);
			needsBaseUpdate = false;
		}
		Graphics2D g = (Graphics2D) img.getGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setColor(bg);
		g.fillRect(0, 0, width - 1, height - 1);

		g.setStroke(new BasicStroke(2f));

		renderValues(g);
		g.setColor(Color.LIGHT_GRAY);
		g.drawRect(0, 0, width - 1, height - 1);
		g.dispose();

		return img;

	}

	private class NodeData
	{
		Point p;
		BasicNeuron n;

	}

}
