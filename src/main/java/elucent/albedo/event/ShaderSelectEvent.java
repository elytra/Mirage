package elucent.albedo.event;

import net.minecraftforge.fml.common.eventhandler.Event;

public class ShaderSelectEvent extends Event {
	private int shader;

	public ShaderSelectEvent(int shader) {
		super();
		this.shader = shader;
		this.setCanceled(false);
	}

	public int getShader() {
		return shader;
	}

	public void setShader(int shader) {
		this.shader = shader;
	}

	@Override
	public boolean isCancelable() {
		return true;
	}
}
