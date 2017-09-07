#version 120
varying vec3 normal;
varying float shift;
varying vec3 position;
varying vec4 uv;
varying vec4 lcolor;
varying float intens;

struct Light {
    vec4 color;
    vec3 position;
    vec3 coneDirection; //Non-normalized! Used for radius as well
    float coneFalloff;
	//float radius;
	float intensity;
};

uniform int chunkX;
uniform int chunkY;
uniform int chunkZ;
uniform sampler2D sampler;
uniform sampler2D lightmap;
uniform mat4 modelview;
uniform Light lights[100];
uniform int lightCount;
uniform int maxLights;
uniform float ticks;
uniform int flickerMode;

const float redScale   = 1.0f - 0.2126f;
const float greenScale = 1.0f - 0.7152f;
const float blueScale  = 1.0f - 0.0722f;

void main() {
    vec4 pos = gl_ModelViewProjectionMatrix * gl_Vertex;
	
	normal = gl_Normal;
	position = gl_Vertex.xyz+vec3(chunkX,chunkY,chunkZ);
	
	float offset = 0;
	
	gl_TexCoord[0] = gl_MultiTexCoord0;
	gl_TexCoord[1] = gl_TextureMatrix[1] * gl_MultiTexCoord1;
	gl_Position = gl_ModelViewProjectionMatrix * (gl_Vertex + vec4(0,offset,0,0));
	gl_FrontColor = gl_Color;
	
	//lcolor = vec4(0,0,0,1.0f);
	float sumR = 0;
	float sumG = 0;
	float sumB = 0;
	//float count = 0;
	float totalIntens = 0;
	for (int i = 0; i < lightCount; i ++) {
		float dist = distance(lights[i].position,position);
		float radius = length(lights[i].coneDirection);
		if (dist <= radius) {
		
			//Cone
			vec3 coneVec  = normalize(lights[i].coneDirection);
			vec3 incident = normalize(position - lights[i].position);
			float angularDifference = dot(coneVec, incident);
			if (angularDifference<lights[i].coneFalloff) continue;
		
			//Intensity
			float falloff = clamp(1.0f - (dist/radius), 0.0f, 1.0f);
			float intensity = falloff * lights[i].color.w * lights[i].intensity;
			totalIntens += intensity;
			
			//Color
			sumR += intensity*lights[i].color.x;
			sumG += intensity*lights[i].color.y;
			sumB += intensity*lights[i].color.z;
			
		}
	}
	
	//lcolor = vec4(max(sumR*2.0f,0.0f), max(sumG*1.0f,0.0f), max(sumB*1.0f,0.0f), 1.0f);
	//intens = min(1.0f,maxIntens);
	intens = totalIntens;
	
	/*
	 * Perceptual brightness isn't the same across RGB, so we need to convert
	 * backwards from intensity (luminance) to chroma so different-colored lights
	 * will look about the same brightness. It's not perfect - it feels like you
	 * can sun-tan in magenta light - but it does a pretty good job.
	 */
	lcolor = vec4(sumR*redScale, sumG*greenScale, sumB*blueScale, 1.0f);
}