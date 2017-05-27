package elucent.albedo.item;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.item.Item;

public class ItemRenderRegistry {
	public static Map<Item, IItemSpecialRenderer> itemRenderMap = new HashMap<Item, IItemSpecialRenderer>();

	public static void register(Item i, IItemSpecialRenderer r){
		itemRenderMap.put(i, r);
	}
}
