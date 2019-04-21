package com.mrburgerus.beta_plus.world.beta_plus;

import com.mrburgerus.beta_plus.util.BiomeReplaceUtil;
import com.mrburgerus.beta_plus.util.DeepenOceanUtil;
import com.mrburgerus.beta_plus.world.beta_plus.populator.TreePopulator;
import com.mrburgerus.beta_plus.world.biome.EnumBetaPlusBiome;
import com.mrburgerus.beta_plus.world.noise.NoiseGeneratorOctavesBeta;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.bukkit.Material.STONE;
import static org.bukkit.Material.WATER;

public class BetaPlusGenerator extends ChunkGenerator
{
	// Fields
	private Random rand;
	private Biome[] biomesGenerate;
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
	public BiomeProviderBetaPlus biomeProviderS;

	private List<org.bukkit.generator.BlockPopulator> populatorList;
	// If the world has already been init
	private boolean hasEnabled = false;


	public BetaPlusGenerator()
	{

		// Could break stuff (IT DOES)
		//long seed = new Random().nextLong();

	}

	public void init(long seed, BiomeProviderBetaPlus biomeProvider)
	{
		rand = new Random(seed);

		octaves1 = new NoiseGeneratorOctavesBeta(rand, 16);
		octaves2 = new NoiseGeneratorOctavesBeta(rand, 16);
		octaves3 = new NoiseGeneratorOctavesBeta(rand, 8);
		beachBlockNoise = new NoiseGeneratorOctavesBeta(rand, 4);
		surfaceNoise = new NoiseGeneratorOctavesBeta(rand, 4);
		scaleNoise = new NoiseGeneratorOctavesBeta(rand, 10);
		octaves7 = new NoiseGeneratorOctavesBeta(rand, 16);
		biomeProviderS = biomeProvider;
	}

	@Override
	public List<BlockPopulator> getDefaultPopulators(World w) {
		List<BlockPopulator> populators = new ArrayList<>();
		populators.add(new TreePopulator());
		return populators;
	}

	@Override
	public ChunkData generateChunkData(World world, Random random, int x, int z, BiomeGrid biomeGrid)
	{
		// First do this
		this.createChunkData(world);
		// Do NOT create a Biome Proivder EVERY time!
		if (!hasEnabled)
		{
			this.init(world.getSeed(), new BiomeProviderBetaPlus(world.getSeed()));
			hasEnabled = true; // Tells it not to re-init
		}

		//Logic for creating terrain
		//System.out.println("X,Z: " + x + ", " + z);
		rand.setSeed((long) x * 341873128712L + (long) z * 132897987541L);
		ChunkData terrain = this.createChunkData(world);
		biomesGenerate = biomeProviderS.getBiome(x * 16, z * 16, 16, 16);
		setMaterialInChunk(x, z, terrain);
		DeepenOceanUtil.deepenOcean(terrain, rand, 64, 7, 3.25);

		//this.replaceBiome(x, z, terrain, world);
		replaceMaterialForBiome(x, z, world, terrain, EnumBetaPlusBiome.convertBiomeTable(biomesGenerate));

		// Replaces biomes (TESTING)
		int n = 0;
		Biome[] biomesList = BiomeReplaceUtil.convertBiomeArray(biomesGenerate);
		for(int localX = 0; localX < 16; ++localX) {
			for(int localZ = 0; localZ < 16; ++localZ) {
				Biome biome = biomesList[n];
				biomeGrid.setBiome(localX, localZ, biome);
				++n;
			}
		}


		return terrain;
	}

	/* GENERATES THE Material */
	// PREVIOUSLY other methods, updated for 1.13!
	private void setMaterialInChunk(int xC, int zC, ChunkData chunk)
	{
		heightNoise = octaveGenerator(heightNoise, xC * 4, zC * 4, 5, 17, 5);
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
								if (y < 64)
								{
									block = WATER;
								}
								if (var48 > 0.0)
								{
									block = STONE;
								}
								if (block != null)
								{
									chunk.setBlock(x, y, z, block);
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

	/* 1.13, COPY BOOGALOO */
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

	private void replaceBiome(int xC, int zC, ChunkData chunkData, World world)
	{
		for (int z = 0; z < 16; ++z)
		{
			for (int x = 0; x < 16; ++x)
			{
				int xPos = xC << 4 + x;
				int zPos = zC << 4 + z;
				int yVal = world.getHighestBlockYAt(xPos, zPos);
				if (yVal > 108)
				{
					biomesGenerate[(x << 4 | z)] = EnumBetaPlusBiome.mountain.handle;
				}
				else if (yVal < 64 - 1)
				{

					if (yVal < 64 - 20)
					{
						biomesGenerate[(x << 4 | z)] = biomeProviderS.getOceanBiome(xPos, zPos, true);
					}
					else
					{
						biomesGenerate[(x << 4 | z)] = biomeProviderS.getOceanBiome(xPos, zPos, false);
					}
				}
			}
		}
	}

	private void replaceMaterialForBiome(int chunkX, int chunkZ, World world, ChunkData chunkData, EnumBetaPlusBiome[] Biome)
	{
		//System.out.println("Replacing at: " + chunkX + ", " + chunkZ);
		double thirtySecond = 0.03125;
		this.sandNoise = this.beachBlockNoise.generateNoiseOctaves(this.sandNoise, chunkX * 16, chunkZ * 16, 0.0, 16, 16, 1, thirtySecond, thirtySecond, 1.0);
		this.gravelNoise = this.beachBlockNoise.generateNoiseOctaves(this.gravelNoise, chunkX * 16, 109.0134, chunkZ * 16, 16, 1, 16, thirtySecond, 1.0, thirtySecond);
		this.stoneNoise = this.surfaceNoise.generateNoiseOctaves(this.stoneNoise, chunkX * 16, chunkZ * 16, 0.0, 16, 16, 1, thirtySecond * 2.0, thirtySecond * 2.0, thirtySecond * 2.0);
		for (int z = 0; z < 16; ++z)
		{
			for (int x = 0; x < 16; ++x)
			{
				EnumBetaPlusBiome biome = Biome[z + x * 16];
				boolean sandN = this.sandNoise[z + x * 16] + this.rand.nextDouble() * 0.2 > 0.0;
				boolean gravelN = this.gravelNoise[z + x * 16] + this.rand.nextDouble() * 0.2 > 3.0;
				int stoneN = (int) (this.stoneNoise[z + x * 16] / 3.0 + 3.0 + this.rand.nextDouble() * 0.25);
				int checkVal = -1;
				Material topBlock = biome.topBlock;
				Material fillerBlock = biome.fillerBlock;

				// GO from Top to bottom of world
				for (int y = 127; y >= 0; --y)
				{
					if (y <= this.rand.nextInt(5))
					{
						chunkData.setBlock(x, y, z, Material.BEDROCK);
					}
					else
					{
						Material block = chunkData.getType(x, y, z);

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
								topBlock = Material.AIR;
								fillerBlock = Material.STONE;
							}
							else if (y >= 64 - 4 && y <= 64 + 1)
							{
								topBlock = biome.topBlock;
								fillerBlock = biome.fillerBlock;
								if (gravelN)
								{
									topBlock = Material.AIR;
									fillerBlock = Material.GRAVEL;
								}
								if (sandN)
								{
									topBlock = Material.SAND;
									fillerBlock = Material.SAND;
								}
							}
							if (y < 64 && topBlock == Material.AIR)
							{
								topBlock = Material.WATER;
							}

							// Sets top & filler Material
							checkVal = stoneN;
							// Test this still.
							if (y >= 64 -1)
							{
								chunkData.setBlock(x, y, z, topBlock);
							}
							else
							{
								chunkData.setBlock(x, y, z, fillerBlock);
							}
						}
						// Add Sandstone (NOT WORKING)
						else if (checkVal > 0)
						{
							--checkVal;
							chunkData.setBlock(x, y, z, fillerBlock);
							//Possibly state comparison fucked it
							if (checkVal == 0 && fillerBlock == Material.SAND)
							{
								checkVal = this.rand.nextInt(4);
								fillerBlock = Material.SANDSTONE;
							}
						} //END OF Y LOOP
					}
				}
			}
		}
	}
}
