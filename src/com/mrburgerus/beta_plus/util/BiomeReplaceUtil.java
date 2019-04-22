package com.mrburgerus.beta_plus.util;

import net.minecraft.server.v1_13_R2.*;

public class BiomeReplaceUtil
{
	/* Converts Biome Array As Generated to a usable Biome Array */
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

	/* Gets the first solid block at a Position */
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
