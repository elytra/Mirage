#version 120
varying vec3 normal;
varying vec4 color;
varying float shift;
varying vec3 position;
varying vec4 uv;
varying vec2 lightuv;
varying vec3 lmapDarkness;
uniform sampler2D lightmap;
uniform int chunkX;
uniform int chunkY;
uniform int chunkZ;
uniform float ticks;
uniform int flickerMode;

float rand2(vec2 co){
    return fract(sin(dot(co.xy ,vec2(12.9898,78.233))) * 43758.5453);
}

vec3 rand3(vec3 co){
    return vec3(rand2(co.xz)-0.5f,rand2(co.yx)-0.5f,rand2(co.zy)-0.5f);
}

void main()
{
    vec4 pos = gl_ModelViewProjectionMatrix * gl_Vertex;
	
	normal = gl_Normal;
	gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
	
	position = gl_Vertex.xyz+vec3(chunkX,chunkY,chunkZ);
	
	gl_TexCoord[0] = gl_MultiTexCoord0;
	gl_TexCoord[1] = gl_MultiTexCoord1;
	
	gl_FrontColor = gl_Color;
	
	lightuv = gl_MultiTexCoord1.st;
	
	vec2 blocklighttex = vec2(gl_TexCoord[1].s,1.0f);
	vec2 sunlighttex = vec2(1.0f,gl_TexCoord[1].t);
	float x = ticks/2f;
	vec3 blocklightdark = texture2D(lightmap,blocklighttex*vec2(1.0f/256.0f,1.0f/256.0f)).xyz*2.0f * (0.95f+0.1f*(((sin(x)+sin(2.0f*x)-2.0f*sin(1.5f*x)+0.5f*sin(3.0f*x)+cos(6.0f*x))+4.449f)/9.226f));
	if (flickerMode == 1){
		blocklightdark *= (0.95f+0.1f*(((sin(x)+sin(2.0f*x)-2.0f*sin(1.5f*x)+0.5f*sin(3.0f*x)+cos(6.0f*x))+4.449f)/9.226f));
	}
	vec3 sunlightdark = texture2D(lightmap,sunlighttex*vec2(1.0f/256f,1.0f/256f)).xyz*2.0f;
	lmapDarkness = min(vec3(1.0f),max(blocklightdark,sunlightdark));
}