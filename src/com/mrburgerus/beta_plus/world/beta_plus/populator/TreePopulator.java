package com.mrburgerus.beta_plus.world.beta_plus.populator;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.TreeType;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;

import java.util.Random;

public class TreePopulator extends BlockPopulator
{
	@Override
	public void populate(World world, Random random, Chunk chunk)
	{
		if (random.nextBoolean())
		{
			int amount = random.nextInt(4) + 1;  // Amount of trees
			for (int i = 1; i < amount; i++)
			{
				int X = random.nextInt(15);
				int Z = random.nextInt(15);
				int Y = 1;
				for (int j = world.getMaxHeight() - 1; chunk.getBlock(X, j, Z).getType() == Material.AIR; j--)
				world.generateTree(chunk.getBlock(X, Y, Z).getLocation(), TreeType.TREE); // The tree type can be changed if you want.
			}
		}
	}
}
