#version 150 core

#define ALWAYS_CIRCLE 4

uniform float circles = 10;

out vec4 out_Color;

in float pass_Brightness;
in float pass_Size;
in vec4 pass_Color;

void main(void) {
	float c = pass_Brightness;
	if(circles>0 || pass_Size>ALWAYS_CIRCLE) {
		float d = distance(gl_PointCoord, vec2(0.5, 0.5));
		float delta = fwidth(d);
		c = 1 - smoothstep(0.5-delta, 0.5, d);
		
		if(pass_Size<3)
			c = 0;
	}
	out_Color = min(c, 1) * pass_Color;
}
