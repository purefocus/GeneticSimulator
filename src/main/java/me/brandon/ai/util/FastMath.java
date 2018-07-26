package me.brandon.ai.util;

public class FastMath
{
	private static final int ATAN2_BITS = 7;

	private static final int ATAN2_BITS2 = ATAN2_BITS << 1;
	private static final int ATAN2_MASK = ~(-1 << ATAN2_BITS2);
	private static final int ATAN2_COUNT = ATAN2_MASK + 1;
	private static final int ATAN2_DIM = (int) Math.sqrt(ATAN2_COUNT);

	private static final float INV_ATAN2_DIM_MINUS_1 = 1.0f / (ATAN2_DIM - 1);
	private static final float DEG = 180.0f / (float) Math.PI;

	private static final float[] atan2 = new float[ATAN2_COUNT];

	private static final int precision = 2;
	private static final int modulus = 360 * precision;

	private static final float[] sinTable = new float[modulus];

	static
	{
		for (int i = 0; i < sinTable.length; i++)
			sinTable[i] = (float) Math.sin((i * Math.PI) / (precision * 180));

		for (int i = 0; i < ATAN2_DIM; i++)
		{
			for (int j = 0; j < ATAN2_DIM; j++)
			{
				float x0 = (float) i / ATAN2_DIM;
				float y0 = (float) j / ATAN2_DIM;

				atan2[j * ATAN2_DIM + i] = (float) Math.atan2(y0, x0);
			}
		}
	}

	private static float sinLookup(int angle)
	{
		return angle >= 0 ? sinTable[angle % modulus] : -sinTable[angle % modulus];
	}

	public static float sin(float angle)
	{
		return sinLookup((int) (angle * precision + 0.5f));
	}

	public static float cos(float angle)
	{
		return sin(angle + 90f);
	}


	/**
	 * ATAN2
	 */

	public static final float atan2Deg(float y, float x)
	{
		return FastMath.atan2(y, x) * DEG;
	}

	public static final float atan2DegStrict(float y, float x)
	{
		return (float) Math.atan2(y, x) * DEG;
	}

	public static final float atan2(float y, float x)
	{
		float add, mul;

		if (x < 0.0f)
		{
			if (y < 0.0f)
			{
				x = -x;
				y = -y;

				mul = 1.0f;
			}
			else
			{
				x = -x;
				mul = -1.0f;
			}

			add = -3.141592653f;
		}
		else
		{
			if (y < 0.0f)
			{
				y = -y;
				mul = -1.0f;
			}
			else
			{
				mul = 1.0f;
			}

			add = 0.0f;
		}

		float invDiv = 1.0f / (((x < y) ? y : x) * INV_ATAN2_DIM_MINUS_1);

		int xi = (int) (x * invDiv);
		int yi = (int) (y * invDiv);

		return (atan2[yi * ATAN2_DIM + xi] + add) * mul;
	}


}
