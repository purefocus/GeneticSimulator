package me.brandon.ai.genetic;

import me.brandon.ai.genetic.genetypes.Gene;
import me.brandon.ai.genetic.genetypes.GeneList;
import me.brandon.ai.genetic.genetypes.NumericGene;

import java.util.ArrayList;
import java.util.List;

public class GeneticTest
{
	private static char possibleValues[] = " abcdefghijklmnopqrstuvwxyz".toLowerCase().toCharArray();
	private static char testStr[] = "hello this is a test string".toLowerCase().toCharArray();
	private static int maxFit = (possibleValues.length + 10) * testStr.length;

	public static void main(String[] args)
	{

		float mutRate = 0.15f;
		float mutAmount = 3f;
		float max = 26f;
		float min = 0f;
		GenomeFactory<NumericGene> f = GenomeFactory.newGenome();
		for (int i = 0; i < testStr.length; i++)
		{
			f.addGene(new NumericGene(mutRate, mutAmount, max, min));
		}

		GeneList template = f.build();

		int sampleSize = 5;

		List<Ent> list = new ArrayList<>(sampleSize);
		for (int i = 0; i < sampleSize; i++)
		{
			Ent ent = new Ent();
			ent.list = template.generateRandom();
			list.add(ent);
		}

		int generation = 0;
		int maxGen = 1000;
		float fit = test(list, generation++);

		while (fit < maxFit && generation < maxGen)
		{

			Ent e1 = list.get(0);
			Ent e2 = list.get(1);
			list.clear();
			list.add(e1);
			list.add(e2);

			for (int i = 2; i < sampleSize; i++)
			{
				Ent ent = new Ent();
				ent.list = (GeneList) e1.list.makeChild(e2.list);
				ent.list.attemptMutate();
				list.add(ent);
			}

			fit = test(list, generation++);
		}

		System.out.println("Done!");
		if (generation == maxGen)
			System.out.println("Failure!");
		else
			printGenome(list.get(0));

	}

	public static float test(List<Ent> list, int generation)
	{
		System.out.println("\nGeneration: " + generation);
		float maxFit = 0;
		for (Ent ent : list)
		{
			ent.fit = testGenome(ent.list);
			maxFit = Math.max(maxFit, ent.fit);
		}
		list.sort((e1, e2) -> (int) (e2.fit - e1.fit));
		printGenome(list.get(0));

		return maxFit;
	}

	public static class Ent
	{
		public GeneList list;
		public float fit;
	}

	public static void printGenome(Ent ent)
	{
		List<Gene<?>> genes = (List<Gene<?>>) ent.list.get();
		System.out.print("\t");
		for (int i = 0; i < genes.size(); i++)
		{
			int v = (int) (((NumericGene) genes.get(i)).getValue());
			char c = possibleValues[v];
			System.out.printf("%c", c);
		}
		System.out.printf("\t\t\t fitness: %2.5f\n", ent.fit / maxFit);
	}

	public static float testGenome(GeneList list)
	{
		List<Gene<?>> genes = (List<Gene<?>>) list.get();
		float fitness = 0;
//		StringBuilder s = new StringBuilder();
//		System.out.print("\t");
		for (int i = 0; i < genes.size(); i++)
		{
			int v = (int) (((NumericGene) genes.get(i)).getValue());

//			s.append(possibleValues[v]);

			fitness += possibleValues.length - Math.abs(testStr[i] - possibleValues[v]);
			if (possibleValues[v] == testStr[i])
			{
				fitness += 10;
			}

		}
//		System.out.printf("\t%s, fitness = %2.0f\n", s.toString(), fitness);
		return fitness;
	}
}
