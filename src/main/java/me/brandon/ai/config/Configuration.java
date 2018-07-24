package me.brandon.ai.config;

import me.brandon.ai.gensim.world.Creature;
import me.brandon.ai.gensim.world.Tile;
import me.brandon.ai.gensim.world.World;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.util.Properties;

public class Configuration
{


	public static void loadConfigurations(Class<?>... configClasses)
	{
		try
		{
			File configFile = new File("./config.properties");

			if (configFile.exists() || configFile.createNewFile())
			{

				Properties config = new Properties();
				config.load(new FileInputStream(configFile));

				boolean dataUpdated = false;

				for (Class<?> clz : configClasses)
				{
					if (loadConfig(clz, config))
					{
						dataUpdated = true;
					}
				}


				if (dataUpdated)
				{
					config.store(new FileOutputStream(configFile), null);
				}


			}

		} catch (Exception e)
		{
			e.printStackTrace();
		}

	}

	private static boolean loadConfig(Class<?> clz, Properties config)
	{

		boolean dataUpdated = false;

		for (Field field : clz.getFields())
		{
			try
			{
				ConfigOption option = field.getAnnotation(ConfigOption.class);

				if (option == null)
					continue;

				String opName = option.option();
				if (opName.equals(""))
				{
					opName = field.getName();
				}

				ConfigOption.DataType dataType = option.type();
				if (dataType == ConfigOption.DataType.Unknown)
				{
					dataType = getType(field);
				}

				if (!field.isAccessible())
				{
					field.setAccessible(true);
				}

				assert dataType != null;

				Object property = config.get(opName);
				if (property == null)
				{

					StringBuilder sb = new StringBuilder();
					Object obj = field.get(null);

					System.out.println("Adding config option: " + opName + " = " + obj);

					if (obj instanceof Object[])
					{
						Object[] objs = (Object[]) obj;
						for (int i = 0; i < objs.length; i++)
						{
							sb.append(objs[i].toString());

							if (i < objs.length - 1)
							{
								sb.append(", ");
							}
						}
					}
					else
					{
						sb.append(obj.toString());
					}

					config.setProperty(opName, sb.toString().trim());

					dataUpdated = true;
				}
				else
				{
					Object configValue = null;

					if (option.isArray())
					{
						String props[] = property.toString().split(",");

						switch (dataType)
						{
							case INTEGER:
								int[] ints = new int[props.length];
								for (int i = 0; i < ints.length; i++)
								{
									ints[i] = Integer.valueOf(props[i]);
								}
								configValue = ints;
								break;
							case DOUBLE:
								double[] doubles = new double[props.length];
								for (int i = 0; i < doubles.length; i++)
								{
									doubles[i] = Double.valueOf(props[i]);
								}
								configValue = doubles;
								break;
							case FLOAT:
								float[] floats = new float[props.length];
								for (int i = 0; i < floats.length; i++)
								{
									floats[i] = Float.valueOf(props[i]);
								}
								configValue = floats;
								break;
							case STRING:
								configValue = props;
								break;
						}
					}
					else
					{
						switch (dataType)
						{
							case INTEGER:
								configValue = Integer.valueOf(property.toString());
								break;
							case DOUBLE:
								configValue = Double.valueOf(property.toString());
								break;
							case FLOAT:
								configValue = Float.valueOf(property.toString());
								break;
							case STRING:
								configValue = property.toString();
								break;
						}
					}

					field.set(null, configValue);
				}
			} catch (Exception e)
			{
				e.printStackTrace();
			}

		}

		return dataUpdated;

	}

	private static ConfigOption.DataType getType(Field field)
	{
		if (field.getType() == int.class)
		{
			return ConfigOption.DataType.INTEGER;
		}
		else if (field.getType() == double.class)
		{
			return ConfigOption.DataType.DOUBLE;
		}
		else if (field.getType() == String.class)
		{
			return ConfigOption.DataType.STRING;
		}
		else if(field.getType() == float.class)
		{
			return ConfigOption.DataType.FLOAT;
		}
		return null;
	}


}
