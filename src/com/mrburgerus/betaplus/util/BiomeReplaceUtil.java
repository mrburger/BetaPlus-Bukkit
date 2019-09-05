package com.mrburgerus.betaplus.util;

import net.minecraft.server.v1_14_R1.*;
import org.bukkit.block.Biome;

public class BiomeReplaceUtil
{
	/* Converts Biome Array As Generated to a usable Biome Array */
	public static Biome[] convertBiomeArray(Biome[] biomesIn)
	{
		Biome[] biomesOut = new Biome[biomesIn.length];
		for (int i = 0; i < biomesOut.length; i++)
		{
			int place = (i & 15) << 4 | i >> 4 & 15;
			biomesOut[i] = biomesIn[place];
		}
		return biomesOut;
	}

	public static BiomeBase[] convertBiomeArray(BiomeBase[] biomesIn)
	{
		BiomeBase[] biomesOut = new BiomeBase[biomesIn.length];
		for (int i = 0; i < biomesOut.length; i++)
		{
			int place = (i & 15) << 4 | i >> 4 & 15;
			biomesOut[i] = biomesIn[place];
		}
		return biomesOut;
	}

	/* Back-Converts a Biome Array */
	// Looks to work
	public static Biome[] backConvertBiomeArray(Biome[] biomesIn)
	{
		// Array is in X, Z format, convert to Z, X format.
		Biome[] biomesOut = new Biome[biomesIn.length];
		for (int i = 0; i < biomesOut.length; i++)
		{
			int place = i >> 4 & 15 | (i & 15) << 4;
			biomesOut[i] = biomesIn[place];
		}
		return biomesOut;
	}

	public static int getSolidHeightY(BlockPosition pos, IChunkAccess chunk)
	{
		for (int y = 130; y >= 0; --y)
		{
			//DUH
			Block block = chunk.getType(new BlockPosition(pos.getX(), y, pos.getZ())).getBlock();
			if (block != Blocks.AIR && block != Blocks.WATER)
			{
				return y;
			}
		}
		return 0;
	}
}
