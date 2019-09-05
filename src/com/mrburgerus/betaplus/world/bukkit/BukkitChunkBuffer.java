package com.mrburgerus.betaplus.world.bukkit;

import net.minecraft.server.v1_14_R1.BlockPosition;
import net.minecraft.server.v1_14_R1.ChunkCoordIntPair;
import org.bukkit.block.data.BlockData;
import org.bukkit.generator.ChunkGenerator;

public class BukkitChunkBuffer
{
	private final ChunkCoordIntPair chunkCoord;
	private final ChunkGenerator.ChunkData chunkData;

	public BukkitChunkBuffer(ChunkCoordIntPair chunkCoord, ChunkGenerator.ChunkData chunkData)
	{
		this.chunkCoord = chunkCoord;
		this.chunkData = chunkData;
	}

	public ChunkCoordIntPair getCoords()
	{
		return chunkCoord;
	}

	public void setBlock(int x, int y, int z, BlockData data)
	{
		chunkData.setBlock(x, y, z, data);
	}

	public void setBlock(BlockPosition pos, BlockData data)
	{
		chunkData.setBlock(pos.getX(), pos.getY(), pos.getZ(), data);
	}

	public BlockData getBlock(int x, int y, int z)
	{
		return chunkData.getBlockData(x, y, z); //((Block.a(world, newP BlockPosition(x, y, z))).getNMS().getBlock();
		//return chunkData.getBlockData(x, y, z);
	}
}
