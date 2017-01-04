package com.rjm.dropout.frontier.objects;

import javafx.scene.shape.TriangleMesh;

public class Shape3DHexagon extends TriangleMesh {

	public void OLDShape3DHexagon(float Width, float Height) {
		float[] points = {
				0,		0,		0,
				-50,		30,		0,
				0,		60,		0,
				50,		30,		0,
				50,		-30,		0,
				0,		-60,		0,
				-50,		-30,		0
		};
		float[] texCoords = {
				0,		0,
				-1,		1,
				0,		2,
				1,		1,
				1,		-1,
				0,		-2,
				-1,		-1
		};
		/**
		 * points:						
		 * 			2(0,2)
		 *  							
		 *  1(-1,1)			3(1,1)
		 *  			
		 *  		0(0,0)
		 *  6(-1,-1)		4(1,-1)
		 *  			
		 *  		5(0,-2)
		 * 
		 * texture[0] 0,0 maps to vertex 0
		 * texture[1] -1,1 maps to vertex 1
		 * texture[2] 0,2 maps to vertex 2
		 * texture[3] 1,1 maps to vertex 3
		 * texture[4] 1,-1 maps to vertex 4
		 * texture[5] 0,-2 maps to vertex 5
		 * texture[6] -1,-1 maps to vertex 6
		 *
		 * Two triangles define rectangular faces:
		 * p0, t0, p1, t1, p2, t2 // First triangle of a textured rectangle
		 * p0, t0, p2, t2, p3, t3 // Second triangle of a textured rectangle
		 */

		int[] faces = {
				/*2,2,0,0,1,1,*/1,1,0,0,2,2,
				/*3,3,0,0,2,2,*/2,2,0,0,3,3,
				/*4,4,0,0,3,3,*/3,3,0,0,4,4,
				/*5,5,0,0,4,4,*/4,4,0,0,5,5,
				/*6,6,0,0,5,5,*/5,5,0,0,6,6,
				/*1,1,0,0,6,6*/6,6,0,0,1,1,
		};

		this.getPoints().setAll(points);
		this.getTexCoords().setAll(texCoords);
		this.getFaces().setAll(faces);
	}

	public Shape3DHexagon(float Width, float Height) {
		float[] points = {
//				0,		0,		0,
//				-30,	50,		0,
//				30,		50,		0,
//				60,		0,		0,
//				30,		-50,	0,
//				-30,	-50,	0,
//				-60,	0,		0
				
				-60,	0,		0,
				-30,	-50,	0,
				-30,	50,		0,
				30,		-50,	0,
				30,		50,		0,
				60,		0,		0
		};
		float[] texCoords = {
//				0,		0,
//				-1,		1,
//				1,		1,
//				2,		0,
//				1,		-1,
//				-1,		-1,
//				-2,		0
				
//				-2,		0,
//				-1,		-1,
//				-1,		1,
//				1,		-1,
//				1,		1,
//				2,		0
				
				0,		0.5f,
				0.25f,	0,
				0.25f,	1,
				0.75f,	0,
				0.75f,	1,
				1,		0.5f
		};
		/**
		 * points:			
		 *  							
		 *  	1(-1,1)		  2(1,1)
		 *  
		 *  			
		 *  
		 * 6(-2,0) 		0(0,0)		3(2,0)
		 *  
		 *  
		 *  
		 *      5(-1,-1)	  4(1,-1)
		 *  			
		 * 
		 * texture[0] 0,0 maps to vertex 0
		 * texture[1] -1,1 maps to vertex 1
		 * texture[2] 0,2 maps to vertex 2
		 * texture[3] 1,1 maps to vertex 3
		 * texture[4] 1,-1 maps to vertex 4
		 * texture[5] 0,-2 maps to vertex 5
		 * texture[6] -1,-1 maps to vertex 6
		 *
		 * Two triangles define rectangular faces:
		 * p0, t0, p1, t1, p2, t2 // First triangle of a textured rectangle
		 * p0, t0, p2, t2, p3, t3 // Second triangle of a textured rectangle
		 */

		
//		   2----4
//		 / | \  | \
//		 0 |  \ | 5
//		 \ |   \| /
//		   1----3
		
		int[] faces = {
//				/*2,2,0,0,1,1,*/1,1,0,0,2,2,
//				/*3,3,0,0,2,2,*/2,2,0,0,3,3,
//				/*4,4,0,0,3,3,*/3,3,0,0,4,4,
//				/*5,5,0,0,4,4,*/4,4,0,0,5,5,
//				/*6,6,0,0,5,5,*/5,5,0,0,6,6,
//				/*1,1,0,0,6,6*/6,6,0,0,1,1,
				
//				2,2,1,1,0,0,
//				2,2,0,0,3,3,
//				0,0,3,3,4,4,
//				4,4,0,0,5,5,
//				5,5,0,0,6,6,
//				6,6,0,0,1,1
				
				0,0,1,1,2,2,
//				1,1,2,2,3,3,
				2,2,1,1,3,3,
				2,2,3,3,4,4,
				4,4,3,3,5,5
		};

		this.getPoints().setAll(points);
		this.getTexCoords().setAll(texCoords);
		this.getFaces().setAll(faces);
	}
}