package com.example.heightmap.programs;

import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniformMatrix4fv;

import com.example.heightmap.R;

import android.content.Context;

public class HeightmapShaderProgram extends ShaderProgram{

	private final int uMatrixLocation;
	private final int aPositionLocation;
	
	public HeightmapShaderProgram(Context context) {
		super(context, R.raw.heightmap_vertex_shader, R.raw.heightmap_fragment_shader);

		uMatrixLocation = glGetUniformLocation(program, U_MATRIX);
		aPositionLocation = glGetAttribLocation(program, A_POSITION);
	}

	public int setPositionAttributeLocation() {
		// TODO Auto-generated method stub
		return aPositionLocation;
	}

	public void setUniforms(float[] modelViewProjectionMatrix) {
		// TODO Auto-generated method stub
		glUniformMatrix4fv(uMatrixLocation, 1, false, modelViewProjectionMatrix, 0);
	}
	
}
