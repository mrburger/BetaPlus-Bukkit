package com.mrburgerus.beta_plus.util;

import com.sun.tools.javac.util.Pair;

/* Just an Interface so I remember to implement Methods */
/* Simulators Make EVERYTHING FUN! */
public interface IWorldSimulator
{
	/* Simulates a Y (Quickly) in chunk, usually by generating values every 4 Material and Averaging */
	/* Return: The Y average of the chunk and whether any values fall above sea level */
	Pair<Integer, Boolean> simulateYChunk(int x, int z);

	/* Simulates an "Averaged" Value, which is usually 3x3 chunks. */
	/* Return: The Y average and whether any values fall above sea level */
	Pair<Integer, Boolean> simulateYAvg(int x, int z);


}
