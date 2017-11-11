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
 * Utility methods that should be part of openGL but are inconsistent across platforms
 */

float len(vec3 vec) {
	return sqrt((vec.x*vec.x) + (vec.y*vec.y) + (vec.z*vec.z));
}

vec3 norm(vec3 vec) {
	float length = len(vec);
	if (length==0) return vec3(0,0,0);
	return vec3(vec.x/length, vec.y/length, vec.z/length);
}


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
}

vec3 combinedLight() {
	//Don't clamp; Intense large-area whites happen a lot in real scenarios
	//if (lcolor.xyz.length()>0) {
		return vanillaLight() + (norm(lcolor.xyz)*intens);
	//} else {
	//	return vanillaLight();
	//}
}

void main() {
	vec4 color = clamp(vanilla() * vec4(combinedLight(),1), 0, 1);
	gl_FragColor = color;
}