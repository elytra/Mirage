#version 120

varying float shift;
varying vec3 position;
varying float intens;
varying vec4 lcolor;
varying vec4 uv;
varying vec4 brightcol;
varying vec3 lightsum;

uniform sampler2D sampler;
uniform sampler2D lightmap;
uniform sampler2D brightlayer;
uniform vec3 playerPos;
uniform vec4 colorMult;
uniform int fogEnabled;

void main()
{
	vec3 lightdark = texture2D(lightmap,gl_TexCoord[1].st).xyz;
	lightdark = clamp(lightdark,0.0f,1.0f);
	
	vec4 lcolor = vec4(max(lightdark,lcolor.xyz),lcolor.w);
	
	vec4 baseColor = gl_Color * texture2D(sampler,gl_TexCoord[0].st);
	if (baseColor.w == 1 && !(baseColor.x == 0 && baseColor.y == 0 && baseColor.z == 0)){
		baseColor = vec4(mix(baseColor.xyz,colorMult.xyz,colorMult.w),baseColor.w);
	}
	
	if (brightcol.w == 0 || brightcol.x < 0 || brightcol.y < 0 || brightcol.z < 0 || brightcol.x == 0 && brightcol.y == 0 && brightcol.z == 0){
	}
	else {
		baseColor = mix(baseColor,vec4(brightcol.xyz,1.0f),brightcol.w);
	}
	
	if (fogEnabled == 1){
		float dist = max((gl_FragCoord.z / gl_FragCoord.w) - gl_Fog.start,0.0);
		
		float fog = gl_Fog.density * dist * gl_Fog.density * dist;
					  
		fog = 1.0-clamp( fog, 0.0, 1.0 );
		  
		baseColor = vec4(mix( vec3( gl_Fog.color ), baseColor.xyz, fog ).xyz,baseColor.w);
	}
	
	vec4 color = vec4((mix(baseColor.xyz*lightdark,baseColor.xyz*lcolor.xyz,intens)),baseColor.w);
	gl_FragColor = vec4(color.xyz * lightsum,color.w);
}