package me.brandon.ai.api.neural;

import java.awt.*;

public interface Network
{

	void setInput(float... inputs);

	void compute();

	void reset();

	float[] getOutput();

	float[] getValues(int layer);

	int getLayerCount();

	float[][] getAllValues();

	void renderNetwork(Graphics g, int width, int height);

}
