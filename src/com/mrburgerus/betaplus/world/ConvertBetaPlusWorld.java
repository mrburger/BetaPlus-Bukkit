package com.mrburgerus.betaplus.world;

import com.mrburgerus.betaplus.BetaPlusPlugin;
import net.minecraft.server.v1_14_R1.IChunkAccess;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.generator.ChunkGenerator;

import java.util.logging.Level;

import static org.bukkit.Bukkit.createChunkData;


// NOT WORKING
public class ConvertBetaPlusWorld
{
	public static ChunkGenerator.ChunkData convertChunkAccess(World world, IChunkAccess chunkIn)
	{
		BetaPlusPlugin.LOGGER.log(Level.CONFIG, "Create Data");
		ChunkGenerator.ChunkData chunkData = createChunkData(world);
		BetaPlusPlugin.LOGGER.log(Level.CONFIG, "Finished Create Data");
		// Iterate over
		for (int px = 0; px < 16; px++)
		{
			for (int pz = 0; pz < 16; pz++)
			{
				for (int py = 0; py < 256; py++)
				{
					//CraftBlock cBLock = (CraftBlock) chunkIn.getType(new BlockPosition(px, py, pz));
					BetaPlusPlugin.LOGGER.log(Level.CONFIG, "Convert  " + px + ", " + py + ", " + pz);
					BlockData cBlock = new Location(world, px, py, pz).getBlock().getBlockData();
					chunkData.setBlock(px, py, pz, cBlock);
				}
			}
		}
		BetaPlusPlugin.LOGGER.log(Level.CONFIG, "Finished Convert");
		return chunkData;
	}
}
