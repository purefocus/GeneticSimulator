package me.brandon.ai.sim.balance;

import me.brandon.ai.util.Chance;
import me.brandon.ai.api.genetic.GeneticEntity;
import me.brandon.ai.api.neural.Network;
import me.brandon.ai.api.neural.NetworkFactory;
import me.brandon.ai.api.neural.impl.NNetwork;
import me.brandon.ai.util.GraphicUtils;
import me.brandon.ai.util.Util;
import me.brandon.ai.util.Vector;

import java.awt.*;

public class BalanceCreature extends GeneticEntity<BalanceCreature>
{

	private float angularMomentum;

	private static final float gravity = -0.035f;

	private Network network;

	private float mem1 = 0;
	private float mem2 = 0;

	private Vector lastPos;

	private VecSystem system;
	private VecSystem mass;
	private VecSystem counterWeight;
	private VecSystem platformEnd1;
	private VecSystem platformEnd2;

	private VecSystem randomMotion;

	private boolean onPlatform = false;
	private int ticks;
//	private int lastOnPlatform;

	private float ballMass;
	private Vector ball;
	private Vector ballVel;
	private Vector cGrav;

	private float counterAngularVelocity;
	private float platformAngularVelocity;

	public float fitness = 0;

	/*
	nn inputs:
	- center gravity x
	- ball dist from center

	nn outputs:
	- counterweight angular vel
	- platform angular vel
	 */

	public BalanceCreature()
	{
		reset();
	}


	@Override
	public void reset()
	{
		float platAngle = 90;
		float platLen = 30;
		float platMass = 0.5f;
		counterAngularVelocity = 0;
		platformAngularVelocity = 0;
		cGrav = Vector.zero();
		dead = false;
		ticks = 0;
		fitness = 0;
		angularMomentum = 0;
		ballMass = 10;
//		onPlatform = false;

		randomMotion = new VecSystem(20f, -160, 20);
		counterWeight = new VecSystem(20f, -180, 80);
		platformEnd1 = new VecSystem(platMass, platAngle, platLen);
		platformEnd2 = new VecSystem(platMass, platAngle, -platLen);
		mass = new VecSystem(2f, 0, 100, counterWeight, platformEnd1, platformEnd2, randomMotion);
		system = new VecSystem(250, 400, mass);
		system.angle = (float) Math.toRadians(90);

		system.calc();
		lastPos = system.cPos;

		ball = mass.pos.clone().add(0, gravity);
		ballVel = Vector.zero();
	}

	public void initNetwork()
	{
		network = NetworkFactory.createFactory()
				.input(4)
				.layer(4)
				.layer(3)
				.output(4)
				.fullyConnect(0, 1)
				.fullyConnect(1, 2)
				.fullyConnect(2, 3)
				.weightRandom(-0.2f, 0.2f)
				.build();
	}

	private void think()
	{
		float motX = (float) (cGrav.x - lastPos.x) / 5;
		float distX = (float) (mass.pos.x - ball.x) / 25;
		lastPos = cGrav;

		network.setInput((float) (system.angle - Math.toRadians(90)) * 10, motX, distX, (float) ballVel.x);
		network.compute();
		float[] out = network.getOutput();
		counterAngularVelocity = out[0];
		platformAngularVelocity = out[1];
		counterWeight.length = (float) Util.limit(Math.abs(out[2]), 0, 100);
		platformEnd1.length = (float) Util.limit(Math.abs(out[3]), 0, 30);
		platformEnd2.length = -platformEnd1.length;
//		platformEnd2.length = -(float) Util.limit(Math.abs(out[4]) * 5, 1, 30);
	}

	private void doBallPhysics()
	{
		ballVel.add(0, -gravity);
		ball.add(ballVel);

		Vector v1 = platformEnd1.pos;
		Vector v2 = platformEnd2.pos;
		Vector vm = mass.pos;

		if (ball.x > v1.x && ball.x < v2.x)
		{

			double slope = (v2.y - v1.y) / (v2.x - v1.x);
			if (slope != Double.POSITIVE_INFINITY && slope != Double.NEGATIVE_INFINITY)
			{
				double yint = vm.y - slope * vm.x;
				double yDiff = -ball.y + slope * ball.x + yint;
				if (yDiff < 0)
				{
					double r = Math.atan(slope);
					ball.y += yDiff;
					ballVel.y += yDiff * (Math.cos(r));
					ballVel.x -= yDiff * Math.tan(r);

//					ballVel.scale(0.95, 0.95);

					airtime = 0;
					onPlatform = true;
				}
				else
				{
					onPlatform = false;
				}
			}
		}
		else
		{
			onPlatform = false;
		}

		if (ball.y >= system.pos.y)
		{
			dead = true;
			ball.y = system.pos.y;
			ballVel.x *= 0.5;
			ballVel.y = -ballVel.y * 0.65;
		}


	}

	private boolean flag;
	int lastTickOnPlat;
	int airtime;

	public boolean tick()
	{
		if (!dead)
		{
			ticks++;

//			fitness++;
			if (onPlatform)
			{
				fitness++;
				if (!flag && airtime > 10)
				{
					fitness += airtime * 1.5;
				}
				airtime = 0;
				lastTickOnPlat = ticks;
			}
			else
			{
				airtime++;
			}
			flag = onPlatform;

			cGrav = system.cPos.clone().sub(system.pos);
			if (onPlatform)
			{
				cGrav.scale(system.totalMass).add(ball.clone().sub(system.pos).scale(ballMass)).divide(system.totalMass + ballMass);
			}
			double len = cGrav.magnitude();
			double ang = cGrav.angle();

			float dAngle = (float) (gravity * len / 2 * Math.cos(ang)) / (1000);
			system.angle += dAngle + angularMomentum;
			angularMomentum += dAngle / 5;

			counterWeight.angle = (float) (counterAngularVelocity - Math.PI);
			platformEnd1.angle = (float) (platformAngularVelocity + Math.PI / 2);

			platformEnd1.angle = (float) Math.max(0, Math.min(Math.PI, platformEnd1.angle));
			platformEnd1.angle = (float) Util.limit(platformEnd1.angle, 0, Math.PI);
			platformEnd2.angle = platformEnd1.angle;
			counterWeight.angle = (float) Util.limit(counterWeight.angle, -Math.PI * 3 / 2, -Math.PI / 2);

			randomMotion.angle = (float) Util.applyChange(randomMotion.angle,  Chance.randVal(0.01f), -Math.PI * 3 / 2, -Math.PI / 2);

			if (system.angle <= 0)
			{
				system.angle = 0;
			}
			else if (system.angle >= Math.PI)
			{
				system.angle = (float) Math.PI;
			}

			if (ball.x > 500 || ball.x < 0 || ball.y > 500 || ball.y < 0)
			{
				dead = true;
			}

			system.calc();

			doBallPhysics();

			think();
//			mem1 = out[2];
//			mem2 = out[3];
		}

		return !dead;

	}

	public float getFitness()
	{
		return fitness;
	}

	@Override
	public BalanceCreature makeChild(BalanceCreature par)
	{
		BalanceCreature ret = new BalanceCreature();
		ret.network = ((NNetwork) network).makeChild((NNetwork) par.network);
		return ret;
	}

	public void draw(Graphics g)
	{
		g.setColor(new Color(0, 0, 0, 30));
		GraphicUtils.fillRect(g, 0, system.pos.y, 1000, 1000);
		g.setColor(Color.black);
		GraphicUtils.drawRect(g, 0, system.pos.y, 1000, 1000);
		g.setColor(Color.LIGHT_GRAY);
		network.renderNetwork(g, 200, 200);

		system.draw(g);

		g.setColor(Color.GREEN);
		GraphicUtils.fillCircle(g, cGrav.clone().add(system.pos), 3);

		g.setColor(Color.RED);
		GraphicUtils.fillCircle(g, ball, 6);

		g.setColor(Color.WHITE);
		int py = (int) (system.pos.y);
		g.drawString(String.format("Fitness: %2.0f", fitness), 10, py += 15);
		g.drawString(String.format("onPlatform: %b", onPlatform), 10, py + 15);

	}

}
