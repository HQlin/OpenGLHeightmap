//res/raw/heightmap_vertex_shader.glsl
uniform mat4 u_Matrix;
attribute vec3 a_Position;
varying vec3 v_Color;

void main()
{
	//在2个不同颜色做平滑插值，高度处于0到1之间，接近0为绿色，接近1为灰色
	v_Color = mix(vec3(0.180, 0.467, 0.153),//绿色
				  vec3(0.660, 0.670, 0.680),//灰色
				  a_Position.y);
				  
	gl_Position = u_Matrix * vec4(a_Position, 1.0);
}