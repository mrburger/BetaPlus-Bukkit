package com.mrburgerus.betaplus.world.beta.select;

import com.mrburgerus.betaplus.world.util.TerrainType;
import net.minecraft.server.v1_14_R1.BiomeBase;
import org.bukkit.block.Biome;

import java.util.List;

public abstract class AbstractBiomeSelector
{
	public List<BiomeBase> SPAWN_BIOMES;

	public AbstractBiomeSelector(List<BiomeBase> spawnBiomes)
	{
		SPAWN_BIOMES = spawnBiomes;
	}

	public abstract BiomeBase getBiome(double temperature, double humidity, double noiseSelect, TerrainType terrainType);

}
