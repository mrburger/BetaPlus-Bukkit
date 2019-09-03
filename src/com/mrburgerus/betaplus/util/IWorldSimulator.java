package com.mrburgerus.betaplus.util;

import com.mojang.datafixers.util.Pair;
import net.minecraft.server.v1_14_R1.BlockPosition;

/* Just an Interface so I remember to implement Methods */
/* Simulators Make EVERYTHING FUN! */
public interface IWorldSimulator
{
	/* Simulates a Y (Quickly) in chunk, usually by generating values every 4 blocks and Averaging */
	/* Return: The Y average of the chunk and whether any values fall above sea level */
	Pair<Integer, Boolean> simulateYChunk(BlockPosition pos);

	/* Simulates an "Averaged" Value, which is usually 3x3 chunks. */
	/* Return: The Y average and whether any values fall above sea level */
	Pair<Integer, Boolean> simulateYAvg(BlockPosition pos);


}
