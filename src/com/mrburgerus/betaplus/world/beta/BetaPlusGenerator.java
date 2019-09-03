package com.mrburgerus.betaplus.world.beta;

import com.mrburgerus.betaplus.BetaPlus;
import com.mrburgerus.betaplus.util.ChunkBuffer;
import com.mrburgerus.betaplus.world.BukkitChunkBuffer;
import com.mrburgerus.betaplus.world.ConvertBetaPlusWorld;
import net.minecraft.server.v1_14_R1.*;
import org.bukkit.craftbukkit.v1_14_R1.CraftWorld;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;

public class BetaPlusGenerator extends ChunkGenerator
{
	BetaPlusInternalGenerator internalGenerator;
	private ArrayList<BlockPopulator> blockPopulator = new ArrayList<BlockPopulator>();

	public BetaPlusGenerator()
	{

	}

	@Override
	public List<BlockPopulator> getDefaultPopulators(org.bukkit.World world)
	{
		BetaPlus.log.log(Level.SEVERE, "Populator");
		return blockPopulator;
	}

	@Override
	public ChunkData generateChunkData(org.bukkit.World world, Random random, int x, int z, BiomeGrid biome)
	{
		ChunkData data = createChunkData(world);
		if (internalGenerator == null)
		{
			BetaPlus.log.log(Level.SEVERE, "CREATING INTERNAL");
			this.internalGenerator = new BetaPlusInternalGenerator(((CraftWorld) world).getHandle(), this, new BiomeProviderBetaPlus(((CraftWorld) world).getHandle()));
		}
		ChunkBuffer buffer = new BukkitChunkBuffer(new ChunkCoordIntPair(x, z), data, world);
		internalGenerator.generateChunk(buffer);
		return data;

		// Test 256 size (16*16)
		//IChunkAccess chunkAccess = new Chunk(((CraftWorld) world).getHandle(), new ChunkCoordIntPair(x, z), new BiomeBase[256]);
		// Testing
		//internalGenerator.buildBase(chunkAccess);
		//BetaPlus.log.log(Level.SEVERE, "Done INTERNAL");

		//return ConvertBetaPlusWorld.convertChunkAccess(world, chunkAccess);
	}



}
