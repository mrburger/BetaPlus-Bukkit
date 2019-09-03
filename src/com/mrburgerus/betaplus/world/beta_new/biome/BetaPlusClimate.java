package com.mrburgerus.betaplus.world.beta_new.biome;

import com.mrburgerus.betaplus.world.noise.NoiseGeneratorOctavesBiome;
import net.minecraft.server.v1_14_R1.BlockPosition;
import net.minecraft.server.v1_14_R1.MathHelper;
import net.minecraft.server.v1_14_R1.World;

import java.util.Random;

/* Works */
public class BetaPlusClimate
{
	// Fields
	private NoiseGeneratorOctavesBiome temperatureOctave;
	private NoiseGeneratorOctavesBiome humidityOctave;
	private NoiseGeneratorOctavesBiome noiseOctave;

	private final double scaleVal;
	private final double mult;

	private double[] temps2;
	private double[] humid2;
	private double[] noise2;

	public BetaPlusClimate(long seed, double scaleFactor, double multiplier)
	{
		temperatureOctave = new NoiseGeneratorOctavesBiome(new Random(seed * 9871), 4);
		humidityOctave = new NoiseGeneratorOctavesBiome(new Random(seed * 39811), 4);
		noiseOctave = new NoiseGeneratorOctavesBiome(new Random(seed * 543321), 2);
		scaleVal = scaleFactor;
		mult = multiplier;
	}

	// Working Feb 21, 2019
	/* Gets Climate Values */
	public double[] getClimateValuesatPos(BlockPosition pos)
	{
		//Copied Over
		int startX = pos.getX();
		int startZ = pos.getZ();
		int xSize = 1;

		temps2 = temperatureOctave.generateOctaves(temps2, startX, startZ, xSize, xSize, scaleVal, scaleVal, 0.25);
		humid2 = humidityOctave.generateOctaves(humid2, startX, startZ, xSize, xSize, scaleVal * mult, scaleVal * mult, 0.3333333333333333);
		noise2 = noiseOctave.generateOctaves(noise2, startX, startZ, xSize, xSize, 0.25, 0.25, 0.5882352941176471);

		double var9 = noise2[0] * 1.1 + 0.5;
		double oneHundredth = 0.01;
		double point99 = 1.0 - oneHundredth;
		double temperatureVal = (temps2[0] * 0.15 + 0.7) * point99 + var9 * oneHundredth;
		oneHundredth = 0.002;
		point99 = 1.0 - oneHundredth;
		double humidityVal = (humid2[0] * 0.15 + 0.5) * point99 + var9 * oneHundredth;
		temperatureVal = 1.0 - (1.0 - temperatureVal) * (1.0 - temperatureVal);
		// a was CLAMP
		temperatureVal = MathHelper.a(temperatureVal, 0.0, 1.0);
		humidityVal = MathHelper.a(humidityVal, 0.0, 1.0);

		//BetaPlus.LOGGER.info("T: " + temperatureVal + " H: " + humidityVal);

		return new double[]{MathHelper.a(temperatureVal, 0.0, 1.0), MathHelper.a(humidityVal, 0.0, 1.0)};
	}
}
