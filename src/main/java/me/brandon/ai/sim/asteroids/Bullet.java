package me.brandon.ai.sim.asteroids;

import me.brandon.ai.api.game.GameEntity;
import me.brandon.ai.util.GraphicsWrapper;

import java.awt.*;
import java.util.ArrayList;

public class Bullet extends GameEntity
{

	protected int lifespan;
	protected final AsteroidsPlayer player;
	protected boolean dead;

	public Bullet(AsteroidsPlayer player, double px, double py, double vx, double vy)
	{
		super(px, py, vx, vy);
		this.player = player;
		this.dead = false;

		lifespan = 200;
	}

	public void tick()
	{
		if (lifespan-- > 0)
		{
			super.tick();

			ArrayList<Asteroid> l = new ArrayList<>(player.asteroids);

			for (Asteroid asteroid : l)
			{
				if (asteroid.containsVector(pos))
				{
					asteroid.split();
					player.onScore();
					die();
					break;
				}
			}
		}
		else
		{
			die();
		}
	}

	private void die()
	{
		dead = true;
	}

	@Override
	public void draw(GraphicsWrapper g)
	{
		g.setColor(Color.WHITE);
		g.drawCircle(pos, 2);
		g.drawLine(pos, pos.clone().sub(vel));
	}
}
