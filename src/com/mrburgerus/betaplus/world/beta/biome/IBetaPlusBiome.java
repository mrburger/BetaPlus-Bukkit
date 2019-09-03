package com.mrburgerus.betaplus.world.beta.biome;

import net.minecraft.server.v1_14_R1.BiomeBase;

public interface IBetaPlusBiome
{
	String name();

	BiomeBase getHandle();

	void setHandle(BiomeBase handle);
}
