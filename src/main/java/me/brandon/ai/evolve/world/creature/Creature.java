package me.brandon.ai.evolve.world.creature;

import me.brandon.ai.config.ConfigOption;
import me.brandon.ai.evolve.world.Tile;
import me.brandon.ai.evolve.world.World;
import me.brandon.ai.evolve.world.WorldEntity;
import me.brandon.ai.ui.Viewport;
import me.brandon.ai.util.FastMath;
import me.brandon.ai.util.Options;
import me.brandon.ai.util.Vector;

import java.awt.*;

import static me.brandon.ai.util.ConvertedGraphics.*;
import static me.brandon.ai.util.Options.*;

public class Creature implements WorldEntity
{

	@ConfigOption(option = "creature_min_size")
	public static int minSize = 30;

	public int generation;

	public World world;

	public Vector pos;
	public Vector vel;

	public float direction;

	public float rateOfDeath = 0.001f;

	public float energy;
	public float consumeRate = 0.00001f;
	public float energyUsage;

	// ---------- Brain Outputs ----------
	public float angularVelocity;
	public float impulse;
	public float needleLength;
	public float desireToBreed;
	public float breedability;

	// ---------- Brain Inputs ----------
	public float age;

	// ---------- Chance Traits ----------
	public float remainingLife = 5f;
	public float baseSize = 2;
	public float size = 5;
	public float maxNeedleLength = 10f;
	public float maxSize = 12f;
	public float maxSpeed = 0.25f;
	public FeatureExtension[] feelers;     // input/output
	public FeatureExtension needle;        // input/output
	public float foodType;
	public float foodTypeRange;
	public float speciesHue;
	public Color speciesColor;
	public CreatureBrain brain;
	public int numMemory;
	public int numFeelers;
	public String name;

	public boolean alive;

	public Tile tile;

	public Color foodColor = Color.GREEN;
	public Rectangle bounds;

	public Creature(World world)
	{
		this(world, 0);
	}

	public Creature(World world, int gen)
	{
		this.world = world;
		bounds = new Rectangle();
		this.generation = gen;
		numFeelers = 3;

		name = "Test Name";

		alive = true;

		brain = new CreatureBrain(this);

		needle = new FeatureExtension();

		if (gen == 0)
		{
			initFeelers();
			generateRandom();
		}

	}

	public void fin()
	{
		speciesColor = Color.getHSBColor(speciesHue, 1f, 1f);
		maxSize = baseSize * 2.5f;
		energy = baseSize;
	}

	public void generateRandom()
	{
		BrainFactory brainFactory = BrainFactory.factory();
		brainFactory.generateRandomBrain();

		brain = brainFactory.generate(this);

		pos = Vector.vector(
				rand.nextFloat() * World.worldWidth * World.tileSize,
				rand.nextFloat() * World.worldHeight * World.tileSize
		);

		Tile tile = world.getTileFromPosition(pos.x, pos.y);
		foodType = tile.getFoodType();

		speciesHue = rand.nextFloat();

		feelers[0].length = rand.nextFloat() * size * mut_amount;
		feelers[0].angle = 0;
		feelers[1].angle = 20;
		feelers[2].angle = -20;

		feelers[1].length = feelers[2].length = size * 2;

		vel = Vector.vector(0, 0);
		direction = 0;

		needle = new FeatureExtension();
		needle.angle = 0;
		needle.length = size;


		angularVelocity = 1f;

		fin();
	}

	public void eat(float food, float amount)
	{
		food = Math.abs(foodType - food) - foodTypeRange;
		energy += food * amount;
	}

	public void updateSensors()
	{
//		needle.angle = direction;
		needle.compute();


		for (FeatureExtension feeler : feelers)
		{
			feeler.compute();
		}
	}

	int mutBrain = 0;

	public void calculateBrain()
	{
		brain.setInput();
		brain.compute();
		brain.getOutput();

		if (mutBrain-- <= 0)
		{
			mutBrain = rand.nextInt(20);
			brain.mutate();
		}

		desireToBreed = Math.abs(desireToBreed);

		impulse = limit(-maxSpeed, maxSpeed, impulse * maxSize);
		needleLength = limit(0, 1, needleLength);
		needle.length = needleLength * maxNeedleLength;
		feelers[0].length = limit(0, 1, feelers[0].length) * maxSize;
		angularVelocity *= 10;
	}


	public boolean atPosition(double px, double py)
	{
		return bounds.contains(px, py);
	}

	public boolean isAlive()
	{
		return alive;
	}

	@Override
	public void tick(int time)
	{
		if (alive)
		{
			age += 0.01;
			if (remainingLife <= 0)
			{
				die();
			}
			energyUsage = size * size_energy_consumption;
			energyUsage += Math.abs(impulse) * impulse_energy_consumption;


			direction += limit(-0.5f, 0.5f, angularVelocity);

			while (direction > 360)
			{
				direction -= 360;
			}

			while (direction < 0)
			{
				direction += 360;
			}

			vel = Vector.fromAngle(direction, Math.min(Math.max(impulse, -maxSpeed), maxSpeed));

			pos.add(vel);
			consumeRate = (size) / 850f;

			if(impulse < 0)
			{
				energyUsage *= 1.5;
			}


			size = energy;
			if (size > maxSize)
			{
				consumeRate /= 3;
			}
//			energyUsage -= (baseSize - size) * 0.025;

			rateOfDeath = rod_energy / Math.abs(energy);
			remainingLife -= rateOfDeath;
			tile = world.getTileFromPosition(pos.x, pos.y);
			if (tile != null)
			{
				if (tile.isWater())
				{
					energyUsage *= 16f;
				}
				else
				{

					float affinity = 1f - Math.abs(foodType - tile.getFoodType()) * 0.2f;
					float consumed = tile.consume(consumeRate) * affinity * 5f;
					energy += consumed * affinity;
				}

			}
			else
			{
				die();
			}

			energy -= energyUsage;

			if (energy <= 0)
			{
				remainingLife -= Math.abs(energy) * rod_neg_energy;
			}
			else if (energy <= baseSize)
			{
				remainingLife -= Math.abs(energy) * rod_neg_energy / 100f;
			}


			bounds.x = (int) (pos.x - size);
			bounds.y = (int) (pos.y - size);
			bounds.width = (int) (2 * size);
			bounds.height = (int) (2 * size);

			if (desireToBreed > 0.25f && age > 0.25 && size > baseSize * 2 && rand.nextFloat() < 0.2)
			{
				energy -= baseSize;
				if (energy > 0)
				{
					Creature c = GeneticBreeder.breed(this);
					c.energy = c.baseSize;

					world.addCreature(c);
				}
			}
		}
	}

	private float limit(float min, float max, float val)
	{
		return Math.min(max, Math.max(min, val));
	}

	private void die()
	{
		alive = false;
	}


	@Override
	public void draw(Graphics2D g, Viewport view)
	{
		try
		{

			g.setColor(Color.RED);
			drawLine(g, pos.x, pos.y, needle.endPos.x, needle.endPos.y, view);

			g.setColor(speciesColor);
			fillCircle(g, pos.x, pos.y, size, view);

			g.setColor(Color.BLACK);
			drawCircle(g, pos.x, pos.y, baseSize, view);

			g.setColor(foodColor);
			fillCircle(g, pos.x, pos.y, baseSize / 2, view);

			if (Options.showCreatureFeelers)
			{
				for (FeatureExtension feeler : feelers)
				{
					g.setColor(Color.BLUE);
					drawLine(g, pos.x, pos.y, feeler.endPos.x, feeler.endPos.y, view);
				}
			}

			if (Options.showCreatureBounds)
			{
				g.setColor(Color.WHITE);
				drawRect(g, bounds.x, bounds.y, bounds.width, bounds.height, view);
			}
		} catch (Exception e)
		{

		}

	}

	public Rectangle getBounds()
	{
		return bounds;
	}

	public Color getSpeciesColor()
	{
		return speciesColor;
	}

	public void initFeelers()
	{
		feelers = new FeatureExtension[numFeelers];
		for (int i = 0; i < numFeelers; i++)
		{
			feelers[i] = new FeatureExtension();
		}
	}


	public class FeatureExtension
	{
		Vector endPos;
		float length;
		float angle;

		float[] hsv = new float[3];

		private void compute()
		{
			float ang = angle + direction;
			endPos = Vector.vector(FastMath.cos(ang), FastMath.sin(ang));
			endPos.scale(size + length);
			endPos.add(pos.x, pos.y);

			Color c = world.getColorAt(endPos.x, endPos.y);
			Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), hsv);
		}

		public boolean intersects(Creature creature)
		{
			return creature.bounds.intersectsLine(pos.x, pos.y, endPos.x, endPos.y);
		}
	}


}
