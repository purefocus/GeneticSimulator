package me.brandon.ai.gensim.world.creature;

import static me.brandon.ai.util.Options.*;

import java.awt.*;

public class CreatureBrain
{

	private Creature creature;

	private BrainConnection[] connections;
	private Neuron[] neurons;

	private int hiddenNeuronSize;

	private static String[] neuronDataNames;


	private static int inputNeuronCount = maxMemory
			+ 3 * maxFeelers // feelers
			+ 1 // age
			+ 1 // energy

			+ 1; // constant

	private static int outputNeuronCount = maxMemory
			+ 1 // needle length
			+ 2 // main feeler length and angle
			+ 2 // movement
			;

	static
	{
		neuronDataNames = new String[inputNeuronCount + outputNeuronCount];

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
		i = outputNeuronCount;
		for (int j = 0; j < maxMemory; j++)
		{
			neuronDataNames[i++] = "Memory " + j;
		}
		neuronDataNames[i++] = "Feeler Length";
		neuronDataNames[i++] = "Feeler Angle";
		neuronDataNames[i++] = "Needle Length";
		neuronDataNames[i++] = "Impulse";
		neuronDataNames[i++] = "Angular Velocity";
	}


	public CreatureBrain(Creature creature)
	{
		this.creature = creature;
	}

	public void initFromParents(Creature p1, Creature p2)
	{
		if (p2 == null)
		{
			p2 = p1;
		}


		CreatureBrain b1 = p1.brain;
		CreatureBrain b2 = p2.brain;
		init(Math.max(b1.hiddenNeuronSize, b2.hiddenNeuronSize));


	}

	public void init(int hiddenSize)
	{
		this.hiddenNeuronSize = hiddenSize;
		if (rand.nextFloat() <= mut_brain_neuronAddition)
		{
			hiddenSize++;
		}
		else if (hiddenSize > 0 && rand.nextFloat() <= mut_brain_neuronAddition)
		{
			hiddenSize--;
		}

		neurons = new Neuron[inputNeuronCount + outputNeuronCount + hiddenSize];

		int i;

		// create inputs
		for (i = 0; i < inputNeuronCount; i++)
		{
			neurons[i] = new Neuron();
			neurons[i].type = NeuronType.Input;
		}

		// create outputs
		for (; i < inputNeuronCount + outputNeuronCount; i++)
		{
			neurons[i] = new Neuron();
			neurons[i].type = NeuronType.Output;
		}

		// create hidden layer
		for (; i < neurons.length; i++)
		{
			neurons[i] = new Neuron();
			neurons[i].type = NeuronType.Hidden;
		}

	}

	public void renderBrain(Graphics2D g, int width, int height)
	{

	}

	public void setInput()
	{

		// copy memory outputs to inputs
		for (int i = 0; i < creature.numMemory; i++)
		{
			neurons[i].value = neurons[inputNeuronCount + i].value;
		}

		int nStart = maxMemory;
		int nEnd;
		int n;

		Creature.FeatureExtension[] feelers = creature.feelers;
		nEnd = maxFeelers * 3;
		n = nStart;
		for (int i = 0; i < feelers.length; i++)
		{
			neurons[n++].value = feelers[i].hsv[0];
			neurons[n++].value = feelers[i].hsv[1];
			neurons[n++].value = feelers[i].hsv[2];
		}
		n = nEnd;

		neurons[n++].value = creature.age;
		neurons[n++].value = creature.energy;


	}

	public void compute()
	{

		Neuron in;
		Neuron out;
		for (BrainConnection con : connections)
		{
			if (con.enable)
			{
				in = neurons[con.in];
				out = neurons[con.out];

				out.value += in.value * con.weight;

			}
		}

	}

	private void mutateConnection(BrainConnection connection)
	{

	}

	public void getOutput()
	{
		int i = outputNeuronCount;
		for (int j = 0; j < maxMemory; j++)
		{
			neurons[j] = neurons[i++];
		}

		creature.feelers[0].length = neurons[i++].value;
		creature.feelers[0].angle = neurons[i++].value;
		creature.needleLength = neurons[i++].value;
		creature.impulse = neurons[i++].value;
		creature.angularVelocity = neurons[i++].value;
	}

	class BrainConnection
	{
		int in;
		int out;
		float weight;


		boolean enable;
	}

	class Neuron
	{
		float value = 0f;
		NeuronType type = NeuronType.Hidden;
		boolean enabled = true;
		boolean computed;
	}

	enum NeuronType
	{
		Input, Hidden, Output
	}
}
