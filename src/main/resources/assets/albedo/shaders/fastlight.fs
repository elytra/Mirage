#version 120

varying vec3 normal;
varying vec4 color;
varying float shift;
varying vec3 position;
varying float intens;
varying vec4 lcolor;
varying vec4 uv;
varying vec3 lmapDarkness;

uniform int chunkX;
uniform int chunkZ;
uniform sampler2D sampler;
uniform sampler2D lightmap;
uniform vec3 playerPos;

void main()
{
	vec4 baseColor = gl_Color * texture2D(sampler,gl_TexCoord[0].st);
	vec4 color = vec4((baseColor.xyz * (lcolor.xyz * 1.0f)),baseColor.w);
	color = min(vec4(1.0f),color);
	color = (1.0f-intens)*baseColor*vec4(lmapDarkness,1) + intens*color;
	gl_FragColor = vec4(color.xyz * (intens + (1.0f-intens)*lmapDarkness),color.w);
}