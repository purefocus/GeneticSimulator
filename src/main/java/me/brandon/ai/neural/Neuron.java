package me.brandon.ai.neural;

public interface Neuron
{

	float compute();

	float value();

	void reset();

	boolean enabled();

	void setValue(float value);

	NeuronType type();

}
