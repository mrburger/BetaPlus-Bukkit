package com.mrburgerus.beta_plus.world.beta_plus;

import com.google.common.collect.Maps;
import com.mrburgerus.beta_plus.util.BiomeReplaceUtil;
import com.mrburgerus.beta_plus.util.DeepenOceanUtil;
import com.mrburgerus.beta_plus.world.AbstractOldChunkGenerator;
import com.mrburgerus.beta_plus.world.biome.EnumBetaPlusBiome;
import com.mrburgerus.beta_plus.world.noise.NoiseGeneratorOctavesBeta;
import net.minecraft.server.v1_13_R2.*;
import org.bukkit.craftbukkit.libs.it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import org.bukkit.craftbukkit.libs.it.unimi.dsi.fastutil.longs.Long2ObjectMaps;
import org.bukkit.craftbukkit.libs.it.unimi.dsi.fastutil.longs.LongSet;

import javax.annotation.Nullable;
import java.util.*;


/* SEE CHUNKGENERATOROVERWORLD.CLASS FOR BASE */
/* ALSO SEE CHUNKGENERATORABSTRACT */

public class ChunkGeneratorBetaPlus extends AbstractOldChunkGenerator<BetaPlusGenSettings>
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
	private static final int CHUNK_SIZE = 16;
	private BetaPlusGenSettings settings;
	private final MobSpawnerPhantom phantomSpawner = new MobSpawnerPhantom();
	private World worldObj;

	// NEW NEW FIELDS
	protected final Map<StructureGenerator<? extends WorldGenFeatureConfiguration>, Long2ObjectMap<StructureStart>> structureStartCache = Maps.newHashMap();
	protected final Map<StructureGenerator<? extends WorldGenFeatureConfiguration>, Long2ObjectMap<LongSet>> structureReferenceCache = Maps.newHashMap();

	public ChunkGeneratorBetaPlus(World world, BiomeProviderBetaPlus biomeProvider, BetaPlusGenSettings settingsIn)
	{
		this.worldObj = world;
		rand = new Random(world.getSeed());
		octaves1 = new NoiseGeneratorOctavesBeta(rand, 16);
		octaves2 = new NoiseGeneratorOctavesBeta(rand, 16);
		octaves3 = new NoiseGeneratorOctavesBeta(rand, 8);
		beachBlockNoise = new NoiseGeneratorOctavesBeta(rand, 4);
		surfaceNoise = new NoiseGeneratorOctavesBeta(rand, 4);
		scaleNoise = new NoiseGeneratorOctavesBeta(rand, 10);
		octaves7 = new NoiseGeneratorOctavesBeta(rand, 16);
		biomeProviderS = biomeProvider;
		settings = settingsIn;
	}

	/* This Method is an Analog to generateChunk, albeit HEAVILY modified! */
	@Override
	public void createChunk(IChunkAccess chunkIn)
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
		DeepenOceanUtil.deepenOcean(chunkIn, rand, settings.getSeaLevel(), settings.getOceanSmoothSize(), 3.25);
		// Replace Biomes (Oceans)
		// This is because detection of Oceans is an average operation.
		this.replaceBiomes(chunkIn);

		// Replace Blocks (DIRT & SAND & STUFF)
		replaceBlocksForBiome(x, z, chunkIn, EnumBetaPlusBiome.convertBiomeTable(biomesForGeneration));
		// Replace Beaches, done afterwards.
		this.replaceBeaches(chunkIn);

		// Set Biomes
		chunkIn.a(BiomeReplaceUtil.convertBiomeArray(biomesForGeneration));

		chunkIn.a(ChunkStatus.BASE);
	}

	@Override
	public void addFeatures(RegionLimitedWorldAccess regionLimitedWorldAccess, WorldGenStage.Features features)
	{
		SeededRandom seededrandom = new SeededRandom(getSeed());
		boolean flag = true;
		int i = regionLimitedWorldAccess.a();
		int j = regionLimitedWorldAccess.b();
		BitSet bitset = regionLimitedWorldAccess.getChunkAt(i, j).a(features);

		for(int k = i - 8; k <= i + 8; ++k) {
			for(int l = j - 8; l <= j + 8; ++l) {
				List<WorldGenCarverWrapper<?>> list = regionLimitedWorldAccess.getChunkProvider().getChunkGenerator().getWorldChunkManager().getBiome(new BlockPosition(k * 16, 0, l * 16), (BiomeBase)null).a(features);
				ListIterator listiterator = list.listIterator();

				while(listiterator.hasNext()) {
					int i1 = listiterator.nextIndex();
					WorldGenCarverWrapper<?> worldgencarverwrapper = (WorldGenCarverWrapper)listiterator.next();
					seededrandom.c(regionLimitedWorldAccess.getMinecraftWorld().getSeed() + (long)i1, k, l);
					if (worldgencarverwrapper.a(regionLimitedWorldAccess, seededrandom, k, l, WorldGenFeatureConfiguration.e)) {
						worldgencarverwrapper.a(regionLimitedWorldAccess, seededrandom, k, l, i, j, bitset, WorldGenFeatureConfiguration.e);
					}
				}
			}
		}

	}


	@Override
	public void addDecorations(RegionLimitedWorldAccess regionLimitedWorldAccess)
	{
		BlockFalling.instaFall = true;
		int i = regionLimitedWorldAccess.a();
		int j = regionLimitedWorldAccess.b();
		int k = i * 16;
		int l = j * 16;
		BlockPosition blockposition = new BlockPosition(k, 0, l);
		BiomeBase biomebase = regionLimitedWorldAccess.getChunkAt(i + 1, j + 1).getBiomeIndex()[0];
		SeededRandom seededrandom = new SeededRandom();
		long i1 = seededrandom.a(regionLimitedWorldAccess.getSeed(), k, l);
		WorldGenStage.Decoration[] aworldgenstage_decoration = WorldGenStage.Decoration.values();
		int j1 = aworldgenstage_decoration.length;

		for(int k1 = 0; k1 < j1; ++k1) {
			WorldGenStage.Decoration worldgenstage_decoration = aworldgenstage_decoration[k1];
			biomebase.a(worldgenstage_decoration, this, regionLimitedWorldAccess, i1, seededrandom, blockposition);
		}

		BlockFalling.instaFall = false;
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
		BiomeBase biomebase = this.getWorldChunkManager().getBiome(blockposition, Biomes.PLAINS);
		return enumcreaturetype == EnumCreatureType.MONSTER && ((WorldGenFeatureSwampHut)WorldGenerator.l).d((GeneratorAccess) this.getWorldChunkManager(), blockposition) ? WorldGenerator.l.d() : (enumcreaturetype == EnumCreatureType.MONSTER && WorldGenerator.n.b((GeneratorAccess) this.getWorldChunkManager(), blockposition) ? WorldGenerator.n.d() : biomebase.getMobs(enumcreaturetype));
	}

	@Override
	public BetaPlusGenSettings getSettings()
	{
		return this.settings;
	}

	//Spawn Mobs but Phantoms
	@Override
	public int a(World world, boolean b, boolean b1)
	{
		int i = 0;
		i = i + this.phantomSpawner.a(world, b, b);
		return i;
	}


	@Override
	public boolean canSpawnStructure(BiomeBase biomeBase, StructureGenerator<? extends WorldGenFeatureConfiguration> structureGenerator)
	{
		return biomeBase.a(structureGenerator);
	}

	@Nullable
	@Override
	public WorldGenFeatureConfiguration getFeatureConfiguration(BiomeBase biomeBase, StructureGenerator<? extends WorldGenFeatureConfiguration> structureGenerator)
	{
		return biomeBase.b(structureGenerator);
	}

	@Override
	public Long2ObjectMap<StructureStart> getStructureStartCache(StructureGenerator<? extends WorldGenFeatureConfiguration> structureGenerator)
	{
		return this.structureStartCache.computeIfAbsent(structureGenerator, (p_203225_0_) -> Long2ObjectMaps.synchronize(new ExpiringMap<>(8192, 10000)));
	}

	@Override
	public Long2ObjectMap<LongSet> getStructureCache(StructureGenerator<? extends WorldGenFeatureConfiguration> structureGenerator)
	{
		return this.structureReferenceCache.computeIfAbsent(structureGenerator, (p_203226_0_) -> {
			return Long2ObjectMaps.synchronize(new ExpiringMap<>(8192, 10000));
		});
	}

	@Override
	public WorldChunkManager getWorldChunkManager()
	{
		return biomeProviderS;
	}

	@Override
	public long getSeed()
	{
		return worldObj.getSeed();
	}

	@Override
	public int getSpawnHeight()
	{
		return this.getWorld().getSeaLevel();
	}

	@Override
	public int getGenerationDepth()
	{
		return 0;
	}

	@Override
	public World getWorld()
	{
		return worldObj;
	}



	/* -- GENERATION METHODS -- */

	/* GENERATES THE BLOCKS */
	// PREVIOUSLY other methods, updated for 1.13!
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
								if (y < settings.getSeaLevel())
								{
									block = Blocks.WATER;
								}
								if (var48 > 0.0)
								{
									block = Blocks.STONE;
								}
								if (block != null)
								{
									chunk.setType(new BlockPosition(x, y, z), block.getBlockData(), false);
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
				int xPos = (iChunk.getPos().x << 4) + x;
				int zPos = (iChunk.getPos().z << 4) + z;
				int yVal = BiomeReplaceUtil.getSolidHeightY(new BlockPosition(xPos, 0, zPos), iChunk);
				if (yVal > settings.getHighAltitude())
				{
					biomesForGeneration[(x << 4 | z)] = EnumBetaPlusBiome.mountain.handle;
				}
				else if (yVal < settings.getSeaLevel() - 1)
				{

					if (yVal < settings.getSeaLevel() - settings.getSeaDepth())
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
				int xPos = (chunk.getPos().x << 4) + x;
				int zPos = (chunk.getPos().z << 4) + z;
				int yVal = BiomeReplaceUtil.getSolidHeightY(new BlockPosition(xPos, 0, zPos), chunk);
				// New Line
				BiomeBase biome = biomesForGeneration[(x << 4 | z)];
				//Inject Beaches (MODIFIED)
				if ((yVal <= (settings.getSeaLevel() + 1) && yVal >= settings.getSeaLevel() - 1) && (biome != EnumBetaPlusBiome.desert.handle) && chunk.getType(new BlockPosition(xPos, yVal, zPos)) == Blocks.SAND.getBlockData())
				{
						this.biomesForGeneration[(x << 4 | z)] = biomeProviderS.getBeachBiome(new BlockPosition(xPos, yVal, zPos));
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
				IBlockData topBlock = biome.topBlock.getBlockData();
				IBlockData fillerBlock = biome.fillerBlock.getBlockData();

				// GO from Top to bottom of world
				for (int y = 127; y >= 0; --y)
				{
					if (y <= this.rand.nextInt(5))
					{
						chunkprimer.setType(new BlockPosition(x, y, z), Blocks.BEDROCK.getBlockData(), false);
					}
					else
					{
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
								topBlock = Blocks.AIR.getBlockData();
								fillerBlock = Blocks.STONE.getBlockData();
							}
							else if (y >= settings.getSeaLevel() - 4 && y <= settings.getSeaLevel() + 1)
							{
								topBlock = biome.topBlock.getBlockData();
								fillerBlock = biome.fillerBlock.getBlockData();
								if (gravelN)
								{
									topBlock = Blocks.AIR.getBlockData();
									fillerBlock = Blocks.GRAVEL.getBlockData();
								}
								if (sandN)
								{
									topBlock = Blocks.SAND.getBlockData();
									fillerBlock = Blocks.SAND.getBlockData();
								}
							}
							if (y < settings.getSeaLevel() && topBlock == Blocks.AIR.getBlockData())
							{
								topBlock = Blocks.WATER.getBlockData();
							}

							// Sets top & filler Blocks
							checkVal = stoneN;
							// Test this still.
							if (y >= settings.getSeaLevel() -1)
							{
								chunkprimer.setType(new BlockPosition(x, y, z), topBlock, false);
							}
							else
							{
								chunkprimer.setType(new BlockPosition(x, y, z), fillerBlock, false);
							}
						}
						// Add Sandstone (NOT WORKING)
						else if (checkVal > 0)
						{
							--checkVal;
							chunkprimer.setType(new BlockPosition(x, y, z), fillerBlock, false);
							//Possibly state comparison fucked it
							if (checkVal == 0 && fillerBlock == Blocks.SAND.getBlockData())
							{
								checkVal = this.rand.nextInt(4);
								fillerBlock = Blocks.SANDSTONE.getBlockData();
							}
						} //END OF Y LOOP
					}
				}
			}
		}
	}
}
