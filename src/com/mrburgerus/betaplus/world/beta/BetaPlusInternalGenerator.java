package com.mrburgerus.betaplus.world.beta;

import com.google.common.annotations.Beta;
import com.mrburgerus.betaplus.BetaPlus;
import com.mrburgerus.betaplus.util.BiomeReplaceUtil;
import com.mrburgerus.betaplus.util.ChunkBuffer;
import com.mrburgerus.betaplus.util.DeepenOceanUtil;
import com.mrburgerus.betaplus.world.ConvertBetaPlusWorld;
import com.mrburgerus.betaplus.world.beta.biome.EnumBetaPlusBiome;
import com.mrburgerus.betaplus.world.noise.NoiseGeneratorOctavesBeta;
import net.minecraft.server.v1_14_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.v1_14_R1.generator.CustomChunkGenerator;
import org.bukkit.generator.ChunkGenerator;

import java.util.Random;
import java.util.logging.Level;

public class BetaPlusInternalGenerator extends CustomChunkGenerator
{
	// Fields
	private Random rand;
	private BiomeBase[] biomesForGeneration;
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
	private BiomeProviderBetaPlus biomeProviderS;
	private final MobSpawnerPhantom phantomSpawner = new MobSpawnerPhantom();
	private static final int CHUNK_SIZE = 16;

	public BetaPlusInternalGenerator(World world, ChunkGenerator generator, WorldChunkManager worldChunkManager)
	{
		super(world, world.getSeed(), generator);

		rand = new Random(world.getSeed());
		octaves1 = new NoiseGeneratorOctavesBeta(rand, 16);
		octaves2 = new NoiseGeneratorOctavesBeta(rand, 16);
		octaves3 = new NoiseGeneratorOctavesBeta(rand, 8);
		beachBlockNoise = new NoiseGeneratorOctavesBeta(rand, 4);
		surfaceNoise = new NoiseGeneratorOctavesBeta(rand, 4);
		scaleNoise = new NoiseGeneratorOctavesBeta(rand, 10);
		octaves7 = new NoiseGeneratorOctavesBeta(rand, 16);
		//TESTING
		biomeProviderS = (BiomeProviderBetaPlus) worldChunkManager;
	}

	@Override
	public int getSeaLevel()
	{
		// Add CONFIG OPTION
		return BetaPlus.seaLevel;
	}

	@Override
	public int getBaseHeight(int i, int i1, HeightMap.Type type)
	{
		return BetaPlus.seaLevel;
	}

	public void generateChunk(ChunkBuffer buffer)
	{
		int x = buffer.getCoords().x;
		int z = buffer.getCoords().z;
		rand.setSeed((long) x * 341873128712L + (long) z * 132897987541L);
		// Similar to ChunkGeneratorOverworld
		biomesForGeneration = biomeProviderS.getBiomes(x * 16, z * 16, 16, 16);
		// Written similarly to "generateTerrain" from earlier versions.
		this.setBlocksInChunkBuffer(buffer);
		// Scale factor formerly 2.85
		DeepenOceanUtil.deepenOceanBuffer(buffer, rand, BetaPlus.seaLevel, 7, 3.25);
		// Replace Biomes (Oceans)
		// This is because detection of Oceans is an average operation.
		this.replaceBiomesBuffer(buffer);

		// Replace Blocks (DIRT & SAND & STUFF)
		this.replaceBlocksForBiomeBuffer(x, z, buffer, EnumBetaPlusBiome.convertBiomeTable(biomesForGeneration));
		// Replace Beaches, done afterwards.
		this.replaceBeachesBuffer(buffer);

		// Set Biomes

		//chunkIn.a(BiomeReplaceUtil.convertBiomeArray(biomesForGeneration));
	}


	@Override
	public void buildBase(IChunkAccess chunkIn)
	{
		// Get Position
		int x = chunkIn.getPos().x;
		int z = chunkIn.getPos().z;
		// Functions As setBaseChunkSeed(), but broken down.
		rand.setSeed((long) x * 341873128712L + (long) z * 132897987541L);
		// Similar to ChunkGeneratorOverworld
		biomesForGeneration = biomeProviderS.getBiomes(x * 16, z * 16, 16, 16);
		// Written similarly to "generateTerrain" from earlier versions.
		setBlocksInChunk(chunkIn);
		// Scale factor formerly 2.85
		DeepenOceanUtil.deepenOcean(chunkIn, rand, BetaPlus.seaLevel, 7, 3.25);
		// Replace Biomes (Oceans)
		// This is because detection of Oceans is an average operation.
		this.replaceBiomes(chunkIn);

		// Replace Blocks (DIRT & SAND & STUFF)
		replaceBlocksForBiome(x, z, chunkIn, EnumBetaPlusBiome.convertBiomeTable(biomesForGeneration));
		// Replace Beaches, done afterwards.
		this.replaceBeaches(chunkIn);

		// Set Biomes

		chunkIn.a(BiomeReplaceUtil.convertBiomeArray(biomesForGeneration));
		BetaPlus.log.log(Level.ALL, "Use: " +  chunkIn.getChunkStatus().toString());
	}

	@Override
	public int getSpawnHeight()
	{
		return BetaPlus.seaLevel;
	}

	@Override
	public void buildNoise(GeneratorAccess generatorAccess, IChunkAccess iChunkAccess)
	{

	}

	private void setBlocksInChunk(IChunkAccess chunk)
	{
		heightNoise = octaveGenerator(heightNoise, chunk.getPos().x * 4, chunk.getPos().z * 4, 5, 17, 5);
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
								Block block = null;
								if (y < BetaPlus.seaLevel)
								{
									block = Blocks.WATER;
								}
								if (var48 > 0.0)
								{
									block = Blocks.STONE;
								}
								if (block != null)
								{
									chunk.a(new BlockPosition(x, y, z), block.a(getWorld()), false);
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

	private void setBlocksInChunkBuffer(ChunkBuffer buffer)
	{
		heightNoise = octaveGenerator(heightNoise, buffer.getCoords().x * 4, buffer.getCoords().z * 4, 5, 17, 5);
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
								org.bukkit.Material block = null;
								if (y < BetaPlus.seaLevel)
								{
									block = org.bukkit.Material.WATER;
								}
								if (var48 > 0.0)
								{
									block = org.bukkit.Material.STONE;
								}
								if (block != null)
								{
									//chunk.a(new BlockPosition(x, y, z), block.a(getWorld()), false);
									buffer.setBlock(x, y, z, Bukkit.createBlockData(block));
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

	//Replace Biomes where necessary
	private void replaceBiomes(IChunkAccess iChunk)
	{
		for (int z = 0; z < CHUNK_SIZE; ++z)
		{
			for (int x = 0; x < CHUNK_SIZE; ++x)
			{
				int xPos = iChunk.getPos().d() + x;
				int zPos = iChunk.getPos().e() + z;
				int yVal = BiomeReplaceUtil.getSolidHeightY(new BlockPosition(xPos, 0, zPos), iChunk);
				if (yVal > 104) // TODO: REPLACE
				{
					biomesForGeneration[(x << 4 | z)] = EnumBetaPlusBiome.mountain.handle;
				}
				else if (yVal < BetaPlus.seaLevel - 1)
				{

					if (yVal < BetaPlus.seaLevel - BetaPlus.seaDepth)
					{
						biomesForGeneration[(x << 4 | z)] = biomeProviderS.getOceanBiome(new BlockPosition(xPos, yVal, zPos), true);
					}
					else
					{
						biomesForGeneration[(x << 4 | z)] = biomeProviderS.getOceanBiome(new BlockPosition(xPos, yVal, zPos), false);
					}
				}
			}
		}
	}

	private void replaceBiomesBuffer(ChunkBuffer buffer)
	{
		for (int z = 0; z < CHUNK_SIZE; ++z)
		{
			for (int x = 0; x < CHUNK_SIZE; ++x)
			{
				int xPos = buffer.getCoords().d() + x;
				int zPos = buffer.getCoords().e() + z;
				int yVal = BiomeReplaceUtil.getSolidHeightY(new BlockPosition(xPos, 0, zPos), buffer);
				if (yVal > 104) // TODO: REPLACE
				{
					biomesForGeneration[(x << 4 | z)] = EnumBetaPlusBiome.mountain.handle;
				}
				else if (yVal < BetaPlus.seaLevel - 1)
				{

					if (yVal < BetaPlus.seaLevel - BetaPlus.seaDepth)
					{
						biomesForGeneration[(x << 4 | z)] = biomeProviderS.getOceanBiome(new BlockPosition(xPos, yVal, zPos), true);
					}
					else
					{
						biomesForGeneration[(x << 4 | z)] = biomeProviderS.getOceanBiome(new BlockPosition(xPos, yVal, zPos), false);
					}
				}
			}
		}
	}

	private void replaceBeaches(IChunkAccess chunk)
	{
		for (int z = 0; z < CHUNK_SIZE; ++z)
		{

			for (int x = 0; x < CHUNK_SIZE; ++x)
			{
				int xPos = chunk.getPos().d() + x;
				int zPos = chunk.getPos().e() + z;
				int yVal = BiomeReplaceUtil.getSolidHeightY(new BlockPosition(xPos, 0, zPos), chunk);
				// New Line
				BiomeBase biome = biomesForGeneration[(x << 4 | z)];
				//Inject Beaches (MODIFIED)
				if ((yVal <= (BetaPlus.seaLevel + 1) && yVal >= BetaPlus.seaLevel - 1) && (biome != EnumBetaPlusBiome.desert.handle) && chunk.getType(new BlockPosition(xPos, yVal, zPos)) == Blocks.SAND.getBlockData())
				{
					this.biomesForGeneration[(x << 4 | z)] = biomeProviderS.getBeachBiome(new BlockPosition(xPos, yVal, zPos));
				}
			}
		}
	}

	private void replaceBeachesBuffer(ChunkBuffer buffer)
	{
		for (int z = 0; z < CHUNK_SIZE; ++z)
		{

			for (int x = 0; x < CHUNK_SIZE; ++x)
			{
				int xPos = buffer.getCoords().d() + x;
				int zPos = buffer.getCoords().e() + z;
				int yVal = BiomeReplaceUtil.getSolidHeightY(new BlockPosition(xPos, 0, zPos), buffer);
				// New Line
				BiomeBase biome = biomesForGeneration[(x << 4 | z)];
				//Inject Beaches (MODIFIED)
				// chunk.getType(new BlockPosition(xPos, yVal, zPos))
				if ((yVal <= (BetaPlus.seaLevel + 1) && yVal >= BetaPlus.seaLevel - 1) && (biome != EnumBetaPlusBiome.desert.handle) &&  buffer.getBlock(xPos, yVal, zPos) == Bukkit.createBlockData(org.bukkit.Material.SAND))
				{
					this.biomesForGeneration[(x << 4 | z)] = biomeProviderS.getBeachBiome(new BlockPosition(xPos, yVal, zPos));
				}
			}
		}
	}

	/* 1.14, COPY TRI-GALOO */
	private double[] octaveGenerator(double[] values, int xPos, int zPos, int var5, int var6, int var7)
	{
		if (values == null)
		{
			values = new double[var5 * var6 * var7];
		}
		double noiseFactor = 684.412;
		double[] temps = biomeProviderS.temperatures;
		double[] humidities = biomeProviderS.humidities;
		octaveArr4 = scaleNoise.generateNoiseOctaves(octaveArr4, xPos, zPos, var5, var7, 1.121, 1.121, 0.5);
		octaveArr5 = octaves7.generateNoiseOctaves(octaveArr5, xPos, zPos, var5, var7, 200.0, 200.0, 0.5);
		octaveArr1 = octaves3.generateNoiseOctaves(octaveArr1, xPos, 0, zPos, var5, var6, var7, noiseFactor / 80.0, noiseFactor / 160.0, noiseFactor / 80.0);
		octaveArr2 = octaves1.generateNoiseOctaves(octaveArr2, xPos, 0, zPos, var5, var6, var7, noiseFactor, noiseFactor, noiseFactor);
		octaveArr3 = octaves2.generateNoiseOctaves(octaveArr3, xPos, 0, zPos, var5, var6, var7, noiseFactor, noiseFactor, noiseFactor);
		int incrementer1 = 0;
		int incrementer2 = 0;
		int var16 = 16 / var5;
		for (int i = 0; i < var5; ++i)
		{
			int var18 = i * var16 + var16 / 2;
			for (int j = 0; j < var7; ++j)
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
				var29 = var29 * (double) var6 / 16.0;
				double var31 = (double) var6 / 2.0 + var29 * 4.0;
				++incrementer2;
				for (int k = 0; k < var6; ++k)
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
					if (k > var6 - 4)
					{
						double var44 = (float) (k - (var6 - 4)) / 3.0f;
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
	private void replaceBlocksForBiome(int chunkX, int chunkZ, IChunkAccess chunkprimer, EnumBetaPlusBiome[] biomes)
	{
		double thirtySecond = 0.03125;
		this.sandNoise = this.beachBlockNoise.generateNoiseOctaves(this.sandNoise, chunkX * 16, chunkZ * 16, 0.0, 16, 16, 1, thirtySecond, thirtySecond, 1.0);
		this.gravelNoise = this.beachBlockNoise.generateNoiseOctaves(this.gravelNoise, chunkX * 16, 109.0134, chunkZ * 16, 16, 1, 16, thirtySecond, 1.0, thirtySecond);
		this.stoneNoise = this.surfaceNoise.generateNoiseOctaves(this.stoneNoise, chunkX * 16, chunkZ * 16, 0.0, 16, 16, 1, thirtySecond * 2.0, thirtySecond * 2.0, thirtySecond * 2.0);
		for (int z = 0; z < 16; ++z)
		{
			for (int x = 0; x < 16; ++x)
			{
				EnumBetaPlusBiome biome = biomes[z + x * 16];
				boolean sandN = this.sandNoise[z + x * 16] + this.rand.nextDouble() * 0.2 > 0.0;
				boolean gravelN = this.gravelNoise[z + x * 16] + this.rand.nextDouble() * 0.2 > 3.0;
				int stoneN = (int) (this.stoneNoise[z + x * 16] / 3.0 + 3.0 + this.rand.nextDouble() * 0.25);
				int checkVal = -1;
				BlockData topBlock = biome.topBlock;
				BlockData fillerBlock = biome.fillerBlock;

				// GO from Top to bottom of world
				for (int y = 127; y >= 0; --y)
				{
					if (y <= this.rand.nextInt(5))
					{
						//TESTING
						chunkprimer.a(new BlockPosition(x, y, z), Blocks.BEDROCK.a(getWorld()), false);
					}
					else
					{
						BlockPosition posUse = new BlockPosition(x, y, z);
						Block block = chunkprimer.getType(new BlockPosition(x, y, z)).getBlock();

						if (block == Blocks.AIR)
						{
							checkVal = -1;
							continue;
						}

						//Checks if model already changed
						if (block != Blocks.STONE) continue;

						if (checkVal == -1)
						{
							if (stoneN <= 0)
							{
								topBlock = Bukkit.createBlockData(org.bukkit.Material.AIR);
								fillerBlock = Bukkit.createBlockData(org.bukkit.Material.STONE);
							}
							else if (y >= BetaPlus.seaLevel - 4 && y <= BetaPlus.seaLevel + 1)
							{
								topBlock = biome.topBlock;
								fillerBlock = biome.fillerBlock;
								if (gravelN)
								{
									topBlock = Bukkit.createBlockData(org.bukkit.Material.AIR);
									fillerBlock = Bukkit.createBlockData(org.bukkit.Material.GRAVEL);
								}
								if (sandN)
								{
									topBlock = Bukkit.createBlockData(org.bukkit.Material.SAND);
									fillerBlock = Bukkit.createBlockData(org.bukkit.Material.SAND);
								}
							}
							if (y < BetaPlus.seaLevel && topBlock == Blocks.AIR.getBlockData())
							{
								topBlock = Bukkit.createBlockData(org.bukkit.Material.WATER);
							}

							// Sets top & filler Blocks
							checkVal = stoneN;
							// Test this still.
							if (y >= BetaPlus.seaLevel -1)
							{
								//chunkprimer.a(posUse, topBlock.a(getWorld(), posUse), false);
							}
							else
							{
								//chunkprimer.a(posUse, fillerBlock.a(getWorld(), posUse), false);
							}
						}
						// Add Sandstone (NOT WORKING)
						else if (checkVal > 0)
						{
							--checkVal;
							//chunkprimer.a(posUse, fillerBlock.a(getWorld(), posUse), false);
							//Possibly state comparison fucked it
							if (checkVal == 0 && fillerBlock == Blocks.SAND.getBlockData())
							{
								checkVal = this.rand.nextInt(4);
								//fillerBlock = Blocks.SANDSTONE.getBlockData();
							}
						} //END OF Y LOOP
					}
				}
			}
		}
	}

	private void replaceBlocksForBiomeBuffer(int chunkX, int chunkZ, ChunkBuffer buffer, EnumBetaPlusBiome[] biomes)
	{
		double thirtySecond = 0.03125;
		this.sandNoise = this.beachBlockNoise.generateNoiseOctaves(this.sandNoise, chunkX * 16, chunkZ * 16, 0.0, 16, 16, 1, thirtySecond, thirtySecond, 1.0);
		this.gravelNoise = this.beachBlockNoise.generateNoiseOctaves(this.gravelNoise, chunkX * 16, 109.0134, chunkZ * 16, 16, 1, 16, thirtySecond, 1.0, thirtySecond);
		this.stoneNoise = this.surfaceNoise.generateNoiseOctaves(this.stoneNoise, chunkX * 16, chunkZ * 16, 0.0, 16, 16, 1, thirtySecond * 2.0, thirtySecond * 2.0, thirtySecond * 2.0);
		for (int z = 0; z < 16; ++z)
		{
			for (int x = 0; x < 16; ++x)
			{
				EnumBetaPlusBiome biome = biomes[z + x * 16];
				boolean sandN = this.sandNoise[z + x * 16] + this.rand.nextDouble() * 0.2 > 0.0;
				boolean gravelN = this.gravelNoise[z + x * 16] + this.rand.nextDouble() * 0.2 > 3.0;
				int stoneN = (int) (this.stoneNoise[z + x * 16] / 3.0 + 3.0 + this.rand.nextDouble() * 0.25);
				int checkVal = -1;
				BlockData topBlock = biome.topBlock;
				BlockData fillerBlock = biome.fillerBlock;

				// GO from Top to bottom of world
				for (int y = 127; y >= 0; --y)
				{
					if (y <= this.rand.nextInt(5))
					{
						//TESTING
						buffer.setBlock(new BlockPosition(x, y, z), Bukkit.createBlockData(org.bukkit.Material.BEDROCK));
					}
					else
					{
						BlockPosition posUse = new BlockPosition(x, y, z);
						BlockData block = buffer.getBlock(x, y, z);

						if (block == Blocks.AIR)
						{
							checkVal = -1;
							continue;
						}

						//Checks if model already changed
						if (block != Blocks.STONE) continue;

						if (checkVal == -1)
						{
							if (stoneN <= 0)
							{
								topBlock = Bukkit.createBlockData(org.bukkit.Material.AIR);
								fillerBlock = Bukkit.createBlockData(org.bukkit.Material.STONE);
							}
							else if (y >= BetaPlus.seaLevel - 4 && y <= BetaPlus.seaLevel + 1)
							{
								topBlock = biome.topBlock;
								fillerBlock = biome.fillerBlock;
								if (gravelN)
								{
									topBlock = Bukkit.createBlockData(org.bukkit.Material.AIR);
									fillerBlock = Bukkit.createBlockData(org.bukkit.Material.GRAVEL);
								}
								if (sandN)
								{
									topBlock = Bukkit.createBlockData(org.bukkit.Material.SAND);
									fillerBlock = Bukkit.createBlockData(org.bukkit.Material.SAND);
								}
							}
							if (y < BetaPlus.seaLevel && topBlock == Blocks.AIR.getBlockData())
							{
								topBlock = Bukkit.createBlockData(org.bukkit.Material.WATER);
							}

							// Sets top & filler Blocks
							checkVal = stoneN;
							// Test this still.
							if (y >= BetaPlus.seaLevel -1)
							{
								//chunkprimer.a(posUse, topBlock.a(getWorld(), posUse), false);
								buffer.setBlock(posUse, topBlock);
							}
							else
							{
								//chunkprimer.a(posUse, fillerBlock.a(getWorld(), posUse), false);
								buffer.setBlock(posUse, fillerBlock);
							}
						}
						// Add Sandstone (NOT WORKING)
						else if (checkVal > 0)
						{
							--checkVal;
							//chunkprimer.a(posUse, fillerBlock.a(getWorld(), posUse), false);
							buffer.setBlock(posUse, fillerBlock);
							//Possibly state comparison fucked it
							if (checkVal == 0 && fillerBlock == Blocks.SAND.getBlockData())
							{
								checkVal = this.rand.nextInt(4);
								fillerBlock = Bukkit.createBlockData(org.bukkit.Material.SANDSTONE);
							}
						} //END OF Y LOOP
					}
				}
			}
		}
	}
}
