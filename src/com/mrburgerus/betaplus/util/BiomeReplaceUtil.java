package com.mrburgerus.betaplus.util;

import com.mrburgerus.betaplus.BetaPlusPlugin;
import net.minecraft.server.v1_15_R1.*;
import org.bukkit.block.Biome;

import java.util.logging.Level;

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

	// Not working, let me figure this out some time.
	public static BiomeStorage convertArrayToStorage(BiomeBase[] biomes)
	{
		BiomeStorage output = new BiomeStorage(new BiomeBase[BiomeStorage.a]);
		BetaPlusPlugin.LOGGER.log(Level.INFO,  "Len: " + BiomeStorage.a);

		return output;
	}
}
