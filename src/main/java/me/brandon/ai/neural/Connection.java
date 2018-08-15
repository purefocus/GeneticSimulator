package me.brandon.ai.neural;

public interface Connection
{

	float weight();

	<T extends Neuron> T source();

	float compute();

	boolean enabled();

}
