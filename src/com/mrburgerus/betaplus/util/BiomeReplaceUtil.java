package com.mrburgerus.betaplus.util;

import net.minecraft.server.v1_15_R1.*;
import org.bukkit.block.Biome;

public class BiomeReplaceUtil
{
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
