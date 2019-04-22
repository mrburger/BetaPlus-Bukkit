package com.mrburgerus.beta_plus.world.biome;

import net.minecraft.server.v1_13_R2.BiomeBase;

public interface IBetaPlusBiome
{
	String name();

	BiomeBase getHandle();

	void setHandle(BiomeBase handle);
}
