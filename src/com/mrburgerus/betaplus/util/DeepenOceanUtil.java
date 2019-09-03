package com.mrburgerus.betaplus.util;

import com.mrburgerus.betaplus.BetaPlus;
import net.minecraft.server.v1_14_R1.BlockPosition;
import net.minecraft.server.v1_14_R1.Blocks;
import net.minecraft.server.v1_14_R1.IChunkAccess;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.v1_14_R1.block.CraftBlock;
import org.bukkit.generator.ChunkGenerator;

import java.util.Random;
import java.util.logging.Level;

public class DeepenOceanUtil
{
	public static void deepenOcean(IChunkAccess chunk, Random random, int seaLevel, int smoothSize, double scaleFactor)
	{
		// Get X and Z start
		int xStart = chunk.getPos().d();
		int zStart = chunk.getPos().e();

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
						chunk.a(new BlockPosition(xStart + xV, yV, zStart + zV), Blocks.WATER.a(Blocks.WATER.getBlockData()), false);
					}
				}
			}
		}
	}

	public static void deepenOcean(int x, int z, ChunkGenerator.ChunkData buffer, Random random, int seaLevel, int smoothSize, double scaleFactor)
	{
		// Get X and Z start
		//BetaPlus.LOGGER.log(Level.SEVERE,"POI: " + x + ", " + z + ";  " + (x << 4) + ", " + (z << 4));
		// Create 2-D Map of Y-Depth in Chunk
		double[][] depthValues = new double[16][16];
		for (int xV = 0; xV < depthValues.length; ++xV)
		{
			for (int zV = 0; zV < depthValues[xV].length; ++zV)
			{
				int y = BiomeReplaceUtil.getSolidHeightY(new BlockPosition((x << 4) + xV, 0, (z << 4) + zV), buffer);
				//BetaPlus.LOGGER.log(Level.INFO, "YPOS: " + y);
				int depth = (BetaPlus.seaLevel - y) - 1; // Depth is -1 because of lowered sea level.
				BetaPlus.LOGGER.log(Level.INFO, "Depth: (" + (x << 4) + xV + ", " +  (z << 4) + zV + "): " + depth);
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
				depthValues[xV][zV] = depthValues[xV][zV] * (3.25 + (random.nextDouble() * 0.125));
			}
		}
		// Gaussian BLUR
		double[][] newDepths = ConvolutionMathUtil.convolve2DSquare(depthValues, smoothSize, 2f);


		// Now Process These Values
		for (int xV = 0; xV < newDepths.length; ++xV)
		{
			for (int zV = 0; zV < newDepths[xV].length; ++zV)
			{
				int y = BiomeReplaceUtil.getSolidHeightY(new BlockPosition((x << 4) + xV, 0, (z << 4) + zV), buffer);
				int yNew = seaLevel - (int) newDepths[xV][zV];
				if (yNew < y && y < seaLevel) // We are Deep, yo.
				{
					//BetaPlus.LOGGER.info("Deepening Ocean");
					// We Are "Underwater"
					for(int yV = y; yV > yNew; --yV)
					{
						//chunk.a(new BlockPosition(xStart + xV, yV, zStart + zV), Blocks.WATER.a(Blocks.WATER.getBlockData()), false);
						buffer.setBlock((x << 4) + xV, yV, (z << 4) + zV, Bukkit.createBlockData(Material.WATER));
					}
				}
			}
		}
	}

	public static void deepenOcean(int xChunk, int zChunk, ChunkGenerator.ChunkData data, Random random, int seaLevel, int smoothSize)
	{
		// Get X and Z start
		int xStart = (xChunk << 4);
		int zStart = (zChunk << 4);

		// Create 2-D Map of Y-Depth in Chunk
		double[][] depthValues = new double[16][16];
		for (int xV = 0; xV < depthValues.length; ++xV)
		{
			for (int zV = 0; zV < depthValues[xV].length; ++zV)
			{
				int y = BiomeReplaceUtil.getSolidHeightY(new BlockPosition(xV, 0, zV), data);
				int depth = (seaLevel - y) - 1; // Depth is -1 because of lowered sea level.
				//BetaPlus.LOGGER.log(Level.INFO, "DEPTH: " + depth);
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
				depthValues[xV][zV] = depthValues[xV][zV] * (2.5 + random.nextDouble() * 0.125);
			}
		}
		// Gaussian BLUR
		double[][] newDepths = ConvolutionMathUtil.convolve2DSquare(depthValues, smoothSize, 2f);


		// Now Process These Values
		for (int xV = 0; xV < newDepths.length; ++xV)
		{
			for (int zV = 0; zV < newDepths[xV].length; ++zV)
			{
				int y = BiomeReplaceUtil.getSolidHeightY(new BlockPosition(xV, 0, zV), data);
				//
				int yNew =  seaLevel - (int) newDepths[xV][zV];
				//BetaPlus.LOGGER.log(Level.INFO, "YN: " + yNew);
				if (yNew < y && y < seaLevel) // We are Deep, yo.
				{
					// We Are "Underwater"
					for(int yV = y; yV > yNew; --yV)
					{
						//BetaPlus.LOGGER.log(Level.INFO, "Water");
						//chunk.a(new BlockPosition(xStart + xV, yV, zStart + zV), Blocks.WATER.a(Blocks.WATER.getBlockData()), false);
						// Trying inside chunk
						//data.setBlock(xV, yV, zV, Material.WATER);
						//BetaPlus.LOGGER.log(Level.INFO, "W: "  + xStart + xV + ", " + yV + ", " + xStart + xV);
						data.setBlock(xV, yV, zV, Material.WATER);
					}
				}
			}
		}
	}

}
