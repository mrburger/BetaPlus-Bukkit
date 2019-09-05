package com.mrburgerus.betaplus.newP;

import nl.rutgerkok.worldgeneratorapi.BaseChunkGenerator;
import nl.rutgerkok.worldgeneratorapi.BaseTerrainGenerator;

public class DummyBaseTerrainGenerator implements BaseTerrainGenerator
{
	@Override
	public int getHeight(int i, int i1, HeightType heightType)
	{
		return 0;
	}

	@Override
	public void setBlocksInChunk(GeneratingChunk generatingChunk)
	{

	}
}
