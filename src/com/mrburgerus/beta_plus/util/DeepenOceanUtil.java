package com.mrburgerus.beta_plus.util;

import net.minecraft.server.v1_13_R2.BlockPosition;
import net.minecraft.server.v1_13_R2.Blocks;
import net.minecraft.server.v1_13_R2.IChunkAccess;

import java.util.Random;

public class DeepenOceanUtil
{
	public static void deepenOcean(IChunkAccess chunk, Random random, int seaLevel, int smoothSize, double scaleFactor)
	{
		// Get X and Z start (COULD NEED MODIFICATION
		int xStart = chunk.getPos().x;
		int zStart = chunk.getPos().z;

		// Create 2-D Map of Y-Depth in Chunk
		double[][] depthValues = new double[16][16];
		for (int xV = 0; xV < depthValues.length; ++xV)
		{
			for (int zV = 0; zV < depthValues[xV].length; ++zV)
			{
				int y = BiomeReplaceUtil.getSolidHeightY(new BlockPosition(xStart + xV, 0, zStart + zV), chunk);
				int depth = (seaLevel - y) - 1; // Depth is -1 because of lowered sea level.
				depthValues[xV][zV] = depth;
			}
		}
		// Process these values by applying a transform, with no randomness
		for (int xV = 0; xV < depthValues.length; ++xV)
		{
			for (int zV = 0; zV < depthValues[xV].length; ++zV)
			{
				// Should eventually have some call to the Seed, like rand.nextDouble()
				// Changed to 2.95 from 2.85
				depthValues[xV][zV] = depthValues[xV][zV] * (scaleFactor + (random.nextDouble() * 0.125));
			}
		}
		// Gaussian BLUR
		double[][] newDepths = ConvolutionMathUtil.convolve2DSquare(depthValues, smoothSize, 2f);


		// Now Process These Values
		for (int xV = 0; xV < newDepths.length; ++xV)
		{
			for (int zV = 0; zV < newDepths[xV].length; ++zV)
			{
				int y = BiomeReplaceUtil.getSolidHeightY(new BlockPosition(xStart + xV, 0, zStart + zV), chunk);
				int yNew = seaLevel - (int) newDepths[xV][zV];
				if (yNew < y && y < seaLevel) // We are Deep, yo.
				{
					//BetaPlus.LOGGER.info("Deepening Ocean");
					// We Are "Underwater"
					for(int yV = y; yV > yNew; --yV)
					{
						chunk.setType(new BlockPosition(xStart + xV, yV, zStart + zV), Blocks.WATER.getBlockData(), false);
					}
				}
			}
		}
	}
}
