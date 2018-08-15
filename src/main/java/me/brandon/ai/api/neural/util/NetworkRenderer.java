package me.brandon.ai.api.neural.util;

import me.brandon.ai.api.neural.NeuronType;
import me.brandon.ai.api.neural.impl.NConnection;
import me.brandon.ai.api.neural.impl.NNetwork;
import me.brandon.ai.api.neural.impl.NNeuron;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class NetworkRenderer
{

	private final NNetwork network;

	private NodeData data[][];
	private Map<Integer, NodeData> dataMap;

	private BufferedImage img;
	private boolean needsBaseUpdate;
	private boolean dataChanged;

	public NetworkRenderer(NNetwork network)
	{
		this.network = network;
		this.dataMap = new HashMap<>();
		int layerCount = network.getLayerCount();
		data = new NodeData[layerCount][];

		for (int i = 0; i < layerCount; i++)
		{
			NNeuron layer[] = network.getLayer(i);
			data[i] = new NodeData[layer.length];
			for (int j = 0; j < layer.length; j++)
			{
				data[i][j] = new NodeData();
				data[i][j].n = layer[j];
				dataMap.put(layer[j].getId(), data[i][j]);
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
			NNeuron layer[] = network.getLayer(i);
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
				g.setColor(getWeightColor(con.weight()));
				NodeData d = dataMap.get(((NNeuron) con.source()).getId());
				if (d != null)
				{
					g.drawLine(data.p.x, data.p.y, d.p.x, d.p.y);
				}
			}
		});
		int nodeSize = 20;
		FontMetrics metrics = g.getFontMetrics();
		int h = metrics.getHeight() / 2;
		dataMap.values().forEach(d ->
		{
			g.setColor(Color.DARK_GRAY);
			g.fillOval(d.p.x - nodeSize, d.p.y - nodeSize / 2, nodeSize * 2, nodeSize);
			g.setColor(getTypeColor(d.n.type()));
			g.drawOval(d.p.x - nodeSize, d.p.y - nodeSize / 2, nodeSize * 2, nodeSize);
			g.setColor(Color.WHITE);
			String str = String.format("%2.2f", d.n.value());
			int w = metrics.stringWidth(str) / 2;
			g.drawString(str, d.p.x - w, d.p.y + h - 3);
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

	public BufferedImage render(int width, int height)
	{
		if (img == null || img.getWidth() != width || img.getHeight() != height)
		{
			img = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
			needsBaseUpdate = true;
		}

		if (needsBaseUpdate)
		{
			renderBase(width, height);
			needsBaseUpdate = false;
		}
		Graphics2D g = (Graphics2D) img.getGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setColor(Color.DARK_GRAY);
		g.fillRect(0, 0, width, height);

		g.setStroke(new BasicStroke(2f));

		renderValues(g);
		g.dispose();

		return img;

	}

	private class NodeData
	{
		Point p;
		NNeuron n;

	}

}
