package com.mrburgerus.beta_plus.world.alpha_plus;

import com.mrburgerus.beta_plus.util.BiomeReplaceUtil;
import com.mrburgerus.beta_plus.util.DeepenOceanUtil;
import com.mrburgerus.beta_plus.world.noise.NoiseGeneratorOctavesAlpha;
import net.minecraft.server.v1_13_R2.*;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class ChunkGeneratorAlphaPlus extends ChunkGeneratorAbstract<AlphaPlusGenSettings>
{
	// Fields
	private Random rand;
	private NoiseGeneratorOctavesAlpha octaves1;
	private NoiseGeneratorOctavesAlpha octaves2;
	private NoiseGeneratorOctavesAlpha octaves3;
	private NoiseGeneratorOctavesAlpha beachBlockNoise;
	private NoiseGeneratorOctavesAlpha surfaceNoise;
	public NoiseGeneratorOctavesAlpha octaves4;
	public NoiseGeneratorOctavesAlpha octaves5;
	private double[] heightNoise;
	double[] octave3Arr;
	double[] octave1Arr;
	double[] octave2Arr;
	double[] octave4Arr;
	double[] octave5Arr;
	private double[] sandNoise = new double[256];
	private double[] gravelNoise = new double[256];
	private double[] stoneNoise = new double[256];
	// New Fields
	private final AlphaPlusGenSettings settings;
	private static final int CHUNK_SIZE = 16;
	private BiomeProviderAlphaPlus biomeProviderS;
	private BiomeBase[] biomesForGeneration;

	public ChunkGeneratorAlphaPlus(World world, BiomeProviderAlphaPlus biomeProvider, AlphaPlusGenSettings settingsIn)
	{
		super(world, biomeProvider);
		this.rand = new Random(world.getSeed());
		/* Declaration Order Matters */
		this.octaves1 = new NoiseGeneratorOctavesAlpha(this.rand, 16);
		this.octaves2 = new NoiseGeneratorOctavesAlpha(this.rand, 16);
		this.octaves3 = new NoiseGeneratorOctavesAlpha(this.rand, 8);
		this.beachBlockNoise = new NoiseGeneratorOctavesAlpha(this.rand, 4);
		this.surfaceNoise = new NoiseGeneratorOctavesAlpha(this.rand, 4);
		this.octaves4 = new NoiseGeneratorOctavesAlpha(this.rand, 10);
		this.octaves5 = new NoiseGeneratorOctavesAlpha(this.rand, 16);
		settings = settingsIn;
		biomeProviderS = biomeProvider;
	}

	@Override
	public void createChunk(IChunkAccess iChunk)
	{
		int xPos = iChunk.getPos().x;
		int zPos =  iChunk.getPos().z;
		biomesForGeneration = this.biomeProviderS.getBiomeBlock(xPos * 16, zPos * 16, 16, 16);
		setBlocksInChunk(iChunk);
		DeepenOceanUtil.deepenOcean(iChunk, new Random(getWorld().getSeed()), settings.getSeaLevel(), 7, 3.1);
		this.replaceBiomes(iChunk);
		this.replaceBeaches(iChunk);

		iChunk.a(BiomeReplaceUtil.convertBiomeArray(biomesForGeneration));
		// Replace Blocks Such as Grass.
		this.replaceBlocks(iChunk);
		iChunk.a(ChunkStatus.BASE);
	}

	@Override
	public void addMobs(RegionLimitedWorldAccess regionlimitedworldaccess)
	{
		int i = regionlimitedworldaccess.a();
		int j = regionlimitedworldaccess.b();
		BiomeBase biomebase = regionlimitedworldaccess.getChunkAt(i, j).getBiomeIndex()[0];
		SeededRandom seededrandom = new SeededRandom();
		seededrandom.a(regionlimitedworldaccess.getSeed(), i << 4, j << 4);
		SpawnerCreature.a(regionlimitedworldaccess, biomebase, i, j, seededrandom);
	}

	@Override
	public List<BiomeBase.BiomeMeta> getMobsFor(EnumCreatureType enumcreaturetype, BlockPosition blockposition)
	{
		BiomeBase biomebase = this.a.getBiome(blockposition);
		return enumcreaturetype == EnumCreatureType.MONSTER && ((WorldGenFeatureSwampHut)WorldGenerator.l).d(this.a, blockposition) ? WorldGenerator.l.d() : (enumcreaturetype == EnumCreatureType.MONSTER && WorldGenerator.n.b(this.a, blockposition) ? WorldGenerator.n.d() : biomebase.getMobs(enumcreaturetype));
	}

	@Override
	public AlphaPlusGenSettings getSettings()
	{
		return this.settings;
	}

	@Override
	public int a(World world, boolean b, boolean b1)
	{
		return 0;
	}

	@Override
	public int getSpawnHeight()
	{
		return getWorld().getSeaLevel();
	}

	@Override
	public double[] a(int i, int i1)
	{
		return new double[0];
	}

	/* Sets blocks, just like beta */
	private void setBlocksInChunk(IChunkAccess chunk)
	{
		int chunkX = chunk.getPos().x;
		int chunkZ = chunk.getPos().z;
		byte var4 = 4;
		byte seaLevel = 63; //Was 64, Ocean Monuments messed with this.
		int var6 = var4 + 1;
		byte var7 = 17;
		int var8 = var4 + 1;
		this.heightNoise = this.generateOctaves(this.heightNoise, chunkX * var4, 0, chunkZ * var4, var6, var7, var8);

		for (int var9 = 0; var9 < var4; ++var9) {
			for (int var10 = 0; var10 < var4; ++var10) {
				for (int var11 = 0; var11 < 16; ++var11) {
					double var12 = 0.125D;
					double var14 = this.heightNoise[((((var9) * var8) + var10) * var7) + var11];
					double var16 = this.heightNoise[((var9) * var8 + var10 + 1) * var7 + var11];
					double var18 = this.heightNoise[((var9 + 1) * var8 + var10) * var7 + var11];
					double var20 = this.heightNoise[((var9 + 1) * var8 + var10 + 1) * var7 + var11];
					double var22 = (this.heightNoise[((var9) * var8 + var10) * var7 + var11 + 1] - var14) * var12;
					double var24 = (this.heightNoise[((var9) * var8 + var10 + 1) * var7 + var11 + 1] - var16) * var12;
					double var26 = (this.heightNoise[((var9 + 1) * var8 + var10) * var7 + var11 + 1] - var18) * var12;
					double var28 = (this.heightNoise[((var9 + 1) * var8 + var10 + 1) * var7 + var11 + 1] - var20) * var12;

					for (int var30 = 0; var30 < 8; ++var30) {
						double var31 = 0.25D;
						double var33 = var14;
						double var35 = var16;
						double var37 = (var18 - var14) * var31;
						double var39 = (var20 - var16) * var31;

						for (int var41 = 0; var41 < 4; ++var41) {
							int x = var41 + var9 * 4;
							int y = var11 * 8 + var30;
							int z = var10 * 4;

							double var44 = 0.25D;
							double stoneN = var33;
							double var48 = (var35 - var33) * var44;

							for (int var50 = 0; var50 < 4; ++var50) {
								Block block = null;
								if (y < seaLevel) {
									block = Blocks.WATER;
								}

								if (stoneN > 0.0D) {
									block = Blocks.STONE;
								}

								if (block != null) {
									chunk.setType(new BlockPosition(x, y, z), block.getBlockData(), false);
								}
								++z;
								stoneN += var48;
							}

							var33 += var37;
							var35 += var39;
						}

						var14 += var22;
						var16 += var24;
						var18 += var26;
						var20 += var28;
					}
				}
			}
		}

	}

	private double[] generateOctaves(double[] var1, int var2, int var3, int var4, int size1, int size2, int size3) {
		if (var1 == null) {
			var1 = new double[size1 * size2 * size3];
		}

		double var8 = 684.412D;
		double var10 = 684.412D;
		this.octave4Arr =
				this.octaves4.generateNoiseOctaves(this.octave4Arr, (double) var2, (double) var3, (double) var4, size1, 1, size3, 1.0D, 0.0D, 1.0D);
		this.octave5Arr = this.octaves5
				.generateNoiseOctaves(this.octave5Arr, (double) var2, (double) var3, (double) var4, size1, 1, size3, 100.0D, 0.0D, 100.0D);
		this.octave3Arr = this.octaves3
				.generateNoiseOctaves(this.octave3Arr, (double) var2, (double) var3, (double) var4, size1, size2, size3, var8 / 80.0D, var10 / 160.0D,
						var8 / 80.0D);
		this.octave1Arr = this.octaves1
				.generateNoiseOctaves(this.octave1Arr, (double) var2, (double) var3, (double) var4, size1, size2, size3, var8, var10, var8);
		this.octave2Arr = this.octaves2
				.generateNoiseOctaves(this.octave2Arr, (double) var2, (double) var3, (double) var4, size1, size2, size3, var8, var10, var8);
		int var12 = 0;
		int var13 = 0;

		for (int var14 = 0; var14 < size1; ++var14) {
			for (int var15 = 0; var15 < size3; ++var15) {
				double var16 = (this.octave4Arr[var13] + 256.0D) / 512.0D;
				if (var16 > 1.0D) {
					var16 = 1.0D;
				}

				double var18 = 0.0D;
				double var20 = this.octave5Arr[var13] / 8000.0D;
				if (var20 < 0.0D) {
					var20 = -var20;
				}

				var20 = var20 * 3.0D - 3.0D;
				if (var20 < 0.0D) {
					var20 = var20 / 2.0D;
					if (var20 < -1.0D) {
						var20 = -1.0D;
					}

					var20 = var20 / 1.4D;
					var20 = var20 / 2.0D;
					var16 = 0.0D;
				} else {
					if (var20 > 1.0D) {
						var20 = 1.0D;
					}

					var20 = var20 / 6.0D;
				}

				var16 = var16 + 0.5D;
				var20 = var20 * (double) size2 / 16.0D;
				double var22 = (double) size2 / 2.0D + var20 * 4.0D;
				++var13;

				for (int var24 = 0; var24 < size2; ++var24) {
					double var25 = 0.0D;
					double var27 = ((double) var24 - var22) * 12.0D / var16;
					if (var27 < 0.0D) {
						var27 *= 4.0D;
					}

					double var29 = this.octave1Arr[var12] / 512.0D;
					double var31 = this.octave2Arr[var12] / 512.0D;
					double var33 = (this.octave3Arr[var12] / 10.0D + 1.0D) / 2.0D;
					if (var33 < 0.0D) {
						var25 = var29;
					} else if (var33 > 1.0D) {
						var25 = var31;
					} else {
						var25 = var29 + (var31 - var29) * var33;
					}

					var25 = var25 - var27;
					if (var24 > size2 - 4) {
						double var35 = (double) ((float) (var24 - (size2 - 4)) / 3.0F);
						var25 = var25 * (1.0D - var35) + -10.0D * var35;
					}

					if ((double) var24 < var18) {
						double var45 = (var18 - (double) var24) / 4.0D;
						if (var45 < 0.0D) {
							var45 = 0.0D;
						}

						if (var45 > 1.0D) {
							var45 = 1.0D;
						}

						var25 = var25 * (1.0D - var45) + -10.0D * var45;
					}

					var1[var12] = var25;
					++var12;
				}
			}
		}

		return var1;
	}

	private void replaceBlocks(IChunkAccess chunk)
	{
		int chunkX = chunk.getPos().x;
		int chunkZ = chunk.getPos().z;

		byte seaLevel = 63;
		double var5 = 0.03125D;


		this.sandNoise = this.beachBlockNoise.generateNoiseOctaves(this.sandNoise, chunkX * 16, chunkZ * 16, 0.0D, 16, 16, 1, var5, var5, 1.0D);

		this.gravelNoise = this.beachBlockNoise.generateNoiseOctaves(this.gravelNoise, chunkZ * 16, 109.0134D, chunkX * 16, 16, 1, 16, var5, 1.0D, var5);

		this.stoneNoise = this.surfaceNoise.generateNoiseOctaves(this.stoneNoise, chunkX * 16, chunkZ * 16, 0.0D, 16, 16, 1, var5 * 2.0D, var5 * 2.0D, var5 * 2.0D);


		for (int x = 0; x < 16; x++)
		{
			for (int z = 0; z < 16; z++)
			{
				boolean sand = this.sandNoise[(x + z * 16)] + this.rand.nextDouble() * 0.2D > 0.0D;
				boolean gravel = this.gravelNoise[(x + z * 16)] + this.rand.nextDouble() * 0.2D > 3.0D;
				int stone = (int) (this.stoneNoise[(x + z * 16)] / 3.0D + 3.0D + this.rand.nextDouble() * 0.25D);
				int var12 = -1;
				Block topBlock = Blocks.GRASS_BLOCK;
				Block fillerBlock = Blocks.DIRT;


				boolean top = true;
				boolean water = false;

				for (int y = 127; y >= 0; y--)
				{
					if (y <= this.rand.nextInt(6) - 1)
					{
						chunk.setType(new BlockPosition(x, y, z), Blocks.BEDROCK.getBlockData(), false);
					}
					else
					{
						Block block = chunk.getType(new BlockPosition(x, y, z)).getBlock();

						if (block == Blocks.AIR)
						{
							var12 = -1;
						}
						else if (block == Blocks.STONE)
						{
							if (var12 == -1)
							{
								if (stone <= 0)
								{
									topBlock = Blocks.AIR;
									fillerBlock = Blocks.STONE;
								}
								else if ((y >= seaLevel - 4) && (y <= seaLevel + 1))
								{
									topBlock = Blocks.GRASS_BLOCK;
									fillerBlock = Blocks.DIRT;
									if (gravel)
									{
										topBlock = Blocks.AIR;
									}

									if (gravel)
									{
										fillerBlock = Blocks.GRAVEL;
									}

									if (sand)
									{
										topBlock = Blocks.SAND;
									}

									if (sand)
									{
										fillerBlock = Blocks.SAND;
									}
								}

								if ((y < seaLevel) && (topBlock == Blocks.AIR))
								{
									topBlock = Blocks.WATER;
								}

								var12 = stone;
								if (y >= seaLevel - 1)
								{
									chunk.setType(new BlockPosition(x, y, z), topBlock.getBlockData(), false);
								}
								else
								{
									chunk.setType(new BlockPosition(x, y, z), fillerBlock.getBlockData(), false);
								}
							}
							else if (var12 > 0)
							{
								var12--;
								chunk.setType(new BlockPosition(x, y, z), fillerBlock.getBlockData(), false);
							}
						}
						else if (block == Blocks.WATER)
						{
							water = true;
						}
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
				int xPos = (iChunk.getPos().x << 4) + x;
				int zPos = (iChunk.getPos().z << 4) + z;
				int yVal = BiomeReplaceUtil.getSolidHeightY(new BlockPosition(xPos, 0, zPos), iChunk);
				if (yVal < settings.getSeaLevel() - 1)
				{
					if(settings.getSnowy())
					{
						biomesForGeneration[(x << 4 | z)] = BiomeProviderAlphaPlus.ALPHA_FROZEN_OCEAN;
					}
					else
					{
						if (yVal < 64 - 16)
						{
							biomesForGeneration[(x << 4 | z)] = BiomeProviderAlphaPlus.ALPHA_OCEAN;
						}
						else
						{
							biomesForGeneration[(x << 4 | z)] = BiomeProviderAlphaPlus.ALPHA_OCEAN;
						}
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
				int xPos = (chunk.getPos().x << 4) + x;
				int zPos = (chunk.getPos().z << 4) + z;
				int yVal = BiomeReplaceUtil.getSolidHeightY(new BlockPosition(xPos, 0, zPos), chunk);
				if ((yVal <= (settings.getSeaLevel() + 1) && yVal >= settings.getSeaLevel() - 1) && chunk.getType(new BlockPosition(xPos, yVal, zPos)) == Blocks.SAND.getBlockData())
				{
					if (settings.getSnowy())
					{
						biomesForGeneration[(x << 4 | z)] = Biomes.SNOWY_BEACH;
					}
					else
					{
						biomesForGeneration[(x << 4 | z)] = Biomes.BEACH;
					}
				}
			}
		}
	}


	@Nullable
	@Override
	public BlockPosition findNearestMapFeature(World world, String s, BlockPosition blockposition, int i, boolean flag)
	{
		StructureGenerator<?> structure = WorldGenFeatureComposite.aF.get(s.toLowerCase(Locale.ROOT));
		if (structure != null)
		{
			return structure.getNearestGeneratedFeature(world, this, blockposition, i, flag);
		}
		return null;
	}
}
