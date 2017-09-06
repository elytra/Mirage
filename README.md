<img src="https://raw.githubusercontent.com/elytra/Mirage/1.12/doc/logo.png" align="right" width="180px"/>

# Mirage

[>> Jenkins (Downloads) <<](https://ci.elytradev.com/job/elytra/job/Mirage/)

*Neat rendering tricks with an API.*

Mirage is a library and coremod containing some neat rendering tricks for use in
any mod. It started life as an update of Elucent's Albedo to 1.12 (best known for
the lighting effects in Embers), and is slowly becoming a more complete render
tweaks library.

The library was renamed from Albedo to Mirage to avoid naming conflicts after
Elucent picked Albedo back up. Mirage's API is intentionally incompatible with
Albedo as a result.

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
compile 'com.elytradev:mirage:2.0.1-SNAPSHOT'
```

Switch to the 1.11.2 branch for 1.11.2 instructions, which are for Albedo
post-fork instead of Mirage. You may also want to try just using
[Albedo pre-fork](https://github.com/elucent/Albedo).

**Note**: If you get a 'peer not authenticated' error, you need to update
Gradle. Older versions of Gradle include a broken Apache HttpClient that doesn't
support our HTTPS certificate.

Finally, you can do something similar to this for any Entity or TileEntity:

```java
import net.minecraftforge.fml.common.Optional;

@Optional.Interface(iface="com.elytradev.mirage.lighting.IColoredLight", modid="mirage")
public class MyEntityOrTileEntity extends {Tile,}Entity implements IColoredLight {
	
	@Optional.Method(modid="mirage")
	@Override
	public Light getColoredLight() {
		return Light.builder()
				.pos(/* ... */)
				.color(1, 1, 1)
				.radius(2)
				.build();
	}
	
}

```

The @Optionals let people use your mod without needing to install Mirage.
Your mod will take advantage of Mirage when possible.

If you need more control, listen for the `GatherLightsEvent` and add your own
Light object to the list.
