package me.brandon.ai.sim.asteroids;

import me.brandon.ai.api.genetic.GeneticEntity;
import me.brandon.ai.api.neural.Network;
import me.brandon.ai.api.neural.NetworkFactory;
import me.brandon.ai.api.neural.NeuralEntity;
import me.brandon.ai.api.neural.impl.NNetwork;
import me.brandon.ai.ui.renderer.Renderer;
import me.brandon.ai.util.GraphicsWrapper;
import me.brandon.ai.util.Vector;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static me.brandon.ai.sim.asteroids.AsteroidsGame.windowHeight;
import static me.brandon.ai.sim.asteroids.AsteroidsGame.windowWidth;
import static me.brandon.ai.util.Chance.randVal;

public class AsteroidsPlayer extends GeneticEntity<AsteroidsPlayer> implements Renderer, NeuralEntity
{

	Random rand = new Random(10);
	private static final int size = 30;
	private static final double a_spread = Math.toRadians(140);


	protected Vector pos;
	protected Vector vel;
	protected Vector acc;
	protected int shootCount;

	protected int score;

	private static final int visionLength = 20;
	private static final double angSpread = Math.toRadians(45);

	protected float angle;
	protected float spin;
	protected float fwdSpeed;
	protected float maxSpeed = 0.5f;
	protected boolean boosting;
	protected int boostFlash;
	protected boolean shooting;

	protected Network brain;

	protected Vector[] visionVectors;
	protected float[] inputs;
	protected float[] decision;

	protected int lifespan;
	protected boolean canShoot;

	protected List<Asteroid> asteroids;
	protected List<Bullet> bullets;

	public AsteroidsPlayer()
	{
		reset();
	}

	@Override
	public void reset()
	{
		pos = Vector.zero();
		vel = Vector.zero();
		acc = Vector.zero();
		boosting = false;
		boostFlash = 0;
		shooting = false;
		fitness = 0;
		score = 0;
		angle = 0;
		fwdSpeed = 0;
		shootCount = 0;
		dead = false;

		visionVectors = new Vector[8];
		inputs = new float[9];
		decision = new float[5];
		angle = 0;
		spin = 0;

		asteroids = new ArrayList<>();
		asteroids.add(new Asteroid(this, (double) windowWidth, rand.nextFloat() * (windowHeight), (double) rand.nextFloat(), (double) rand.nextFloat(), 3));
		asteroids.add(new Asteroid(this, (double) rand.nextFloat() * (windowWidth), windowHeight, (double) rand.nextFloat(), (double) rand.nextFloat(), 3));
		asteroids.add(new Asteroid(this, (double) -windowWidth, rand.nextFloat() * (windowHeight), (double) rand.nextFloat(), (double) rand.nextFloat(), 3));
		asteroids.add(new Asteroid(this, (double) rand.nextFloat() * (windowWidth), windowHeight, (double) rand.nextFloat(), (double) rand.nextFloat(), 3));

		float randX = windowWidth;
		float randY = rand.nextFloat() * (windowHeight);
		asteroids.add(new Asteroid(this, randX, randY, pos.x - randX, pos.y - randY, 3));

		bullets = new ArrayList<>();
	}

	public void initNetwork()
	{
		brain = NetworkFactory.createFactory()
				.input(inputs.length)
				.layer(4)
				.layer(3)
				.output(decision.length)
//				.fullyConnect(0, 1)
//				.fullyConnect(1, 2)
//				.fullyConnect(0, 2)
//				.weightRandom(-0.2f, 0.2f)
//				.disableRandom(0.9f)
				.addRandomConnectionPath(0, 4, 3)
				.build();
	}

	private void think()
	{

		inputs[8] = decision[4];

		brain.setInput(inputs);
		brain.compute();
		decision = brain.getOutput();

		shooting = decision[0] > 0 && decision[0] < 1;

		if (decision[1] > 0.2)
			fwdSpeed += 0.5;
		else if (decision[1] < -0.5)
			fwdSpeed -= 0.5;

		if (decision[2] > 1)
			spin += 0.1;
		else if (decision[2] < -1)
			spin -= 0.1;

		boosting = decision[3] > 1f;

	}

	public void doVision()
	{
		double ang = angle;

		for (int i = 0; i < visionVectors.length; i++)
		{
			Vector v = Vector.fromAngle(ang).scale(8);
			Vector p = pos.clone().add(v);
			int j = 1;
			for (; j < visionLength && collidesWithAsteroid(p) == null; j++)
			{
				p.add(v);
				loopy(p);
			}
			visionVectors[i] = p.clone();

			inputs[i] = (float) (j / 20.0);

			ang += angSpread;
		}
	}

	private void shoot()
	{
//		System.out.println("Shoot");
		Vector bv = Vector.fromAngle(angle, 1f).add(vel).scale(3);
		bullets.add(new Bullet(this, pos.x, pos.y, bv.x, bv.y));
		fitness -= 3;
	}

	@Override
	public boolean tick()
	{

		if (!dead)
		{
			fitness += 0.1f;
			doVision();
			think();

			if (boosting)
			{
				maxSpeed = 0.4f;
				fwdSpeed += 0.05f;

			}
			else
			{
				maxSpeed = 0.1f;
			}

			if (fwdSpeed > maxSpeed)
			{
				fwdSpeed = maxSpeed;
			}
			else if (fwdSpeed < 0)
			{
				fwdSpeed = 0;
			}
			acc = Vector.fromAngle(angle, fwdSpeed);

			vel.scale(0.90);
			vel.add(acc);
			pos.add(vel);
			angle += spin;
			spin = 0;

			if (shootCount <= 0)
			{
				if (shooting)
				{
					shootCount = 30;
					shoot();
				}
			}
			else
			{
				shootCount--;
			}

			loopy(pos);


			asteroids.forEach(Asteroid::tick);
			bullets.forEach(Bullet::tick);
			bullets.removeIf(b -> b.dead);
			asteroids.removeIf(b -> b.dead);

			if (asteroids.size() < 10 && rand.nextFloat() < 0.005f)
			{
				asteroids.add(new Asteroid(this,
						(double) randVal(windowWidth), 0,
						(double) randVal(), (double) randVal(), 3));
			}

		}
		return !isDead();
	}

	public void onScore()
	{
		score++;
		fitness += 10f;
	}

	public void onColision()
	{
		dead = true;
	}

	private void loopy(Vector v)
	{
		if (v.y > windowHeight)
			v.y -= windowHeight * 2;
		else if (pos.y < -windowHeight)
			v.y += windowHeight * 2;
		if (v.x > windowWidth)
			v.x -= windowWidth * 2;
		else if (v.x < -windowWidth)
			v.x += windowWidth * 2;
	}

	private Asteroid collidesWithAsteroid(Vector v)
	{
		for (Asteroid asteroid : asteroids)
		{
			if (asteroid.containsVector(v))
			{
				return asteroid;
			}
		}
		return null;
	}

	@Override
	public AsteroidsPlayer makeChild(AsteroidsPlayer par)
	{
		AsteroidsPlayer child = new AsteroidsPlayer();
		child.brain = ((NNetwork) brain).makeChild((NNetwork) par.brain);
		return child;
	}


	@Override
	public void draw(GraphicsWrapper g, int w, int h)
	{
		g.setAntialias();
		g.setColor(Color.DARK_GRAY);
		g.fillBackground(w, h);
		g.setColor(Color.BLACK);
		g.drawRect(-w - 1, -h - 1, w * 2 + 1, h * 2 + 1);

		if (boosting)
		{
//			g.setColor(boostFlash++ > 5 ? Color.RED : Color.WHITE);
			g.setColor(Color.RED);
//			boostFlash %= 10;
		}
		else
		{
			g.setColor(Color.WHITE);
		}

		g.drawPolygon(createPlayer());

		for (Asteroid asteroid : asteroids)
		{
			asteroid.draw(g);
		}

		g.setColor(Color.BLACK);
		for (Bullet bullet : bullets)
		{
			bullet.draw(g);
		}

		if (AsteroidsGame.renderVision)
		{
			g.setColor(Color.LIGHT_GRAY);
			for (int i = 0; i < visionVectors.length; i++)
			{
				if (inputs[i] < 1 && visionVectors[i] != null)
				{
					g.drawLine(pos, visionVectors[i]);
				}
			}
		}
	}

	@Override
	public void firstGeneration()
	{
		initNetwork();
	}

	private Polygon createPlayer()
	{
		Polygon poly = new Polygon();

		poly.addPoint((int) (pos.x + size * Math.cos(angle)), (int) (pos.y + size * Math.sin(angle)));
		poly.addPoint((int) (pos.x + size * Math.cos(angle + a_spread)), (int) (pos.y + size * Math.sin(angle + a_spread)));
		poly.addPoint((int) (pos.x - size * Math.cos(angle) / 3), (int) (pos.y - size * Math.sin(angle) / 3));
		poly.addPoint((int) (pos.x + size * Math.cos(angle - a_spread)), (int) (pos.y + size * Math.sin(angle - a_spread)));


		return poly;
	}

	@Override
	public Network getNeuralNetwork()
	{
		return brain;
	}
}
