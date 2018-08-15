package me.brandon.ai.util;

public class Vector
{


	public static Vector zero()
	{
		return new Vector(0, 0);
	}

	public static Vector vector(double x, double y)
	{
		return new Vector(x, y);
	}

	public static Vector fromAngle(double angle, double mag)
	{
		return new Vector(angle).scale(mag);
	}

	public static Vector fromAngle(double angle)
	{
		return new Vector(angle);
	}

	public double x;
	public double y;

	public Vector()
	{

	}

	public Vector(double angle)
	{
		this.x = Math.cos((float) angle);
		this.y = Math.sin((float) angle);
	}

	public Vector(double x, double y)
	{
		this.x = x;
		this.y = y;
	}

	public Vector addAngle(double angle)
	{
		double m = magnitude();
		double a = angle();
		a += angle;

		this.x = Math.cos((float) a) * m;
		this.y = Math.sin((float) a) * m;

		return this;
	}

	public Vector add(Vector v)
	{
		return add(v.x, v.y);
	}

	public Vector add(double x, double y)
	{
		this.x += x;
		this.y += y;
		return this;
	}

	public Vector sub(Vector v)
	{
		return sub(v.x, v.y);
	}

	public Vector sub(double x, double y)
	{
		this.x -= x;
		this.y -= y;
		return this;
	}

	public Vector scale(double scale)
	{
		this.x *= scale;
		this.y *= scale;
		return this;
	}


	public Vector scale(Vector v)
	{
		return scale(v.x, v.y);
	}

	public Vector scale(double sx, double sy)
	{
		this.x *= sx;
		this.y *= sy;
		return this;
	}

	public Vector divide(double dx, double dy)
	{
		this.x /= dx;
		this.y /= dy;
		return this;
	}

	public Vector divide(Vector v)
	{
		divide(v.x, v.y);
		return this;
	}

	public Vector divide(double divisor)
	{
		this.x /= divisor;
		this.y /= divisor;
		return this;
	}

	public Vector normalize()
	{
		double magnitude = magnitude();
		this.x /= magnitude;
		this.y /= magnitude;
		return this;
	}

	public Vector dot(Vector v)
	{
		return scale(v.x, v.y);
	}

	public double dot(double x, double y)
	{
		return this.x * x + this.y * y;
	}

	public double magnitudeSq()
	{
		return this.x * this.x + this.y * this.y;
	}

	public double magnitude()
	{
		return Math.sqrt(magnitudeSq());
	}

	public double distSq(Vector v)
	{
		return distSq(v.x, v.y);
	}

	public double distSq(double x, double y)
	{
		return (this.x - x) * (this.x - x) + (this.y - y) * (this.y - y);
	}

	public double dist(Vector v)
	{
		return dist(v.x, v.y);
	}

	public double dist(double x, double y)
	{
		return Math.sqrt(distSq(x, y));
	}

	public Vector clone()
	{
		return new Vector(this.x, this.y);
	}

	public float angle()
	{
		return (float) Math.atan2(y, x);
	}

	public String toString()
	{
		return String.format("Vector[%2.2f, %2.2f]", x, y);
	}

}
