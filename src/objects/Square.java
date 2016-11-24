package objects;

import processing.core.PApplet;
import processing.core.PConstants;

/**
 * @author James Ronayne
 */
public class Square extends DisplayableObject {
	
	private float rotation = 0, rotationInc;
	private float size, sizeDecrement;
	private float decrement, sinceLastUpdate;
	//A new variable to keep track of how long the particle has been “alive”
	float lifespan;
	
	// Lifespan of square in milliseconds
	private float LIFESPAN_MILLIS = 40000;

	public Square(PApplet parent) {
		super(parent);
		
		size = parent.height/3*2;
		sizeDecrement = size/LIFESPAN_MILLIS;
		sinceLastUpdate = parent.millis();

		//We start at 255 and count down for convenience
		lifespan = 255;
		decrement = lifespan/LIFESPAN_MILLIS;
		
		newSeed();
		
		rotationInc = 360/LIFESPAN_MILLIS;
	}

	/**
	 * Draws square
	 */
	@Override
	public void display() {
		parent.pushStyle();
		parent.pushMatrix();
		parent.translate(-10, 0);
		
		parent.strokeWeight(5);
		
		parent.lights();
		parent.rotateY(PApplet.radians(90));
		parent.rotateZ(PApplet.radians(rotation));
		//Since our life ranges from 255 to 0 we can use it for alpha
		parent.stroke(0,lifespan);
		parent.fill(r,g,b,lifespan);
		parent.lights();
		
		parent.beginShape(PConstants.QUAD);
		parent.vertex(-size, size, 0,1,1);
        parent.vertex(size, size, -0,0,1);
        parent.vertex(size, -size, -0, 0,0);
        parent.vertex(-size, -size, 0,0,0);
        parent.endShape();
		
		
		parent.popMatrix();
		parent.popStyle();
	}
	
	/**
	 * Shrinks, rotates and changes opacity according to time elapsed since creation
	 */
	void update() {
		int millisElapsed = (int) (sinceLastUpdate - parent.millis());
		
		//lifespan update
		float dec = millisElapsed*-decrement;
		lifespan = lifespan - dec;
		
		//rotation update
		if(rotationInc+rotation < 360) {
			rotation = (rotationInc*millisElapsed)+rotation;
		} else {
			float diff = (rotationInc*millisElapsed)+rotation - 360;
			rotation = 0 + diff;
		}
		
		//size update
		size = size + (sizeDecrement*millisElapsed);
	}
	
	/**
	 * updates positon and size data then draws
	 */
	public void run() {
		update();
		display();
	}
	
	/**
	 * Checks if squared has died (no longer visible)
	 * @return
	 */
	public boolean isDead() {
		if (lifespan < 0.0) {
			return true;
		} else {
			return false;
		}
	}
}
