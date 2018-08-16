package me.brandon.ai.sim.asteroids;

import me.brandon.ai.api.game.GameEntity;
import me.brandon.ai.util.Chance;
import me.brandon.ai.util.GraphicsWrapper;
import me.brandon.ai.util.Vector;

import java.awt.*;

import static me.brandon.ai.sim.asteroids.AsteroidsGame.windowHeight;
import static me.brandon.ai.sim.asteroids.AsteroidsGame.windowWidth;

public class Asteroid extends GameEntity
{
	protected int size;
	protected int radius;
	protected int radSq;

	protected boolean dead;

	protected AsteroidsPlayer player;

	public Asteroid(AsteroidsPlayer player, double px, double py, double vx, double vy, int size)
	{
		super(px, py, vx, vy);
		this.player = player;
		this.size = size;
		this.dead = false;

		if (size <= 0)
		{
			size = 1;
		}

		switch (size)
		{
			case 1:
				radius = 15;
				vel.normalize();
				vel.scale(1.25);
				break;
			case 2:
				radius = 30;
				vel.normalize();
				break;
			case 3:
				radius = 60;
				vel.normalize();
				vel.scale(0.75);
				break;
		}
		radius *= 2;

		radSq = (radius / 2) * (radius / 2);
	}

	public boolean containsVector(Vector v)
	{
		return v.distSq(pos) < radSq;
	}

	public void split()
	{
		dead = true;
		player.asteroids.removeIf(a -> a.dead);
		if(size > 1)
		{
			player.asteroids.add(new Asteroid(player, pos.x, pos.y, Chance.randVal(0.5f) * 2.5, Chance.randVal(0.5f) * 2.5, size - 1));
			player.asteroids.add(new Asteroid(player, pos.x, pos.y, Chance.randVal(0.5f) *2.5, Chance.randVal(0.5f) * 2.5, size - 1));
		}
	}

	public void tick()
	{
		super.tick();

		if (player.pos.distSq(pos) < (radSq + 1000))
		{
			player.onColision();
		}

		int r2 = radius >> 1;
		if (pos.y > windowHeight + r2)
			pos.y -= windowHeight * 2 + radius;
		else if (pos.y < -windowHeight - r2)
			pos.y += windowHeight * 2 + radius;
		if (pos.x > windowWidth + r2)
			pos.x -= windowWidth * 2 + radius;
		else if (pos.x < -windowWidth - r2)
			pos.x += windowWidth * 2 + radius;
	}

	@Override
	public void draw(GraphicsWrapper g)
	{
		g.setColor(Color.WHITE);
		g.drawCircle(pos, radius);
	}
}
