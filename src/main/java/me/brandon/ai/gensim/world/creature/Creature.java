package me.brandon.ai.gensim.world.creature;

import me.brandon.ai.config.ConfigOption;
import me.brandon.ai.gensim.world.World;
import me.brandon.ai.gensim.world.WorldEntity;
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

	World world;

	Vector pos;
	Vector vel;

	float direction;

	float rateOfDeath;

	float energy;
	float consumeRate;

	// ---------- Brain Outputs ----------
	float angularVelocity;
	float impulse;
	float needleLength;
	float desireToBreed;
	float breedability;

	// ---------- Brain Inputs ----------
	float age;

	// ---------- Genetic Traits ----------
	float remainingLife = 100f;
	float size = 5;
	float maxNeedleLength = size * 2.5f;
	float maxSize = 12f;
	FeatureExtension[] feelers;     // input/output
	FeatureExtension needle;        // input/output
	float editableFoodType;
	Color speciesColor;
	CreatureBrain brain;
	int numMemory;
	String name;

	boolean alive;

	private Color foodColor = Color.GREEN;
	private Rectangle bounds;

	public Creature(World world)
	{
		this.world = world;
		bounds = new Rectangle();

		brain = new CreatureBrain(this);
	}

	public void birth(Creature parent)
	{

		if (parent != null)
		{
			pos = parent.pos.clone().add(parent.size + size, parent.size + size);


		}
		else
		{
			pos = Vector.vector(Math.random() * World.worldWidth * World.tileSize, Math.random() * World.worldWidth * World.tileSize);
		}
		vel = Vector.vector(0, 0);
		direction = 0;


		needle = new FeatureExtension();
		needle.angle = direction;


		needle.length = size;
		needle.compute();

		angularVelocity = 1f;

	}

	public void updateSensors()
	{
		needle.angle = direction;
		needle.length = 40;
		needle.compute();
	}

	public void calculateBrain()
	{
		brain.setInput();
		brain.compute();
		brain.getOutput();
	}


	public boolean atPosition(double px, double py)
	{
		return false;
	}

	public boolean isAlive()
	{
		return alive;
	}

	@Override
	public void tick(int time)
	{
		remainingLife -= rateOfDeath;


		direction += angularVelocity;
		vel = Vector.fromAngle(direction, impulse);
		needle.length = Math.min(0, Math.max(1, needleLength)) * maxNeedleLength;

		pos.add(vel);

		bounds.x = (int) (pos.x - size);
		bounds.y = (int) (pos.y - size);
		bounds.width = (int) (2 * size);
		bounds.height = (int) (2 * size);
	}


	@Override
	public void draw(Graphics2D g, Viewport view)
	{

		System.out.println("Draw " + world.getTime());

		g.setColor(Color.RED);
		drawLine(g, pos.x, pos.y, needle.endPos.x, needle.endPos.y, view);

		g.setColor(speciesColor);
		fillCircle(g, pos.x, pos.y, size, view);

		g.setColor(Color.BLACK);
		drawCircle(g, pos.x, pos.y, size, view);

		g.setColor(foodColor);
		fillCircle(g, pos.x, pos.y, size / 2, view);

		if (Options.showCreatureFeelers)
		{
			for (FeatureExtension feeler : feelers)
			{

			}
		}

		if (Options.showCreatureBounds)
		{
			g.setColor(Color.WHITE);
			drawRect(g, bounds.x, bounds.y, bounds.width, bounds.height, view);
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
		for (int i = 0; i < feelers.length; i++)
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
			endPos.scale(length);
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
