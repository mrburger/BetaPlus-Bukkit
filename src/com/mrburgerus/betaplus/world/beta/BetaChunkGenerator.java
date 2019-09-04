package com.mrburgerus.betaplus.world.beta;

import com.mrburgerus.betaplus.BetaPlusPlugin;
import com.mrburgerus.betaplus.world.bukkit.BukkitChunkBuffer;
import net.minecraft.server.v1_14_R1.ChunkCoordIntPair;
import net.minecraft.server.v1_14_R1.WorldGenDungeons;
import net.minecraft.server.v1_14_R1.WorldGenStronghold;
import net.minecraft.server.v1_14_R1.WorldGenVillage;
import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;

import java.util.Random;


// OTG Analog: OTGChunkGenerator
public class BetaChunkGenerator extends ChunkGenerator
{
	/*
	// FIELDS //
	private BetaPlusPlugin plugin;
	private BetaPlusChunkGenerator chunkGenerator;

	private WorldGenStronghold strongholdGen;
	private WorldGenVillage villageGen;

	public BetaChunkGenerator(BetaPlusPlugin plusPlugin)
	{
		this.plugin = plusPlugin;
	}

	public void onInitialize(World _world)
	{
		this.chunkGenerator = new BetaPlusChunkGenerator(_world);
	}

	@Override
	public ChunkData generateChunkData(World world, Random random, int x, int z, BiomeGrid biome)
	{
		ChunkData data = createChunkData(world);

		// Create a ChunkPos (forge name)
		ChunkCoordIntPair chunkPos = new ChunkCoordIntPair(x, z);
		BukkitChunkBuffer buffer = new BukkitChunkBuffer(chunkPos, data);
		this.chunkGenerator.generate(buffer);

		return data;
	}
	*/
}
