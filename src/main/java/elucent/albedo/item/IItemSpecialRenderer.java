package elucent.albedo.item;

import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.item.ItemStack;

public interface IItemSpecialRenderer {
	public void render(ItemStack stack, TransformType type);
}
