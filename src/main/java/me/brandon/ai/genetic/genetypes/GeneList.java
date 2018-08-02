package me.brandon.ai.genetic.genetypes;

import me.brandon.ai.genetic.Mutator;

import java.util.ArrayList;
import java.util.List;

import static me.brandon.ai.genetic.Genetic.chance;
import static me.brandon.ai.genetic.Genetic.weightedChance;

public class GeneList extends Gene<GeneList>
{
	private List<Gene<? extends Gene>> geneList;

	public GeneList()
	{
		this(1.0f, null);
	}

	public GeneList(float mutRate, Mutator<GeneList> mutator)
	{
		super(mutRate, mutator);
		this.geneList = new ArrayList<>();
	}

	@Override
	public GeneList cross(GeneList other)
	{
		GeneList list = createClone();
		int s1 = geneList.size();
		int s2 = other.geneList.size();

		Gene<?> a = null;
		Gene<?> b = null;
		for (int i = 0, j = 0; i < s1 || j < s2; )
		{
			if (a == null && i < s1)
				a = list.geneList.get(i);
			if (b == null && j < s2)
				b = list.geneList.get(j);

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
	public GeneList generateRandom()
	{
		return new GeneList();
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

	public Gene<?> addGene(Gene<?> gene)
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
	protected void copy(GeneList other)
	{
		geneList = new ArrayList<>();
		other.geneList.forEach(gene -> geneList.add(gene.createClone()));
	}


	public List<Gene<?>> getGeneList()
	{
		return geneList;
	}

}
