package com.mrburgerus.betaplus.util;

import java.lang.reflect.Field;
import java.util.NoSuchElementException;
import java.util.Objects;

public final class ReflectionUtil
{
		public ReflectionUtil()
		{

		}

		public static Field getFieldByName(Object on, String name)
		{
			Objects.requireNonNull(name, "name");
			Class clazz = on.getClass();

			while(clazz != null) {
				try {
					Field field = clazz.getDeclaredField(name);
					field.setAccessible(true);
					return field;
				} catch (NoSuchFieldException var4) {
					clazz = clazz.getSuperclass();
				}
			}

			throw new NoSuchElementException("No field on " + on.getClass().getSimpleName() + " of name " + name);
		}

		public static Field getFieldOfType(Class<?> onClazz, Class<?> typeOfField)
		{
			Objects.requireNonNull(onClazz, "onClazz");

			for(Class clazz = onClazz; clazz != null; clazz = clazz.getSuperclass()) {
				Field[] var3 = clazz.getDeclaredFields();
				int var4 = var3.length;

				for(int var5 = 0; var5 < var4; ++var5) {
					Field field = var3[var5];
					if (field.getType().equals(typeOfField)) {
						field.setAccessible(true);
						return field;
					}
				}
			}

			throw new NoSuchElementException("No field on " + onClazz.getSimpleName() + " of type " + typeOfField);
		}

		public static Field getFieldOfType(Object on, Class<?> typeOfField)
		{
			Objects.requireNonNull(typeOfField, "typeOfField");
			return getFieldOfType(on.getClass(), typeOfField);
		}
}
