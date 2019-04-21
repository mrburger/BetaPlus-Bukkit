package com.mrburgerus.beta_plus.util;

public class MathHelper
{
	public static double clamp(double num, double min, double max) {
		if (num < min) {
			return min;
		} else {
			return num > max ? max : num;
		}
	}
}
