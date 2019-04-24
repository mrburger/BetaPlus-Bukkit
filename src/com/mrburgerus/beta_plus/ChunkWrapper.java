package com.mrburgerus.beta_plus;

import com.mrburgerus.beta_plus.world.AbstractOldChunkGenerator;
import com.mrburgerus.beta_plus.world.beta_plus.ChunkGeneratorBetaPlus;
import net.minecraft.server.v1_13_R2.*;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;

import java.util.List;
import java.util.Random;

public class ChunkWrapper extends ChunkGenerator
{
	public final AbstractOldChunkGenerator<GeneratorSettingsOverworld> actualGenerator;

	public ChunkWrapper(AbstractOldChunkGenerator chunkGenerator)
	{
		actualGenerator = chunkGenerator;
	}

	@Override
	public ChunkData generateChunkData(World world, Random random, int x, int z, BiomeGrid biome)
	{
		System.out.println("Generating Data");
		actualGenerator.createChunk(new ProtoChunk(x, z, new ChunkConverter(new NBTTagCompound())));
		return null;
	}
}