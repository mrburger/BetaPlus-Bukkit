package com.mrburgerus.betaplus.world.beta_new.biome;

import org.bukkit.block.Biome;

public interface IBetaPlusBiome
{
	String name();

	Biome getHandle();

	void setHandle(Biome handle);
}
