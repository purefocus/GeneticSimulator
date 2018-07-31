package me.brandon.ai.neural;

public interface Connection
{

	float weight();

	Neuron source();

	float compute();

	boolean enabled();

}
