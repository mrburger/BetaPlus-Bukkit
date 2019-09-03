package com.mrburgerus.betaplus.util;

import com.mrburgerus.betaplus.BetaPlus;
import net.minecraft.server.v1_14_R1.*;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.block.data.BlockData;
import org.bukkit.generator.ChunkGenerator;

import java.util.logging.Level;

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

	/* Converts Biome Array As Generated to a usable Biome Array */
	public static BiomeBase[] convertBiomeArray(Biome[] biomesIn)
	{
		BiomeBase[] biomesOut = new BiomeBase[biomesIn.length];
		for (int i = 0; i < biomesOut.length; i++)
		{
			int place = (i & 15) << 4 | i >> 4 & 15;

			biomesOut[i] =  findBiomeBase(biomesIn[place].getKey().getKey());
		}
		return biomesOut;
	}

	public static BiomeBase convertBiome(Biome biome)
	{
		for (BiomeBase b : BiomeBase.b)
		{
			if (b.j().equals(biome.getKey().getKey()))
			{
				return b;
			}
		}
		return Biomes.b;
	}

	// WORKING
	public static BiomeBase findBiomeBase(String biomeIn)
	{
		for (BiomeBase base : BiomeBase.b)
		{
			if (base.j().replaceAll("biome.minecraft.", "").equals(biomeIn))
			{
				return base;
			}
		}
		return Biomes.b;
	}

	public static Biome[] toBukkitBiome(BiomeBase[] biomes)
	{
		Biome[] retBiomes = new Biome[biomes.length];
		for (int i = 0; i < biomes.length; i++)
		{
			for (Biome b : Biome.values())
			{
				if (biomes[i].j().replaceAll("biome.minecraft.", "").equals(b.getKey().getKey()))
				{
					BetaPlus.LOGGER.log(Level.FINE, "Found!");
					retBiomes[i] = b;
				}
			}
		}
		return retBiomes;
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

	/* Gets the first solid block at a Position */
	public static int getSolidHeightY(BlockPosition pos, ChunkBuffer buffer)
	{
		for (int y = 130; y >= 0; --y)
		{
			//DUH
			//Block block = chunk.getType(new BlockPosition(pos.getX(), y, pos.getZ())).getBlock();
			BlockData block = buffer.getBlock(pos.getX(), y, pos.getZ());
			if (block != Blocks.AIR && block != Blocks.WATER)
			{
				return y;
			}
		}
		return 0;
	}

	/* Gets the first solid block at a Position */
	// POSITION IS RELATIVE TO CHUNK!
	public static int getSolidHeightY(BlockPosition posInChunk, ChunkGenerator.ChunkData data)
	{
		for (int y = 130; y >= 0; --y)
		{
			//DUH
			Material block = data.getType(posInChunk.getX(), y, posInChunk.getZ());
			if (block != Material.AIR && block != Material.WATER)
			{
				//BetaPlus.LOGGER.log(Level.INFO, "MAT: " + block.getKey().getKey() + ", " + y);
				return y;
			}
		}
		return 0;
	}
}
