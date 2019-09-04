package com.mrburgerus.betaplus.world.util;

import net.minecraft.server.v1_14_R1.BlockPosition;
import nl.rutgerkok.worldgeneratorapi.BaseChunkGenerator;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.generator.ChunkGenerator;

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

	/* Gets the first solid block at a Position */
	public static int getSolidHeightY(BlockPosition pos, BaseChunkGenerator.GeneratingChunk chunk)
	{
		for (int y = 130; y >= 0; --y)
		{
			//DUH
			Material block = chunk.getBlocksForChunk().getBlockData(pos.getX(), y, pos.getZ()).getMaterial();
			if (block != Material.AIR && block != Material.WATER)
			{
				return y;
			}
		}
		return 0;
	}

}
