package me.brandon.ai.ui;

import me.brandon.ai.gensim.GeneticSimulator;
import me.brandon.ai.gensim.world.World;
import me.brandon.ai.gensim.world.creature.Creature;

import java.awt.*;
import java.awt.image.BufferStrategy;

public class CreatureDataPanel extends Canvas
{

	public CreatureDataPanel()
	{

		setPreferredSize(new Dimension(800, 800));

	}

	int w, h;

	public void render()
	{
		BufferStrategy bs = this.getBufferStrategy();

		if (bs == null)
		{
			createBufferStrategy(2);
			return;
		}

		Graphics2D g = (Graphics2D) bs.getDrawGraphics();

		g.setColor(Color.DARK_GRAY);
		g.fillRect(0, 0, getWidth(), getHeight());


		Creature creature = World.selectedCreature;
		if (creature != null)
		{
			g.setColor(Color.white);

			int brainHeight = getHeight() / 3 * 2;

			if (GeneticSimulator.updateRendering)
				creature.brain.renderBrain(g, getWidth(), brainHeight);

			g.drawLine(0, brainHeight, getWidth(), brainHeight);

			int spaceY = 15;
			int posX = 10;
			int posY = brainHeight + spaceY;
			g.drawString(String.format("Name: %sf", creature.name), posX, posY += spaceY);
			g.drawString(String.format("Age: %2.2f", creature.age), posX, posY += spaceY);
			g.drawString(String.format("Generation: %d", creature.generation), posX, posY += spaceY);
			posY += spaceY;
			g.drawString(String.format("Size: %2.2f > %2.2f > %2.2f", creature.maxSize, creature.size, creature.baseSize), posX, posY += spaceY);
			g.drawString(String.format("Consume Rate: %2.4f", creature.energyUsage), posX, posY += spaceY);
			g.drawString(String.format("RoD: %2.4f", creature.rateOfDeath), posX, posY += spaceY);
			g.drawString(String.format("Impulse: %2.4f", creature.impulse), posX, posY += spaceY);

			posX = getWidth() / 3;
			posY = brainHeight + spaceY;
			g.drawString(String.format("energy: %2.4f", creature.energy), posX, posY += spaceY);
			g.drawString(String.format("Remaining Life: %2.4f", creature.remainingLife), posX, posY += spaceY);
			g.drawString(String.format("Needle: [l: %2.2f, a: %2.2f]", creature.needleLength, creature.direction), posX, posY += spaceY);
			posY += spaceY;
			g.drawString(String.format("Pos: [x: %2.2f, y: %2.2f]", creature.pos.x, creature.pos.y), posX, posY += spaceY);
			g.drawString(String.format("vel: [x: %2.2f, y: %2.2f]", creature.vel.x, creature.vel.y), posX, posY += spaceY);
			posY += spaceY;
			g.drawString(String.format("food type: %2.2f", creature.foodType), posX, posY += spaceY);
			if (creature.tile != null)
			{
				g.drawString(String.format("tile food yype: %2.2f", creature.tile.getFoodType()), posX, posY += spaceY);
				g.drawString(String.format("tile food Amount: %2.2f", creature.tile.getFoodLevel()), posX, posY += spaceY);
				g.drawString(String.format("tile temperature: %2.2f", creature.tile.getTemperature()), posX, posY += spaceY);
				g.drawString(String.format("tile water: %b", creature.tile.isWater()), posX, posY += spaceY);
			}


		}


		g.dispose();
		bs.show();
	}
}
