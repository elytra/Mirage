<img src="https://raw.githubusercontent.com/elytra/Albedo/1.12/doc/logo.png" align="right" width="180px"/>

# Albedo

*Fast colored lighting for entities and blocks.*

Albedo is a library/coremod originally created by Elucent for use in Embers.
With permission, unascribed forked it, cleaned it up a bit, and ported it to
1.12. It's based on shaders and is *completely client-sided*, so it's relatively
lag-free, but that also means Albedo lights cannot prevent mob spawns.

## Usage

First off, add the Elytra Maven to your build.gradle:

```gradle
repositories {
	maven {
		url 'https://repo.elytradev.com/'
	}
}
```

Then, add this to your `dependencies` block:

```gradle
compile 'elucent:albedo:2.0-SNAPSHOT
```

Use 1.0-SNAPSHOT instead if you're on 1.11.2.

**Note**: If you get a 'peer not authenticated' error, you need to update
Gradle. Older versions of Gradle include a broken Apache HttpClient that doesn't
support our HTTPS certificate.

Finally, you can do something similar to this for any Entity or TileEntity:

```java
import net.minecraftforge.fml.common.Optional;

@Optional.Interface(iface="elucent.albedo.lighting.ILightProvider", modid="albedo")
public class MyEntityOrTileEntity extends {Tile,}Entity implements ILightProvider {
	
	@Optional.Method(modid="albedo")
	@Override
	public Light provideLight() {
		return Light.builder()
				.pos(/* ... */)
				.color(1, 1, 1)
				.radius(2)
				.build();
	}
	
}

```

The @Optionals let people use your mod without needing to install Albedo.
Your mod will take advantage of Albedo when possible.

If you need more control, listen for the `GatherLightsEvent` and add your own
Light object to the list.