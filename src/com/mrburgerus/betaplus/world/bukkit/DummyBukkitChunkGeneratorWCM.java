package com.mrburgerus.betaplus.world.bukkit;


import com.mrburgerus.betaplus.BetaPlusInjectPlugin;
import com.mrburgerus.betaplus.BetaPlusPlugin;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public final class DummyBukkitChunkGeneratorWCM extends ChunkGenerator
{
	public final boolean PAPER_ASYNC_SAFE = true;
	private final BetaPlusInjectPlugin impl;

	public DummyBukkitChunkGeneratorWCM(BetaPlusInjectPlugin impl) {
		this.impl = (BetaPlusInjectPlugin) Objects.requireNonNull(impl);
	}

	public ChunkData generateChunkData(World world, Random random, int x, int z, BiomeGrid biome) {
		throw new UnsupportedOperationException("This is a dummy class, used because a custom world generator was registered, but no base chunk generator has been set. Please use WorldGenerator.setBaseChunkGenerator(...).");
	}

	public List<BlockPopulator> getDefaultPopulators(World world) {
		this.impl.getForWorld(world);
		return new ArrayList();
	}

	public boolean isParallelCapable() {
		return true;
	}
}

