package objects;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PShape;
import processing.core.PVector;
import processing.core.PImage;

/**
 * Textured ball
 * @author James Ronayne
 */
public class Ball extends DisplayableObject {
	public PVector speed;
	
	PShape p;
	
	float gravity = 15f;
	boolean isGravity = true;
	boolean isBeatDetection = true;
	
	public int radius;
	
	public Ball(PApplet parent, PVector speed, int radius, float x, float y, float z, PImage img) {
		super(parent);
	    this.speed = speed;
	    this.radius = radius;
	    pos = new PVector(x,y,z);

        parent.sphereDetail(6);
	    p = parent.createShape();
	    
	    // decides detail of sphere
	    initSphere(15, 15);
	    // Draws shape and saves in PShape p
		drawSphere(radius, img);
	}
	
	/**
	 * Turns beat detection on or off
	 */
	public void beatDetection() {
		if(isBeatDetection) {
			isBeatDetection = false;
		} else {
			isBeatDetection = true;
		}
	}
	
	/**
	 * Turns gravity on or off
	 */
	public void gravity() {
		if(isGravity) {
			isGravity = false;
		} else {
			isGravity = true;
		}
	}
	
	public void beat() {
		if(isBeatDetection) {
			pos.y -= parent.random(parent.height*0.023f,parent.height*0.091f);
		}
	}
	
	/**
	 * Moves ball according to speed vector and gravity
	 * also does bounds checking
	 */
	public void move() {
		if(isGravity) speed.y += gravity;
	    
	    pos.add(speed);
	    rotation.add(PVector.mult(speed,0.008f));
	    
	    if(pos.x > (parent.height*4)/2-radius) {
	      pos.x= (parent.height*4)/2-radius;
	      speed.x*=-1;
	    }
	    else if (pos.x < -(parent.height*4)/2 + radius) {
	      pos.x = -(parent.height*4)/2+radius;
	      speed.x*=-1;
	    }
	    if (pos.y > 0/2-radius) {
	      pos.y = 0/2-radius ;
	      speed.y*= -1;
	    }
	    else if (pos.y < -(parent.height*4)/2 + radius) {
	      pos.y= -(parent.height*4)/2+radius;
	      speed.y*= -1;
	    }
	    if (pos.z > (parent.height*4)/2-radius) {
	      pos.z = (parent.height*4)/2-radius;
	      speed.z*= -1;
	    }
	    else if (pos.z < -(parent.height*4)/2 + radius) {
	      pos.z = -(parent.height*4)/2 + radius;
	      speed.z*= -1;
	    }
	}
	
	/**
	 * Draws sphere contained in p
	 */
	public void display() {
		parent.pushStyle();
		parent.pushMatrix();
		parent.translate(pos.x,pos.y, pos.z);

        parent.rotateY(rotation.y);                     // Set orientation (Y - roll)
        parent.rotateZ(rotation.z);                     // Set orientation (Z - yaw)
        parent.rotateX(rotation.x);                     // Set orientation (X - pitch)
		
		parent.textureMode(PConstants.NORMAL);
		parent.noStroke();
		
		p.setStroke(false);
		parent.shape(p);
		
		
		parent.popMatrix();
		parent.popStyle();
	}
	
	int pointsWidth;
	int pointsHeight2PI; 
	int pointsHeight;

	float[] x;
	float[] y;
	float[] z;
	float[] multXZ;
	
	void initSphere(int numPtsW, int numPtsH_2pi) {

		// The number of points around the width and height
		pointsWidth = numPtsW + 1;
		pointsHeight2PI = numPtsH_2pi;  // How many actual pts around the sphere (not just from top to bottom)
		pointsHeight = PApplet.ceil((float) pointsHeight2PI / 2) + 1;  // How many pts from top to bottom (abs(....) b/c of the possibility of an odd numPointsH_2pi)

		x = new float[pointsWidth];   // All the x-coor in a horizontal circle radius 1
		y = new float[pointsHeight];   // All the y-coor in a vertical circle radius 1
		z = new float[pointsWidth];   // All the z-coor in a horizontal circle radius 1
		multXZ = new float[pointsHeight];  // The radius of each horizontal circle (that you will multiply with coorX and coorZ)

		for (int i = 0; i < pointsWidth; i++) {  // For all the points around the width
			float thetaW = i*2*PApplet.PI/(pointsWidth-1);
			x[i] = PApplet.sin(thetaW);
			z[i] = PApplet.cos(thetaW);
		}
		  
		for (int i = 0; i < pointsHeight; i++) {  // For all points from top to bottom
			if ((int) (pointsHeight2PI/2) != (float) pointsHeight2PI/2 && i == pointsHeight - 1) {  // If the numPointsH_2pi is odd and it is at the last pt
				float thetaH = (i-1) * 2 * PApplet.PI / (pointsHeight2PI);
				y[i] = PApplet.cos(PApplet.PI + thetaH); 
				multXZ[i] = 0;
			} else {
				//The numPointsH_2pi and 2 below allows there to be a flat bottom if the numPointsH is odd
				float thetaH = i * 2 * PApplet.PI / (pointsHeight2PI);

				//PI+ below makes the top always the point instead of the bottom.
				y[i] = PApplet.cos(PApplet.PI + thetaH); 
				multXZ[i] = PApplet.sin(thetaH);
			}
		}
	}

	void drawSphere(float radius, PImage t) { 
		// These are so we can map certain parts of the image on to the shape 
		float changeU = t.width / (float) (pointsWidth - 1); 
		float changeV = t.height / (float) (pointsHeight - 1); 
		float u = 0;  // Width variable for the texture
		float v = 0;  // Height variable for the texture

		p.beginShape(PApplet.TRIANGLE_STRIP);
		p.texture(t);
		for (int i = 0; i < (pointsHeight - 1); i++) {
			// Goes into the array here instead of loop to save time
			float coory = y[i];
			float cooryPlus = y[i+1];

			float multxz = multXZ[i];
			float multxzPlus = multXZ[i+1];

			for (int j = 0; j < pointsWidth; j++) {
		    	p.normal(-x[j] * multxz, -coory, -z[j] * multxz);
		    	p.vertex(x[j] * multxz * radius, coory * radius, z[j] * multxz * radius, u, v);
		    	p.normal(-x[j] * multxzPlus, -cooryPlus, -z[j] * multxzPlus);
		    	p.vertex(x[j] * multxzPlus * radius, cooryPlus * radius, z[j] * multxzPlus * radius, u, v + changeV);
		    	u += changeU;
			}
			v += changeV;
			u = 0;
		}
		p.endShape();
	}
}
