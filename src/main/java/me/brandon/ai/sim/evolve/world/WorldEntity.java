package me.brandon.ai.sim.evolve.world;

import me.brandon.ai.ui.Drawable;

public interface WorldEntity extends Drawable
{

	void tick(int time);

}
