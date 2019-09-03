package com.mrburgerus.betaplus.world;

import com.mrburgerus.betaplus.util.ChunkBuffer;
import net.minecraft.server.v1_14_R1.*;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.generator.ChunkGenerator;

public class BukkitChunkBuffer implements ChunkBuffer
{
	private final ChunkCoordIntPair chunkCoord;
	private final ChunkGenerator.ChunkData chunkData;
	private final World world;

	public BukkitChunkBuffer(ChunkCoordIntPair chunkCoord, ChunkGenerator.ChunkData chunkData, World world)
	{
		this.chunkCoord = chunkCoord;
		this.chunkData = chunkData;
		this.world = world;
	}

	@Override
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
		return chunkData.getBlockData(x, y, z); //((Block.a(world, new BlockPosition(x, y, z))).getNMS().getBlock();
		//return chunkData.getBlockData(x, y, z);
	}
}
