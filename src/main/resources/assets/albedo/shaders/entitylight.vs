#version 120
varying float shift;
varying vec3 position;
varying vec4 uv;
varying vec4 lcolor;
varying vec4 brightcol;
varying float intens;
varying vec3 lightsum;

struct Light{
    vec4 color;
    vec3 position;
	float radius;
};

uniform vec3 entityPos;
uniform vec3 playerPos;
uniform sampler2D sampler;
uniform sampler2D lightmap;
uniform mat4 modelview;
uniform Light lights[100];
uniform int lightCount;
uniform int maxLights;
uniform float ticks;
uniform int flickerMode;
uniform int lightingEnabled;
uniform sampler2D brightlayer;

float rand2(vec2 co){
    return fract(sin(dot(co.xy ,vec2(12.9898,78.233))) * 43758.5453);
}

vec3 rand3(vec3 co){
    return vec3(rand2(co.xz)-0.5f,rand2(co.yx)-0.5f,rand2(co.zy)-0.5f);
}

float distSq(vec3 a, vec3 b){
	return pow((a.x-b.x),2)+pow((a.y-b.y),2)+pow((a.z-b.z),2);
}

void main()
{
    vec4 pos = gl_ModelViewProjectionMatrix * gl_Vertex;
	
	position = gl_Vertex.xyz+entityPos;
	
	float offset = 0;
	
	gl_TexCoord[0] = gl_TextureMatrix[0] * gl_MultiTexCoord0;
	gl_TexCoord[1] = gl_TextureMatrix[1] * gl_MultiTexCoord1;
		
	vec3 normal = (gl_NormalMatrix * gl_Normal).xyz; 
	
	gl_Position = gl_ModelViewProjectionMatrix * (gl_Vertex + vec4(0,offset,0,0));
	
	gl_FrontColor = gl_Color;
	
	if (lightingEnabled == 1 || gl_Normal.x == 0 && gl_Normal.y == 0 && gl_Normal.z == 0){
		lcolor = vec4(0,0,0,1.0f);
		float sumR = 0;
		float sumG = 0;
		float sumB = 0;
		float count = 0;
		float maxIntens = 0;
		float totalIntens = 0;
		for (int i = 0; i < lightCount; i ++){
			if (distSq(lights[i].position,position) <= pow(lights[i].radius,2)){
				float intensity = max(0,1.0f-distance(lights[i].position,position)/(lights[i].radius)) * 1.0f * lights[i].color.w;
				totalIntens += intensity;
				maxIntens = max(maxIntens,intensity);
			}
		}
		for (int i = 0; i < lightCount; i ++){
			if (distSq(lights[i].position,position) <= pow(lights[i].radius,2)){
				float intensity = max(0.0f,1.0f-distance(lights[i].position,position)/(lights[i].radius)) * 1.0f * lights[i].color.w;
				sumR += (intensity/totalIntens)*lights[i].color.x;
				sumG += (intensity/totalIntens)*lights[i].color.y;
				sumB += (intensity/totalIntens)*lights[i].color.z;
			}
		}
		lcolor = vec4(max(sumR*1.5f,0.0f), max(sumG*1.5f,0.0f), max(sumB*1.5f,0.0f), 1.0f);  
		
		vec4 ambtot = vec4(0);
		vec4 difftot = vec4(0);
		vec4 spectot = vec4(0);
		
		for (int i = 0; i < 9; i ++){
			vec4 amb = gl_FrontLightProduct[i].ambient;    
			
			vec4 diff = gl_FrontLightProduct[i].diffuse * max(dot(normal,gl_LightSource[i].position.xyz), 0.0f);
			diff = clamp(diff, 0.0f, 1.0f);     
		   
			/*vec4 spec = gl_FrontLightProduct[i].specular 
				* pow(max(dot(R,E),0.0),gl_FrontMaterial.shininess);
			spec = clamp(spec, 0.0f, 1.0f); */
			
			//ambtot += amb;
			difftot += diff;
			//spectot += spec;
		}
		
		ambtot += gl_LightModel.ambient;
		
		vec3 templight = (ambtot + difftot).xyz;
		lightsum = clamp(templight, 0.0f, 1.0f);
		intens = min(1.0f,maxIntens);
	}
	else {
		lcolor = vec4(1,1,1,1);
		lightsum = vec3(1.0f);
		intens = 0;
	}
	
}