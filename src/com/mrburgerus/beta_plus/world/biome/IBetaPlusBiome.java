package com.mrburgerus.beta_plus.world.biome;

import org.bukkit.block.Biome;

public interface IBetaPlusBiome
{
	String name();

	Biome getHandle();

	void setHandle(Biome handle);
}
