package com.mrburgerus.beta_plus.world.alpha_plus;

import net.minecraft.server.v1_13_R2.GeneratorSettingsOverworld;
import net.minecraft.server.v1_13_R2.NBTTagCompound;

public class AlphaPlusGenSettings extends GeneratorSettingsOverworld
{
	private boolean isSnowy = false;
	private final int seaLevel = 63; // Had to be changed :(

	public void setSnowy(boolean snowy)
	{
		isSnowy = snowy;
	}

	public boolean getSnowy()
	{
		return isSnowy;
	}

	public int getSeaLevel()
	{
		return seaLevel;
	}

}
