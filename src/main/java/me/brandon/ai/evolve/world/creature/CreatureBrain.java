package me.brandon.ai.evolve.world.creature;

import java.awt.*;

import static me.brandon.ai.util.Options.*;

public class CreatureBrain
{

	public static CreatureBrain instance;

	protected static int _brainId;
	protected int brainId = _brainId++;
	protected Creature creature;

	protected int layerCount;
	protected BrainConnection[] connections;
	protected Neuron[] neurons;

	private static String[] neuronDataNames;


//	private static int inputNeuronCount = maxMemory
//			+ 3 * maxFeelers // feelers
//			+ 1 // age
//			+ 1 // energy
//
//			+ 1; // constant
//
//	private static int outputNeuronCount = maxMemory
//			+ 1 // needle length
//			+ 2 // main feeler length and angle
//			+ 2 // movement
//			;

//	private static int hiddenNeuronStartIndex = inputNeuronCount + outputNeuronCount;

	static
	{
		neuronDataNames = new String[inputNodeSize + outputNodeSize];

		// inputs
		int i = 0;
		for (int j = 0; j < maxMemory; j++)
		{
			neuronDataNames[i++] = "Memory " + j;
		}

		for (int j = 0; j < maxFeelers; j++)
		{
			neuronDataNames[i++] = "Feeler " + j + " (h)";
			neuronDataNames[i++] = "Feeler " + j + " (s)";
			neuronDataNames[i++] = "Feeler " + j + " (v)";
		}

		neuronDataNames[i++] = "Age";
		neuronDataNames[i++] = "Energy";


		neuronDataNames[i] = "1";

		// outputs
		i = inputNodeSize;
		for (int j = 0; j < maxMemory; j++)
		{
			neuronDataNames[i++] = "Memory " + j;
		}
		neuronDataNames[i++] = "Feeler Length";
		neuronDataNames[i++] = "Feeler Angle";
		neuronDataNames[i++] = "Needle Length";
		neuronDataNames[i++] = "Impulse";
		neuronDataNames[i++] = "Angular Velocity";
		neuronDataNames[i++] = "Breed Desire";
	}


	public CreatureBrain(Creature creature)
	{
		instance = this;
		this.creature = creature;
	}

	public void mutate()
	{
		for (BrainConnection con : connections)
		{
			if (rand.nextFloat() < 0.25)
				con.weight += (rand.nextFloat() - 0.5f) * 2 * mut_brain_wight_amnt;
		}
	}

	public void renderBrain(Graphics2D g, int width, int height)
	{
		if (neurons == null)
			return;
		try
		{


			g.setColor(Color.DARK_GRAY);
			g.fillRect(0, 0, width, height);

			int nodeSize = 20;
			int nameSize = 0;
			int spacingX = 20;
			int spacingY = 30;
			int[] counts = new int[layerCount];

			FontMetrics metrics = g.getFontMetrics();
			for (int i = 0; i < neurons.length; i++)
			{
				if (neurons[i].enabled)
				{
					counts[neurons[i].layer]++;
					if (neurons[i].type != NeuronType.Hidden)
						nameSize = Math.max(nameSize, metrics.stringWidth(neuronDataNames[i]));
				}

			}
			int maxCount = 0;
			for (int i = 0; i < counts.length; i++)
			{
				maxCount = Math.max(maxCount, counts[i]);
			}
			int renderWidth = width - nameSize * 2 - 5 * spacingX;

			int renderHeight = height;//maxCount * spacingY + spacingY;
			int nodeSpaceX = (renderWidth / (layerCount - 1));


			int posX = spacingX * 2 + nameSize;
			g.setColor(Color.LIGHT_GRAY);
			for (int c = 0; c < layerCount; c++)
			{
				int nodeSpaceY = renderHeight / (counts[c] + 1);
				int posY = nodeSpaceY;
				for (int i = 0; i < neurons.length; i++)
				{
					if (neurons[i].enabled && neurons[i].layer == c)
					{
						g.setColor(Color.WHITE);
						if (c == 0)
						{
							g.drawString(neuronDataNames[i], spacingX, posY + 5);
						}
						else if (neurons[i].type == NeuronType.Output)
						{
							g.drawString(neuronDataNames[i], width - spacingX - nameSize, posY + 5);
						}


						neurons[i].p = new Point(posX, posY);
						posY += nodeSpaceY;
					}
				}

				posX += nodeSpaceX;
			}

			for (BrainConnection con : connections)
			{
				if (con.enable)
				{
					int alpha = (int) Math.min(255, Math.abs(con.weight * 255) + 30);
					Color col = con.weight > 0 ? new Color(0, 255, 0, alpha) : new Color(255, 0, 0, alpha);
					g.setColor(col);
					Point p1 = neurons[con.in].p;
					Point p2 = neurons[con.out].p;

					g.drawLine(p1.x, p1.y, p2.x, p2.y);
				}
			}

			for (Neuron neuron : neurons)
			{
				if (neuron.enabled)
				{
					Point p = neuron.p;

					int alpha = (int) Math.min(255, Math.abs(neuron.value * 255) + 30);
					Color c = new Color(100, 100, 100, alpha);
					g.setColor(c);
					g.fillRect(p.x - nodeSize, p.y - nodeSize, nodeSize * 2, nodeSize * 2);
					g.setColor(Color.WHITE);
					g.drawRect(p.x - nodeSize, p.y - nodeSize, nodeSize * 2, nodeSize * 2);
					String str = String.format("%2.2f", neuron.value);
					int w = metrics.stringWidth(str);
					g.drawString(str, p.x - w / 2, p.y + 5);
				}
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}

	}

	public void setInput()
	{

		// fullCopy memory outputs to inputs
		for (int i = 0; i < creature.numMemory; i++)
		{
			neurons[i].value = neurons[inputNodeSize + i].value;
		}

		int nStart = maxMemory;
		int nEnd;
		int n;

		neurons[inputNodeSize - 1].value = 0.5f;

		Creature.FeatureExtension[] feelers = creature.feelers;
		nEnd = nStart + maxFeelers * 3;
		n = nStart;
		for (int i = 0; i < feelers.length; i++)
		{
			neurons[n++].value = feelers[i].hsv[0];
			neurons[n++].value = feelers[i].hsv[1];
			neurons[n++].value = feelers[i].hsv[2];
		}
		n = nEnd;

		neurons[n++].value = 1.0f / (creature.age + 1);
		neurons[n].value = creature.energy / 10f;

	}

	public void compute()
	{

		Neuron out;
		for (BrainConnection con : connections)
		{

			if (con.enable)
			{
				neurons[con.out].add(neurons[con.in].get() * con.weight);
			}

		}

	}


	public void getOutput()
	{
		int i = inputNodeSize;
		for (int j = 0; j < maxMemory; j++)
		{
			neurons[j].value = neurons[i++].get();
		}

		creature.feelers[0].length = neurons[i++].get();
		creature.feelers[0].angle = neurons[i++].get();
		creature.needleLength = neurons[i++].get();
		creature.impulse = neurons[i++].get();
		creature.angularVelocity = neurons[i++].get();
		creature.desireToBreed = neurons[i++].get();

	}

	public void print()
	{
		for (int i = 0; i < neurons.length; i++)
		{

			if (neurons[i].enabled)
			{
				String name = i < neuronDataNames.length ? neuronDataNames[i] : "Hidden";
				System.out.printf("%d\t%s\t%s\t[layer: %d, in: %d, out: %d]\n", i, neurons[i].type.name(), name,
						neurons[i].layer, neurons[i].inputCount, neurons[i].outputCount);
			}
		}
	}

	public void fin()
	{
		for (int i = 0; i < inputNodeSize; i++)
		{
			neurons[i].type = NeuronType.Input;
		}
		for (int i = 0; i < outputNodeSize; i++)
		{
			neurons[inputNodeSize + i].type = NeuronType.Output;
		}
		int n = 0;
		for (int i = n; i < maxMemory; i++)
		{
			neurons[n++].enabled = i < creature.numMemory;
		}
		for (int i = 0; i < maxFeelers; i++)
		{
			neurons[n++].enabled = i < creature.numFeelers;
			neurons[n++].enabled = i < creature.numFeelers;
			neurons[n++].enabled = i < creature.numFeelers;
		}

		n = inputNodeSize;
		for (int i = 0; i < maxMemory; i++)
		{
			neurons[n++].enabled = i < creature.numMemory;
		}

		for (BrainConnection con : connections)
		{
			con.enable = neurons[con.in].enabled && neurons[con.out].enabled;
		}

	}

	protected static class BrainConnection implements Cloneable
	{
		int in;
		int out;
		float weight;

		boolean enable;

		protected BrainConnection clone()
		{
			BrainConnection con = new BrainConnection();
			con.in = in;
			con.out = out;
			con.weight = weight;

			return con;
		}
	}

	protected static class Neuron
	{
		int layer = -1;
		float value = 0f;
		NeuronType type = NeuronType.Hidden;
		boolean enabled = true;
		boolean computed;
		Point p;

		Neuron()
		{

		}

		Neuron(NeuronType type)
		{
			this.type = type;
		}

		public void add(float val)
		{
			if (computed)
				value = val;
			else
				value += val;
			computed = false;
		}

		public float get()
		{
			if (!computed && type != NeuronType.Input)
			{
				activate();
			}
			computed = true;
			return value;
		}

		void activate()
		{
//			value = value < 0.3 ? -1 : value > 0.3 ? 1 : 0;
		}

		int inputCount;
		int outputCount;
	}

	protected enum NeuronType
	{
		Input, Hidden, Output
	}
}
