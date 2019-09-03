package com.mrburgerus.betaplus.world;

import com.mrburgerus.betaplus.BetaPlus;
import net.minecraft.server.v1_14_R1.IChunkAccess;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.generator.ChunkGenerator;

import java.util.logging.Level;

import static org.bukkit.Bukkit.createChunkData;

public class ConvertBetaPlusWorld
{
	public static ChunkGenerator.ChunkData convertChunkAccess(World world, IChunkAccess chunkIn)
	{
		BetaPlus.LOGGER.log(Level.CONFIG, "Create Data");
		ChunkGenerator.ChunkData chunkData = createChunkData(world);
		BetaPlus.LOGGER.log(Level.CONFIG, "Finished Create Data");
		// Iterate over
		for (int px = 0; px < 16; px++)
		{
			for (int pz = 0; pz < 16; pz++)
			{
				for (int py = 0; py < 256; py++)
				{
					//CraftBlock cBLock = (CraftBlock) chunkIn.getType(new BlockPosition(px, py, pz));
					BetaPlus.LOGGER.log(Level.CONFIG, "Convert  " + px + ", " + py + ", " + pz);
					BlockData cBlock = new Location(world, px, py, pz).getBlock().getBlockData();
					chunkData.setBlock(px, py, pz, cBlock);
				}
			}
		}
		BetaPlus.LOGGER.log(Level.CONFIG, "Finished Convert");
		return chunkData;
	}
}
