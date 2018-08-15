package me.brandon.ai.genetic.genetypes;

import static me.brandon.ai.genetic.Chance.randVal;
import static me.brandon.ai.genetic.Chance.weightedChance;

public class NameGene extends Gene<NameGene>
{

	private static char[] chars = "abcdefghijklmnopqrstuvwxyz".toCharArray();

	private int nameLength;
	private String name;

	public NameGene(int nameLength)
	{
		super(0.05f);
		this.nameLength = nameLength;
	}

	@Override
	public NameGene cross(NameGene other)
	{
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < name.length(); i++)
		{
			sb.append(weightedChance(dominance, other.dominance) ? name.charAt(i) : other.name.charAt(i));
		}

		this.name = sb.toString();
		return this;
	}

	@Override
	public NameGene generateRandom()
	{
		NameGene gene = new NameGene(nameLength);
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < name.length(); i++)
		{
			sb.append(chars[(int) randVal(0, chars.length)]);
		}

		gene.name = sb.toString();
		return gene;
	}

	@Override
	public float geneticDistance(NameGene other) // measure how many characters are different between the two genes
	{
		int dif = Math.abs(nameLength - other.nameLength);
		int s = Math.min(nameLength, other.nameLength);
		for (int i = 0; i < s; i++)
		{
			if (name.charAt(i) != other.name.charAt(i))
				dif++;
		}
		return dif;
	}

	@Override
	public void mutate()
	{
		char[] narr = name.toCharArray();
		narr[(int) randVal(0, narr.length)] = chars[(int) randVal(0, chars.length)];
	}

	@Override
	protected void copy(NameGene other)
	{
		this.name = other.name;
		this.nameLength = other.nameLength;
	}

	@Override
	public Object get()
	{
		return name;
	}
}
