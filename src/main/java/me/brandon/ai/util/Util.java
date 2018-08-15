package me.brandon.ai.util;

public class Util
{

	public static double applyChange(double val, double change, double min, double max)
	{
		val += change;
		if (val > max)
		{
			val -= max - val;
		}
		else if (val < min)
		{
			val += val - min;
		}
		return val;
	}

	public static double limit(double val, double min, double max)
	{
		if (val > max)
			return max;
		if (val < min)
			return min;
		return val;
	}
}
