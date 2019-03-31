#version 150 core

#define MIN_TEMP 2300
#define MAX_TEMP 40000
#define ALWAYS_CIRCLE 4

uniform float circles = 10;

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 cycleMatrix;

uniform sampler2D texSpectrum;

uniform float exposure;
uniform float contrast;

in vec3 in_Position;
in float in_Luminosity;
in float in_Temperature;

out float pass_Brightness;
out float pass_Size;
out vec4 pass_Color;

void main(void) {
	gl_Position = projectionMatrix * viewMatrix * cycleMatrix * vec4(in_Position, 1);
	
	float rf = exposure * in_Luminosity * in_Luminosity;
	if(circles>0)
		rf *= circles;
	float c = pow(rf, contrast+1);
	float r = rf * 1.12838; // 2.0/sqrt(pi)
	
	if(circles>0 || r<=ALWAYS_CIRCLE)
		gl_PointSize = r+0.5;
	else
		gl_PointSize = r*1.27324+1.0; // 4.0/pi
	pass_Brightness = c;
	pass_Size = r;
	
	if(circles>0) {
		pass_Color = vec4(1, 1, 1, 1);
	}
	else {
		float t = (in_Temperature - MIN_TEMP) / (MAX_TEMP - MIN_TEMP);
		vec4 color = texture(texSpectrum, vec2(t, 0));
		pass_Color = mix(vec4(1, 1, 1, 1), color, clamp((rf-0.75)/1.25, 0, 1));
	}
}
