package com.mrburgerus.betaplus.util;


import net.minecraft.server.v1_14_R1.BlockPosition;
import net.minecraft.server.v1_14_R1.ChunkCoordIntPair;
import org.bukkit.block.data.BlockData;

public interface ChunkBuffer
{
	ChunkCoordIntPair getCoords();

	void setBlock(int x, int y, int z, BlockData material);

	void setBlock(BlockPosition pos, BlockData material);

	BlockData getBlock(int x, int y, int z);
}
