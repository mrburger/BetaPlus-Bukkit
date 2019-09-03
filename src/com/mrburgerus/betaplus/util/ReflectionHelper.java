package com.mrburgerus.betaplus.util;

import java.lang.reflect.Field;

public final class ReflectionHelper
{
	public static <T> void setValueInFieldOfType(Object on, Class<? super T> fieldType, T newValue)
	{
		Field field = getOnlyFieldDefOfType(on.getClass(), fieldType);
		try
		{
			field.setAccessible(true);
			field.set(on, newValue);
		}
		catch (IllegalAccessException e)
		{
			// Cannot happen, we just made the field accessible
			throw new AssertionError(e);
		}
	}

	private static Field getOnlyFieldDefOfType(Class<?> searchClass, Class<?> fieldType)
	{
		// As getDeclaredFields() only returns fields declared in the class,
		// we also need to search parent classes
		Class<?> onClass = searchClass;
		Field result = null;
		while (onClass != null)
		{
			for (Field field : onClass.getDeclaredFields())
			{
				if (!field.getType().equals(fieldType))
				{
					continue;
				}

				if (result != null)
				{
					throw new NoSuchFieldError("Two fields of type " + fieldType + " in " + onClass + ": " + field.getName() + " and " + result.getName());
				}

				result = field;
			}

			if (result != null)
			{
				// Found single field in class, stop searching
				break;
			}

			// Not yet found, continue search in super class
			onClass = onClass.getSuperclass();
		}

		if (result == null)
		{
			throw new NoSuchFieldError("Found no field of type " + fieldType + " in " + searchClass);
		}

		return result;
	}
}
