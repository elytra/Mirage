package elucent.albedo.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class Fields {
	public static Field Minecraft_mcProfiler, Field_modifiers;
	
	public static void init(){
		try {
			Field_modifiers = Field.class.getDeclaredField("modifiers");
			Field_modifiers.setAccessible(true);
			
			Minecraft_mcProfiler = ReflectionHelper.findField(Minecraft.class, "mcProfiler", "field_71424_I");
			Minecraft_mcProfiler.setAccessible(true);
			Field_modifiers.setInt(Minecraft_mcProfiler, Minecraft_mcProfiler.getModifiers() & ~Modifier.FINAL);
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
}
