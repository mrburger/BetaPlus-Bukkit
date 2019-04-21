package com.mrburgerus.beta_plus.util;


/* Written to EASE the transition to Bukkit */
public class ChunkPos
{
	int xChunk;
	int zChunk;

	public ChunkPos(int xPos, int zPos)
	{
		xChunk = xPos >> 4;
		zChunk = zPos >> 4;
	}

	public int getX()
	{
		return xChunk;
	}

	public int getZ()
	{
		return zChunk;
	}
}
