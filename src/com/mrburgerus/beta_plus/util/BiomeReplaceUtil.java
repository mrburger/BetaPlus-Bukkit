package com.mrburgerus.beta_plus.util;

import org.bukkit.Material;
import org.bukkit.block.Biome;

import static org.bukkit.Material.*;
import static org.bukkit.generator.ChunkGenerator.ChunkData;

public class BiomeReplaceUtil
{
	/* Converts Biome Array As Generated to a usable Biome Array */
	public static Biome[] convertBiomeArray(Biome[] BiomeIn)
	{
		Biome[] BiomeOut = new Biome[BiomeIn.length];
		for (int i = 0; i < BiomeOut.length; i++)
		{
			int place = (i & 15) << 4 | i >> 4 & 15;
			BiomeOut[i] = BiomeIn[place];
		}
		return BiomeOut;
	}

	/* Gets the first solid block at a Position */
	public static int getSolidHeightY(int x, int z, ChunkData chunkData)
	{
		for (int y = 130; y >= 0; --y)
		{
			//DUH
			Material block = chunkData.getType(x, y, z);
			if (block != Material.AIR && block != Material.WATER)
			{
				return y;
			}
		}
		return 0;
	}
}
