package com.mrburgerus.betaplus.world.beta.beta_api;

import com.mrburgerus.betaplus.BetaPlusPlugin;
import com.mrburgerus.betaplus.world.noise.NoiseGeneratorOctavesBeta;
import com.mrburgerus.betaplus.world.util.DeepenOceanUtil;
import net.minecraft.server.v1_14_R1.*;
import nl.rutgerkok.worldgeneratorapi.BaseTerrainGenerator;
import nl.rutgerkok.worldgeneratorapi.BiomeGenerator;
import nl.rutgerkok.worldgeneratorapi.internal.BaseTerrainGeneratorImpl;
import nl.rutgerkok.worldgeneratorapi.internal.bukkitoverrides.InjectedChunkGenerator;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.v1_14_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_14_R1.block.CraftBlock;
import org.bukkit.craftbukkit.v1_14_R1.util.CraftMagicNumbers;
import org.bukkit.generator.ChunkGenerator;

import java.util.Objects;
import java.util.Random;

public class BetaPlusTerrainGenerator implements BaseTerrainGenerator
{
	private Random rand;
	private Biome[] biomesForGeneration;
	//Noise Generators
	private NoiseGeneratorOctavesBeta octaves1;
	private NoiseGeneratorOctavesBeta octaves2;
	private NoiseGeneratorOctavesBeta octaves3;
	private NoiseGeneratorOctavesBeta beachBlockNoise; // Formerly scaleNoise, used for Gravel and Sand, so probably beaches.
	private NoiseGeneratorOctavesBeta surfaceNoise; // Formerly octaves7
	private NoiseGeneratorOctavesBeta scaleNoise; // Formerly octaves6, renamed using ChunkGeneratorOverworld
	private NoiseGeneratorOctavesBeta octaves7;
	//Noise Arrays
	private double[] octaveArr1;
	private double[] octaveArr2;
	private double[] octaveArr3;
	private double[] octaveArr4;
	private double[] octaveArr5;
	private double[] heightNoise;
	private double[] sandNoise = new double[256];
	private double[] gravelNoise = new double[256];
	private double[] stoneNoise = new double[256];
	// New Fields
	private BetaPlusBiomeGenerator biomeProviderS;
	//private final BetaPlusGenSettings settings;
	public static final int CHUNK_SIZE = 16;

	// NEW //
	//private final net.minecraft.server.v1_14_R1.ChunkGenerator<?> internal;
	//private final GeneratorAccess world;

	public BetaPlusTerrainGenerator(GeneratorAccess world, net.minecraft.server.v1_14_R1.ChunkGenerator<?> chunkGenerator) {
		if (chunkGenerator instanceof InjectedChunkGenerator) {
			throw new IllegalArgumentException("Double-wrapping");
		} else {
			//this.world = (GeneratorAccess)Objects.requireNonNull(world, "world");
			//this.internal = (net.minecraft.server.v1_14_R1.ChunkGenerator)Objects.requireNonNull(chunkGenerator, "internal");
		}

		long seed  = world.getSeed();
		rand = new Random(seed);
		octaves1 = new NoiseGeneratorOctavesBeta(rand, 16);
		octaves2 = new NoiseGeneratorOctavesBeta(rand, 16);
		octaves3 = new NoiseGeneratorOctavesBeta(rand, 8);
		beachBlockNoise = new NoiseGeneratorOctavesBeta(rand, 4);
		surfaceNoise = new NoiseGeneratorOctavesBeta(rand, 4);
		scaleNoise = new NoiseGeneratorOctavesBeta(rand, 10);
		octaves7 = new NoiseGeneratorOctavesBeta(rand, 16);
		biomeProviderS = new BetaPlusBiomeGenerator(world.getSeed());
	}

	public BetaPlusTerrainGenerator(World world)
	{
		long seed  = world.getSeed();
		rand = new Random(seed);
		octaves1 = new NoiseGeneratorOctavesBeta(rand, 16);
		octaves2 = new NoiseGeneratorOctavesBeta(rand, 16);
		octaves3 = new NoiseGeneratorOctavesBeta(rand, 8);
		beachBlockNoise = new NoiseGeneratorOctavesBeta(rand, 4);
		surfaceNoise = new NoiseGeneratorOctavesBeta(rand, 4);
		scaleNoise = new NoiseGeneratorOctavesBeta(rand, 10);
		octaves7 = new NoiseGeneratorOctavesBeta(rand, 16);
		biomeProviderS = new BetaPlusBiomeGenerator(world.getSeed());
	}

	// Much like the getHeight from Forge.
	@Override
	public int getHeight(int x, int z, HeightType heightType)
	{
		int[][] valuesInChunk = biomeProviderS.simulator.simulateChunkYFull(new ChunkCoordIntPair(new BlockPosition(x, 0, z))).getFirst();
		// Working!
		int yRet = valuesInChunk[x & 0x000F][z & 0x000F];
		if (yRet < BetaPlusPlugin.seaLevel)
		{
			yRet = BetaPlusPlugin.seaLevel + 1;
		}
		return yRet;
	}

	@Override
	public void setBlocksInChunk(GeneratingChunk generatingChunk)
	{
		int x = generatingChunk.getChunkX();
		int z = generatingChunk.getChunkZ();
		rand.setSeed((long) x * 341873128712L + (long) z * 132897987541L);
		biomesForGeneration = biomeProviderS.getBiomes(x * 16, z * 16, 16, 16, false);

		this.setMaterial(generatingChunk);
		DeepenOceanUtil.deepenOcean(generatingChunk, rand, BetaPlusPlugin.seaLevel, 7, BetaPlusPlugin.oceanYScale);

		//generatingChunk.getBiomeGenerator().getZoomedOutBiome(BiomeReplaceUtil.convertBiomeArray(biomesForGeneration));
		this.replaceMaterialForBiome(x, z, generatingChunk, biomesForGeneration);

		ChunkGenerator.BiomeGrid biomes = generatingChunk.getBiomesForChunk();
		// Chunk Size ^2 is the size of the grid
		for (int r = 0; r < CHUNK_SIZE; ++r)
		{
			for (int c = 0; c < CHUNK_SIZE; ++ c)
			{
				// Testing
				int i = c + r * CHUNK_SIZE;
				//Set Biomes
				biomes.setBiome(r, c, biomesForGeneration[(i & 15) << 4 | i >> 4 & 15]);
			}
		}
	}

	private void setMaterial(GeneratingChunk chunk)
	{
		heightNoise = octaveGenerator(heightNoise, chunk.getChunkX() * 4, chunk.getChunkZ() * 4);
		for (int i = 0; i < 4; ++i)
		{
			for (int j = 0; j < 4; ++j)
			{
				for (int k = 0; k < 16; ++k)
				{
					double eigth = 0.125;
					double var16 = heightNoise[((i) * 5 + j) * 17 + k];
					double var18 = heightNoise[((i) * 5 + j + 1) * 17 + k];
					double var20 = heightNoise[((i + 1) * 5 + j) * 17 + k];
					double var22 = heightNoise[((i + 1) * 5 + j + 1) * 17 + k];
					double var24 = (heightNoise[((i) * 5 + j) * 17 + k + 1] - var16) * eigth;
					double var26 = (heightNoise[((i) * 5 + j + 1) * 17 + k + 1] - var18) * eigth;
					double var28 = (heightNoise[((i + 1) * 5 + j) * 17 + k + 1] - var20) * eigth;
					double var30 = (heightNoise[((i + 1) * 5 + j + 1) * 17 + k + 1] - var22) * eigth;
					for (int l = 0; l < 8; ++l)
					{
						double quarter = 0.25;
						double var35 = var16;
						double var37 = var18;
						double var39 = (var20 - var16) * quarter;
						double var41 = (var22 - var18) * quarter;
						for (int m = 0; m < 4; ++m)
						{
							int x = m + i * 4;
							int y = k * 8 + l;
							int z = j * 4;
							double var46 = 0.25;
							double var48 = var35;
							double var50 = (var37 - var35) * var46;
							for (int n = 0; n < 4; ++n)
							{
								Material block = null;
								if (y < BetaPlusPlugin.seaLevel)
								{
									block = Material.WATER;
								}
								if (var48 > 0.0)
								{
									block = Material.STONE;
								}
								if (block != null)
								{
									chunk.getBlocksForChunk().setBlock(x, y, z, block.createBlockData());
								}
								++z;
								var48 += var50;
							}
							var35 += var39;
							var37 += var41;
						}
						var16 += var24;
						var18 += var26;
						var20 += var28;
						var22 += var30;
					}

				}
			}
		}
	}

	/* 1.14, BUKKIT BOOGALOO */
	private double[] octaveGenerator(double[] values, int xPos, int zPos)
	{
		int size1 = 5;
		int size2 = 17;
		int size3 = 5;
		if (values == null)
		{
			values = new double[size1 * size2 * size3];
		}
		double noiseFactor = 684.412D;
		double[] temps = biomeProviderS.temperatures;
		double[] humidities = biomeProviderS.humidities;
		octaveArr4 = scaleNoise.generateNoiseOctaves(octaveArr4, xPos, zPos, size1, size3, 1.121, 1.121, 0.5);
		octaveArr5 = octaves7.generateNoiseOctaves(octaveArr5, xPos, zPos, size1, size3, 200.0, 200.0, 0.5);
		octaveArr1 = octaves3.generateNoiseOctaves(octaveArr1, xPos, 0, zPos, size1, size2, size3, noiseFactor / 80.0, noiseFactor / 160.0, noiseFactor / 80.0);
		octaveArr2 = octaves1.generateNoiseOctaves(octaveArr2, xPos, 0, zPos, size1, size2, size3, noiseFactor, noiseFactor, noiseFactor);
		octaveArr3 = octaves2.generateNoiseOctaves(octaveArr3, xPos, 0, zPos, size1, size2, size3, noiseFactor, noiseFactor, noiseFactor);
		int incrementer1 = 0;
		int incrementer2 = 0;
		int var16 = 16 / size1;
		for (int i = 0; i < size1; ++i)
		{
			int var18 = i * var16 + var16 / 2;
			for (int j = 0; j < size3; ++j)
			{
				double var29;
				int var20 = j * var16 + var16 / 2;
				double var21 = temps[var18 * 16 + var20];
				double var23 = humidities[var18 * 16 + var20] * var21;
				double var25 = 1.0 - var23;
				var25 *= var25;
				var25 *= var25;
				var25 = 1.0 - var25;
				double var27 = (octaveArr4[incrementer2] + 256.0) / 512.0;
				if ((var27 *= var25) > 1.0)
				{
					var27 = 1.0;
				}
				if ((var29 = octaveArr5[incrementer2] / 8000.0) < 0.0)
				{
					var29 = (-var29) * 0.3;
				}
				if ((var29 = var29 * 3.0 - 2.0) < 0.0)
				{
					if ((var29 /= 2.0) < -1.0)
					{
						var29 = -1.0;
					}
					var29 /= 1.4;
					var29 /= 2.0;
					var27 = 0.0;
				}
				else
				{
					if (var29 > 1.0)
					{
						var29 = 1.0;
					}
					var29 /= 8.0;
				}
				if (var27 < 0.0)
				{
					var27 = 0.0;
				}
				var27 += 0.5;
				var29 = var29 * (double) size2 / 16.0;
				double var31 = (double) size2 / 2.0 + var29 * 4.0;
				++incrementer2;
				for (int k = 0; k < size2; ++k)
				{
					double var34;
					double var36 = ((double) k - var31) * 12.0 / var27;
					if (var36 < 0.0)
					{
						var36 *= 4.0;
					}
					double var38 = octaveArr2[incrementer1] / 512.0;
					double var40 = octaveArr3[incrementer1] / 512.0;
					double var42 = (octaveArr1[incrementer1] / 10.0 + 1.0) / 2.0;
					var34 = var42 < 0.0 ? var38 : (var42 > 1.0 ? var40 : var38 + (var40 - var38) * var42);
					var34 -= var36;
					if (k > size2 - 4)
					{
						double var44 = (float) (k - (size2 - 4)) / 3.0f;
						var34 = var34 * (1.0 - var44) + -10.0 * var44;
					}
					values[incrementer1] = var34;
					++incrementer1;
				}
			}
		}
		return values;
	}

	/* YES, IT IS COPIED AND MODIFIED FROM 1.12 */
	private void replaceMaterialForBiome(int chunkX, int chunkZ, GeneratingChunk chunk, Biome[] biomes)
	{
		ChunkGenerator.ChunkData data = chunk.getBlocksForChunk();
		double thirtySecond = 0.03125;
		this.sandNoise = this.beachBlockNoise.generateNoiseOctaves(this.sandNoise, chunkX * 16, chunkZ * 16, 0.0, 16, 16, 1, thirtySecond, thirtySecond, 1.0);
		this.gravelNoise = this.beachBlockNoise.generateNoiseOctaves(this.gravelNoise, chunkX * 16, 109.0134, chunkZ * 16, 16, 1, 16, thirtySecond, 1.0, thirtySecond);
		this.stoneNoise = this.surfaceNoise.generateNoiseOctaves(this.stoneNoise, chunkX * 16, chunkZ * 16, 0.0, 16, 16, 1, thirtySecond * 2.0, thirtySecond * 2.0, thirtySecond * 2.0);
		for (int z = 0; z < 16; ++z)
		{
			for (int x = 0; x < 16; ++x)
			{
				BiomeBase biome = CraftBlock.biomeToBiomeBase(biomes[z + x * 16]);
				boolean sandN = this.sandNoise[z + x * 16] + this.rand.nextDouble() * 0.2 > 0.0;
				boolean gravelN = this.gravelNoise[z + x * 16] + this.rand.nextDouble() * 0.2 > 3.0;
				int stoneN = (int) (this.stoneNoise[z + x * 16] / 3.0 + 3.0 + this.rand.nextDouble() * 0.25);
				int checkVal = -1;
				// Changed to use the actual Biome Config.
				BlockData topBlock = CraftMagicNumbers.getMaterial(biome.q().a()).getItemType().createBlockData();
				BlockData fillerBlock = CraftMagicNumbers.getMaterial(biome.q().b()).getItemType().createBlockData();
				// GO from Top to bottom of world
				for (int y = 127; y >= 0; --y)
				{
					if (y <= this.rand.nextInt(5))
					{
						data.setBlock(x, y, z, Material.BEDROCK);
					}
					else
					{
						Material block = data.getBlockData(x, y, z).getMaterial();

						if (block == Material.AIR)
						{
							checkVal = -1;
							continue;
						}

						//Checks if model already changed
						if (block != Material.STONE) continue;

						if (checkVal == -1)
						{
							if (stoneN <= 0)
							{
								topBlock = Material.AIR.createBlockData();
								fillerBlock = Material.STONE.createBlockData();
							}
							else if (y >= BetaPlusPlugin.seaLevel - 4 && y <= BetaPlusPlugin.seaLevel + 1)
							{
								topBlock = CraftMagicNumbers.getMaterial(biome.q().a()).getItemType().createBlockData();
								fillerBlock = CraftMagicNumbers.getMaterial(biome.q().b()).getItemType().createBlockData();
								if (gravelN)
								{
									topBlock = Material.AIR.createBlockData();
									fillerBlock = Material.GRAVEL.createBlockData();
								}
								if (sandN)
								{
									topBlock = Material.SAND.createBlockData();
									fillerBlock = Material.SAND.createBlockData();
								}
							}
							if (y < BetaPlusPlugin.seaLevel && topBlock == Material.AIR.createBlockData())
							{
								topBlock = Material.WATER.createBlockData();
							}

							// Sets top & filler Material
							checkVal = stoneN;
							// Test this still.
							if (y >= BetaPlusPlugin.seaLevel -1)
							{
								data.setBlock(x, y, z, topBlock);
							}
							else
							{
								data.setBlock(x, y, z, fillerBlock);
							}
						}
						// Add Sandstone (NOT WORKING)
						else if (checkVal > 0)
						{
							--checkVal;
							data.setBlock(x, y, z, fillerBlock);
							//Possibly state comparison fucked it
							if (checkVal == 0 && fillerBlock == Material.SAND.createBlockData())
							{
								checkVal = this.rand.nextInt(4);
								fillerBlock = Material.SANDSTONE.createBlockData();
							}
						} //END OF Y LOOP
					}
				}
			}
		}
	}

	// COPIED METHODS //

	static BaseTerrainGenerator fromMinecraft(World world) {
		WorldServer worldServer = ((CraftWorld)world).getHandle();
		net.minecraft.server.v1_14_R1.ChunkGenerator<?> chunkGenerator = worldServer.getChunkProvider().getChunkGenerator();
		if (chunkGenerator instanceof InjectedChunkGenerator) {
			return ((InjectedChunkGenerator)chunkGenerator).getBaseTerrainGenerator();
		} else if (isSupported(chunkGenerator)) {
			return new BetaPlusTerrainGenerator(worldServer, chunkGenerator);
		} else {
			throw new UnsupportedOperationException("Cannot extract base chunk generator from " + chunkGenerator.getClass() + ". \nYou can only customize worlds where the base terrain is generated by Minecraft or using the WorldGeneratorApi methods. If you are using the WorldGeneratorApi methods, make sure that a BaseChunkGenerator was set before other aspects of terrain generation were modified.");
		}
	}

	static boolean isSupported(net.minecraft.server.v1_14_R1.ChunkGenerator<?> chunkGenerator)
	{
		return chunkGenerator instanceof ChunkProviderDebug || chunkGenerator instanceof ChunkProviderFlat || chunkGenerator instanceof ChunkProviderGenerate || chunkGenerator instanceof ChunkProviderHell || chunkGenerator instanceof ChunkProviderTheEnd;
	}
}
