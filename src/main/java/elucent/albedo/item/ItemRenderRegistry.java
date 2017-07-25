package elucent.albedo.item;

import java.util.Map;

import com.google.common.collect.Maps;

import net.minecraft.item.Item;

public class ItemRenderRegistry {
	public static final Map<Item, IItemSpecialRenderer> itemRenderMap = Maps.newHashMap();

	public static void register(Item i, IItemSpecialRenderer r) {
		itemRenderMap.put(i, r);
	}
	
}
