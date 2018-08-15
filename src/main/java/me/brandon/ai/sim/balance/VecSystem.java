package me.brandon.ai.sim.balance;

import me.brandon.ai.util.GraphicUtils;
import me.brandon.ai.util.Vector;

import java.awt.*;

public class VecSystem
{

	private static final int minSize = 4;

	public boolean hasChildren;

	public VecSystem[] children;

	public Vector pos;
	public Vector cPos;

	public float size;

	public float mass;
	public float totalMass;

	public float fullAngle;

	public float angle;
	public float length;

	public VecSystem(float x, float y, VecSystem... children) // for top level
	{
		this();
		this.pos = Vector.vector(x, y);
		this.children = children;
		this.totalMass = 0;
		hasChildren = children.length > 0;
		for (VecSystem child : children)
		{
			this.totalMass += child.totalMass;
		}
		this.mass = totalMass;
		this.size = (float) Math.max(minSize, Math.sqrt(mass * 2));
	}

	public VecSystem(float mass, float angle, float length, VecSystem... children) // for bottom level
	{
		this();
		this.mass = mass;
		this.angle = (float) Math.toRadians(angle);
		this.length = length;
		this.totalMass = mass;
		hasChildren = children.length > 0;
		this.children = children;
		for (VecSystem child : children)
		{
			this.totalMass += child.totalMass;
		}
		this.size = (float) Math.max(minSize, Math.sqrt(mass * 2));
	}

	public VecSystem(float mass, float angle, float length) // for bottom level
	{
		this();
		this.mass = mass;
		this.angle = (float) Math.toRadians(angle);
		this.length = length;
		this.totalMass = mass;
		this.size = (float) Math.max(minSize, Math.sqrt(mass * 2));
	}

	private VecSystem() // for bottom level
	{
		pos = Vector.zero();
		cPos = Vector.zero();
	}

	private void calc(VecSystem parent)
	{
		fullAngle = parent.fullAngle + angle;
		pos.x = (float) (parent.pos.x + length * Math.cos(fullAngle));
		pos.y = (float) (parent.pos.y - length * Math.sin(fullAngle));

		cPos.x = pos.x * mass;
		cPos.y = pos.y * mass;
		if (hasChildren)
		{
			for (VecSystem child : children)
			{
				child.calc(this);
				cPos.x += child.cPos.x;
				cPos.y += child.cPos.y;
			}
		}
	}

	public Vector getCenterGravity()
	{
		return cPos;
	}

	public Vector getPosition()
	{
		return pos;
	}

	public void calc()
	{
		fullAngle = angle;
		if (hasChildren)
		{
			cPos = Vector.zero();
			for (VecSystem child : children)
			{
				child.calc(this);
				cPos.x += child.cPos.x;
				cPos.y += child.cPos.y;
			}
			cPos.divide(totalMass);
		}
	}

	public void draw(Graphics g)
	{

		if (hasChildren)
		{
			for (VecSystem child : children)
			{
				g.setColor(Color.lightGray);
				GraphicUtils.drawLine(g, pos, child.pos);
				child.draw(g);
			}
		}
		g.setColor(Color.BLUE);
		GraphicUtils.fillCircle(g, pos, size);
	}


}
