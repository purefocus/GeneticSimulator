package me.brandon.ai.gensim.world;

import java.awt.*;

public interface Drawable
{

	boolean isVisible(Rectangle bounds);

	void draw(Graphics2D g, Rectangle bounds);

}
