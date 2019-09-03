package com.mrburgerus.betaplus.world.beta_new;

import com.mrburgerus.betaplus.BetaPlus;
import net.minecraft.server.v1_14_R1.*;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_14_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_14_R1.generator.CustomChunkGenerator;
import org.bukkit.generator.BlockPopulator;

import java.util.Random;
import java.util.logging.Level;

public class BetaBlockPopulator extends BlockPopulator
{
	private CustomChunkGenerator chunkGenerator;
	@Override
	public void populate(World world, Random random, Chunk chunk)
	{

		//BetaPlus.LOGGER.log(Level.WARNING, "Populator");
		net.minecraft.server.v1_14_R1.World world1 = ((CraftWorld) world).getHandle();
		if (BetaPlusGenerator.INSTANCE != null)
		{
			 //chunkGenerator = new BetaPlusInternalGenerator(world1, BetaPlusGenerator.INSTANCE);
		}
		//if (chunkGenerator != null)
		{
			BlockPosition pos = new BlockPosition(chunk.getX() << 4, 100, chunk.getZ() << 4);
			BetaPlus.LOGGER.log(Level.INFO, "GENERATE FEATURE: " + pos.toString());
			//new WorldGenDungeons(WorldGenFeatureEmptyConfiguration::a).generate(world1, chunkGenerator, random, new BlockPosition(chunk.getX(), 20, chunk.getZ()), new WorldGenFeatureEmptyConfiguration());
			// Testing
			new WorldGenVillage(WorldGenFeatureVillageConfiguration::a).a(world1.getWorldProvider().getChunkGenerator(), random, chunk.getX(), chunk.getZ());		//.generate(world1, chunkGenerator, random, new BlockPosition(chunk.getX() << 4, 100, chunk.getZ() << 4), new WorldGenFeatureVillageConfiguration("", 6));        //.a(,random, chunk.getX(), chunk.getZ());
			//new WorldGenVillage(WorldGenFeatureVillageConfiguration::a).generate(world1, world1.getWorldProvider().getChunkGenerator(), random, pos, new WorldGenFeatureVillageConfiguration("", 6));
		}
	}

}
