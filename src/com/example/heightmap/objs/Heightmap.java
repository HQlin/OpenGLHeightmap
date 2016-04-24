package com.example.heightmap.objs;

import static android.opengl.GLES20.GL_ELEMENT_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.GL_UNSIGNED_SHORT;
import static android.opengl.GLES20.glBindBuffer;
import static android.opengl.GLES20.glDrawElements;

import com.example.heightmap.data.IndexBuffer;
import com.example.heightmap.data.VertexBuffer;
import com.example.heightmap.programs.HeightmapShaderProgram;

import android.graphics.Bitmap;
import android.graphics.Color;

public class Heightmap {
	private static final int POSITION_COMPONENT_COUNT = 3;
	
	private final int width;
	private final int height;
	private final int numElements;
	private final VertexBuffer vertexBuffer;
	private final IndexBuffer indexBuffer;
	
	public Heightmap(Bitmap bitmap) {
		// TODO Auto-generated constructor stub
		width = bitmap.getWidth();
		height = bitmap.getHeight();
		
		System.out.println( width*height );
		if(width*height>65536){
			throw new RuntimeException("Heightmap高度超出索引缓冲区");
		}
		numElements = calculateNumElements();
		vertexBuffer = new VertexBuffer(loadBitmapData(bitmap));
		indexBuffer = new IndexBuffer(creatIndexData());
	}

	/**
	 * 生成索引数据
	 * @return
	 */
	private short[] creatIndexData() {
		// TODO Auto-generated method stub
		final short[] indexData = new short[numElements];
		int offset = 0;
		
		for(int row = 0; row < height - 1; row++){
			for(int col = 0; col < width - 1; col++){
				short topLeftIndexNum = (short)(row*width + col);
				short topRightIndexNum = (short)(row*width + col + 1);
				short bottomLeftIndexNum = (short)((row+1)*width + col);
				short bottomRightIndexNum = (short)((row+1)*width + col + 1);
				
				indexData[offset++] = topLeftIndexNum;
				indexData[offset++] = bottomLeftIndexNum;
				indexData[offset++] = topRightIndexNum;
				
				indexData[offset++] = topRightIndexNum;
				indexData[offset++] = bottomLeftIndexNum;
				indexData[offset++] = bottomRightIndexNum;
 			}
		}
		return indexData;
	}

	/**
	 * 计算索引数量，比如，一个3x3的高度图有（3-1）x（3-1）=4个组，以及每个组需要2个三角形和每个三角形需要3个元素，总共得到24个索引
	 * @return
	 */
	private int calculateNumElements() {
		// TODO Auto-generated method stub
		return (width-1)*(height-1)*2*3;
	}

	private float[] loadBitmapData(Bitmap bitmap) {
		// TODO Auto-generated method stub
		final int[] pixels = new int[width*height];
		//获取图片的像素点
		bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
		bitmap.recycle();
		
		final float[] heightmapVertices = new float[width*height*POSITION_COMPONENT_COUNT];
		int offset = 0;
		
		//把位图像素转换为高度图数据
		//高度图在每个方向上都是1个单位宽，且其以x-z平面上的位置（0,0）为中心，位图的最上角将被映射到（-0.5，-0.5），右下角会被映射到（0.5,0.5）
		//像素偏移值 = 当前行*高度 + 当前列
		//一行一行地读取位图的原因在于其在内存中的布局方式就是这样的，当CPU按顺序缓存和移动数据时，它们更有效率
		for(int row = 0; row < height; row++){
			for(int col = 0; col < width; col++){
				final float xPosition = ((float)col/(float)(width-1)) - 0.5f;
				final float yPosition = (float)Color.red(pixels[(row*height)+col])/(float)255;
				final float zPosition = ((float)row/(float)(height-1))-0.5f;
				
				heightmapVertices[offset++] = xPosition;
				heightmapVertices[offset++] = yPosition;
				heightmapVertices[offset++] = zPosition;
 			}
		}
		return heightmapVertices;
	}
	
	public void bindData(HeightmapShaderProgram heightmapProgram){
		vertexBuffer.setVertexAttribPointer(0, 
				heightmapProgram.setPositionAttributeLocation(), 
				POSITION_COMPONENT_COUNT, 0);
	}
	
	public void draw(){
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexBuffer.getBufferId());
		glDrawElements(GL_TRIANGLES, numElements, GL_UNSIGNED_SHORT, 0);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
	}
	
}
