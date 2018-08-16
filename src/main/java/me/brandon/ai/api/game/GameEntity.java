package me.brandon.ai.api.game;

import me.brandon.ai.util.GraphicsWrapper;
import me.brandon.ai.util.Vector;

public abstract class GameEntity
{

	protected Vector pos;
	protected Vector vel;

	public GameEntity(double px, double py)
	{
		this.pos = Vector.vector(px, py);
		this.vel = Vector.zero();
	}

	public GameEntity(double px, double py, double vx, double vy)
	{
		this.pos = Vector.vector(px, py);
		this.vel = Vector.vector(vx, vy);
	}

	public void tick()
	{
		pos.add(vel);
	}

	public abstract void draw(GraphicsWrapper g);

}
