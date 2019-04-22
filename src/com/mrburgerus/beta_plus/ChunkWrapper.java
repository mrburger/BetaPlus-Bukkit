package com.mrburgerus.beta_plus;

import net.minecraft.server.v1_13_R2.*;
import org.bukkit.craftbukkit.libs.it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import org.bukkit.craftbukkit.libs.it.unimi.dsi.fastutil.longs.LongSet;
import org.bukkit.craftbukkit.v1_13_R2.generator.InternalChunkGenerator;

import javax.annotation.Nullable;
import java.util.List;

public class ChunkWrapper extends InternalChunkGenerator<GeneratorSettings>
{
	public final ChunkGenerator actualGenerator;

	public ChunkWrapper (ChunkGenerator chunkGenerator)
	{
		actualGenerator = chunkGenerator;
	}

	@Override
	public void createChunk(IChunkAccess iChunkAccess)
	{
		actualGenerator.createChunk(iChunkAccess);
	}

	@Override
	public void addFeatures(RegionLimitedWorldAccess regionLimitedWorldAccess, WorldGenStage.Features features)
	{
		actualGenerator.addFeatures(regionLimitedWorldAccess, features);
	}

	@Override
	public void addDecorations(RegionLimitedWorldAccess regionLimitedWorldAccess)
	{
		actualGenerator.addDecorations(regionLimitedWorldAccess);
	}

	@Override
	public void addMobs(RegionLimitedWorldAccess regionLimitedWorldAccess)
	{
		actualGenerator.addMobs(regionLimitedWorldAccess);
	}

	@Override
	public List<BiomeBase.BiomeMeta> getMobsFor(EnumCreatureType enumCreatureType, BlockPosition blockPosition)
	{
		return actualGenerator.getMobsFor(enumCreatureType, blockPosition);
	}

	@Nullable
	@Override
	public BlockPosition findNearestMapFeature(World world, String s, BlockPosition blockPosition, int i, boolean b)
	{
		return actualGenerator.findNearestMapFeature(world, s, blockPosition, i, b);
	}

	@Override
	public GeneratorSettings getSettings()
	{
		return null;
	}

	@Override
	public int a(World world, boolean b, boolean b1)
	{
		return 0;
	}

	@Override
	public WorldChunkManager getWorldChunkManager()
	{
		return null;
	}

	@Override
	public long getSeed()
	{
		return 0;
	}

	@Override
	public int getSpawnHeight()
	{
		return 0;
	}

	@Override
	public int getGenerationDepth()
	{
		return 0;
	}

	@Override
	public World getWorld()
	{
		return null;
	}

	@Override
	public Long2ObjectMap<LongSet> getStructureCache(StructureGenerator structureGenerator)
	{
		return null;
	}

	@Override
	public Long2ObjectMap<StructureStart> getStructureStartCache(StructureGenerator structureGenerator)
	{
		return null;
	}

	@Nullable
	@Override
	public WorldGenFeatureConfiguration getFeatureConfiguration(BiomeBase biomeBase, StructureGenerator structureGenerator)
	{
		return null;
	}

	@Override
	public boolean canSpawnStructure(BiomeBase biomeBase, StructureGenerator structureGenerator)
	{
		return false;
	}
}
