package me.brandon.ai.genetic;

import me.brandon.ai.genetic.genetypes.Gene;
import me.brandon.ai.genetic.genetypes.GeneList;

import java.util.List;

public class GenomeFactory<T extends Gene<T>>
{

	public static <T extends Gene<T>> GenomeFactory<T> newGenome()
	{
		return new GenomeFactory<T>();
	}

	private GeneList<T> geneList = new GeneList<>();

	public GenomeFactory addGene(T gene)
	{
		geneList.addGene(gene);
		return this;
	}

	public GeneList<T> build()
	{
		return geneList;
	}

}
