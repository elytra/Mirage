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

const float redScale   = 1.0f - 0.7152f;//0.2126f; //turns out red color sensitivity and red perceptual brightness are RATHER DIFFERENT
const float greenScale = 1.0f - 0.7152f;
const float blueScale  = 1.0f - 0.0722f;

//const float falloff = 0.1f;

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
			float coneStrength = 1.0f;
		
			//Cone
			vec3 coneVec  = normalize(lights[i].coneDirection);
			vec3 incident = normalize(position - lights[i].position);
			float angularDifference = dot(coneVec, incident);
			if (angularDifference<lights[i].coneFalloff) {
				coneStrength = 1-clamp((lights[i].coneFalloff - angularDifference) * 2.15f, 0, 1);
			}
			
			//Intensity
			float falloff = clamp(1.0f - (dist/radius), 0.0f, 1.0f);
			float intensity = falloff * lights[i].color.w * lights[i].intensity;
			totalIntens += intensity*coneStrength;
			
			//Color
			sumR += lights[i].color.x;
			sumG += lights[i].color.y;
			sumB += lights[i].color.z;
			
		}
	}
	
	intens = totalIntens;

	float averageLuma = (sumR+sumG+sumB) / 3.0f;
	float conv = clamp(abs(totalIntens) / abs(averageLuma), 0, 1); 
	lcolor = vec4(sumR*conv, sumG*conv, sumB*conv, 1.0f);
}