package me.brandon.ai.api.genetic.genetypes;

import me.brandon.ai.api.genetic.Mutator;

import java.util.ArrayList;
import java.util.List;

import static me.brandon.ai.util.Chance.chance;
import static me.brandon.ai.util.Chance.weightedChance;

public class GeneList<T extends Gene<T>> extends Gene<GeneList<T>>
{
	private List<T> geneList;

	public GeneList()
	{
		this(1.0f, null);
	}

	public GeneList(float mutRate, Mutator<GeneList<T>> mutator)
	{
		super(mutRate, mutator);
		this.geneList = new ArrayList<>();
	}

	@Override
	public GeneList<T> cross(GeneList<T> other)
	{
		int s1 = geneList.size();
		int s2 = other.geneList.size();

		T a = null;
		T b = null;
		for (int i = 0, j = 0; i < s1 || j < s2; )
		{
			if (a == null && i < s1)
				a = geneList.get(i);
			if (b == null && j < s2)
				b = geneList.get(j);

			if (a != null && b != null)
			{
				if (a.matches(b))
				{
					a.doCross(b);
					if (a.dormant || b.dormant)
					{
						if (a.dormant && b.dormant)
						{
							a.dormant = chance(dominance);
						}
						else if (a.dormant)
						{
							a.dormant = weightedChance(dominance, b.dominance);
						}
						else
						{
							a.dormant = weightedChance(b.dominance, dominance);
						}
					}
					a = null;
					b = null;
					i++;
					j++;
				}
				else if (a.getIdentifier() < b.getIdentifier())
				{
					i++;
					a.dominance *= 0.75f;

					if (a.dormant)
					{
						a.dormant = weightedChance(dominance, other.dominance);
					}

					a = null;
				}
				else if (a.getIdentifier() > b.getIdentifier())
				{
					j++;
					b = addGene(b.createClone());
					b.dominance *= 0.75f;

					if (b.dormant)
					{
						b.dormant = weightedChance(dominance, other.dominance);
					}
					b = null;
				}
			}
		}

		return this;
	}

	@Override
	public GeneList<T> generateRandom()
	{
		GeneList<T> list = new GeneList<>();
		for (Gene<T> gene : geneList)
		{
			list.addGene(gene.generateRandom());
		}
		return list;
	}

	@Override
	public float geneticDistance(GeneList other)
	{
		float dist = Math.abs(other.geneList.size() - geneList.size());
		for (int i = 0, j = 0; i < geneList.size(); i++)
		{
			Gene<?> a = geneList.get(i);
			Gene<?> b = geneList.get(j);
			if (a.matches(b))
			{
				dist += a.calcGeneticDistance(b);
				j++;
			}
		}
		return dist;
	}

	@Override
	public void mutate()
	{
		geneList.forEach(Gene::attemptMutate);
	}

	public T addGene(T gene)
	{
		geneList.add(gene);
		return gene;
	}

	public void removeGene(Gene<?> gene)
	{
		geneList.remove(gene);
	}

	public boolean hasGene(Gene<?> gene)
	{
		return geneList.contains(gene);
	}

	@Override
	protected void copy(GeneList<T> other)
	{
		geneList = new ArrayList<>();
		other.geneList.forEach(gene -> geneList.add(gene.createClone()));
	}

	@Override
	public Object get()
	{
		return geneList;
	}
}
