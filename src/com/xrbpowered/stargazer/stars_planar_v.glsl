#version 150 core

in vec3 in_Position;
in float in_Brightness;
in float in_Size;

out float pass_Brightness;
out float pass_Size;

void main(void) {
	gl_Position = vec4(in_Position, 1);
	gl_PointSize = in_Size+1;
	pass_Brightness = in_Brightness;
	pass_Size = in_Size;
}
