#version 120

varying vec3 normal;
varying float shift;
varying vec3 position;
varying float intens;
varying vec4 lcolor;
varying vec4 uv;

uniform int chunkX;
uniform int chunkZ;
uniform sampler2D sampler;
uniform sampler2D lightmap;
uniform vec3 playerPos;

/*
 * Gets the fog color and intensity at the current location
 */
vec4 vanillaFog() {
	vec3 dv = position - playerPos;
	float fogdist = max(sqrt(dv.x*dv.x+dv.y*dv.y+dv.z*dv.z) - gl_Fog.start,0.0f) / (gl_Fog.end-gl_Fog.start);
	float fog = gl_Fog.density * fogdist;
	fog = clamp( fog, 0.0f, 1.0f );
	return vec4(gl_Fog.color.xyz, fog * gl_Fog.color.w);
}

vec3 vanillaLight() {
	return clamp(texture2D(lightmap, gl_TexCoord[1].st).xyz, 0.0f, 1.0f);
}

vec4 vanilla() {
	vec4 baseColor = gl_Color * texture2D(sampler, gl_TexCoord[0].st);
	vec4 fog = vanillaFog();
	baseColor = vec4(mix(baseColor.xyz, fog.xyz, fog.w), baseColor.w);
	
	return baseColor;
	//return baseColor * vec4(vanillaLight(), 1.0f);
}

vec3 combinedLight() {
	//Don't clamp; Intense large-area whites happen a lot in real scenarios
	return vanillaLight() + (lcolor.xyz); //stacks vanilla on top of colored lights, close to physical behavior
	//return mix(vanillaLight(), lcolor.xyz, intens); //old mixing, prioritizes colored lights over vanilla
}

void main() {
	//vec4 light = vec4(combinedLight(),1);
	//vec4 color = vanilla();
	//if (light.x>0) { color.x *= light.x; } else { color.x = 0; }
	//if (light.y>0) { color.y *= light.y; } else { color.y = 0; }
	//if (light.z>0) { color.z *= light.z; } else { color.z = 0; }
	//color = color * light;
	//color = clamp(color, 0, 1);
	
	
	vec4 color = clamp(vanilla() * vec4(combinedLight(),1), 0, 1);
	//vec4 color = vec4(max(mix(baseColor.xyz*lightdark,baseColor.xyz*lcolor.xyz,intens),lightdark*baseColor.xyz),baseColor.w);
	//vec4 color = baseColor;
	gl_FragColor = color;
}