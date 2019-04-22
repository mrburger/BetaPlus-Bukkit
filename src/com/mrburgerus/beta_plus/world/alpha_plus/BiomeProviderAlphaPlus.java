package com.mrburgerus.beta_plus.world.alpha_plus;

import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import com.mrburgerus.beta_plus.world.alpha_plus.sim.AlphaPlusSimulator;
import net.minecraft.server.v1_13_R2.*;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class BiomeProviderAlphaPlus extends WorldChunkManager
{
	private BiomeBase landBiome;
	private BiomeBase oceanBiome;
	/* Had to create custom biomes */
	/* Custom Biomes created so that ICE sheets Spawn on Oceans in snowy worlds */
	private static final BiomeBase ALPHA_FROZEN_BIOME = Biomes.SNOWY_TUNDRA;
	public static final BiomeBase ALPHA_FROZEN_OCEAN = Biomes.FROZEN_OCEAN; // NOT THE BEST SOLUTION
	private static final BiomeBase ALPHA_BIOME = Biomes.FOREST;
	public static final BiomeBase ALPHA_OCEAN = Biomes.OCEAN;
	private static final BiomeBase[] BIOMES_LIST = new BiomeBase[]{ALPHA_FROZEN_BIOME, ALPHA_FROZEN_OCEAN, ALPHA_BIOME, ALPHA_OCEAN};
	// Simulator for Y-heights
	private AlphaPlusSimulator simulator;

	public BiomeProviderAlphaPlus(World world)
	{
		this.landBiome = ALPHA_BIOME;
		this.oceanBiome = ALPHA_OCEAN;
		simulator = new AlphaPlusSimulator(world);
	}

	/* Just Like Beta Plus, generates a single landBiome */
	private BiomeBase[] generateBiomes(int startX, int startZ, int xSize, int zSize)
	{
		BiomeBase[] biomeArr = new BiomeBase[xSize * zSize];
		for (int i = 0; i < biomeArr.length; i++)
		{
			biomeArr[i] = this.landBiome;
		}
		return biomeArr;
	}

	/* Adds OCEANS to the mix to the Biome Provider. */
	/* ONLY CALL WHEN NECESSARY, has to simulate the Y-heights of the world */
	/* useAverage should be TRUE if searching for Monuments, false Otherwise */
	private BiomeBase[] generateBiomesWithOceans(int startX, int startZ, int xSize, int zSize, boolean useAverage)
	{
		BiomeBase[] biomeArr = new BiomeBase[xSize * zSize];
		int counter = 0;
		// Swapped X and Z, to match beta (HAD NO EFFECT!)
		for (int x = startX; x < xSize + startX; ++x)
		{
			for (int z = startZ; z < zSize + startZ; ++z)
			{
				BlockPosition blockPos = new BlockPosition(x, 0, z);
				//Assign this first
				biomeArr[counter] = this.landBiome;
				// If using the 3x3 Average (Monuments and Mansions)
				if (useAverage)
				{
					// Changed from avg to chunk
					Pair<Integer, Boolean> avg = simulator.simulateYAvg(blockPos);
					// Tried 56, 58, 57
					if (avg.getFirst() < 57 && !avg.getSecond())
					{
						biomeArr[counter] = this.oceanBiome;
					}
				}
				else
				{
					Pair<Integer, Boolean> avg = simulator.simulateYChunk(blockPos);
					if (avg.getFirst() < 56) //&& !avg.getSecond())  //Typically for Shipwrecks, Ruins, and Chests
					{
						biomeArr[counter] = this.oceanBiome;
					}
				}
				counter++;
			}
		}
		return biomeArr;
	}

	/* Used By Shipwrecks and Buried Treasure */
	@Nullable
	@Override
	public BiomeBase getBiome(BlockPosition blockPos, @Nullable BiomeBase biome)
	{
		return this.generateBiomesWithOceans(blockPos.getX(), blockPos.getZ(), 1, 1, false)[0];
	}

	@Override
	public BiomeBase[] getBiomes(int x, int z, int width, int depth)
	{
		return getBiomes(x, z, width, depth, true);
	}

	@Override
	public BiomeBase[] a(int i, int i1, int i2, int i3, boolean b)
	{
		return new BiomeBase[0];
	}

	@Override
	public Set<BiomeBase> a(int centerX, int centerZ, int sideLength)
	{
		Set<BiomeBase> set = Sets.newHashSet();
		Collections.addAll(set, this.generateBiomesWithOceans(centerX, centerZ, sideLength, sideLength, true));
		return set;
	}

	// Find Biome Position
	@Nullable
	@Override
	public BlockPosition a(int x, int z, int range, List<BiomeBase> biomeList, Random random)
	{
		int i = x - range >> 2;
		int j = z - range >> 2;
		int k = x + range >> 2;
		int l = z + range >> 2;
		int xSize = k - i + 1;
		int zSize = l - j + 1;
		BiomeBase[] biomeArr = this.generateBiomes(i, j, xSize, zSize);

		BlockPosition blockpos = null;
		int k1 = 0;

		for(int counter = 0; counter < xSize * zSize; ++counter) {
			int i2 = i + counter % xSize << 2;
			int j2 = j + counter / xSize << 2;
			if (biomeList.contains(biomeArr[counter]))
			{
				if (blockpos == null || random.nextInt(k1 + 1) == 0) {
					blockpos = new BlockPosition(i2, 0, j2);
				}

				++k1;
			}
		}
		return blockpos;
	}

	@Override
	public boolean a(StructureGenerator<?> structure)
	{
		return this.a.computeIfAbsent(structure, (param1) -> {
			for(BiomeBase biome : BIOMES_LIST) // Go through list of declared Biomes
			{
				if (biome.a(param1))
				{
					return true;
				}
			}

			return false;
		});
	}

	//Get Surface Blocks
	@Override
	public Set<IBlockData> b()
	{
		this.b.add(landBiome.r().a());
		return this.b;
	}

	public BiomeBase[] getBiomes(int x, int z, int width, int length, boolean b)
	{
		return generateBiomes(x, z, width, length);
	}
}
