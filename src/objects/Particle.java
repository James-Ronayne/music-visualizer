package objects;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PShape;
import processing.core.PVector;

/**
 * @author James Ronayne
 */
public class Particle extends DisplayableObject{
	
	PShape p;
	
	private float r = 255,g = 0,b = 0;
	
	private float deltaY = 0f;

	public Particle(PApplet parent, float x, float y, float z) {
		super(parent);
		
		position(x,y,z);
		p = parent.createShape(PConstants.SPHERE,15);
	}
	
	public Particle(PApplet parent, PVector location) {
		super(parent);
		
	    this.pos = location;
	}
	
	/**
	 * Change in Y the particles position should by altered
	 * @param y
	 */
	public void deltaY(float y) {
		deltaY = y;
	}
	
	public float getY() {
		return pos.y;
	}
	
	/**
	 * Set rgb values for colour of particle
	 * @param r
	 * @param g
	 * @param b
	 */
	public void setRGB(float r, float g, float b) {
		this.r = r;
		this.g = g;
		this.b = b;
	}
 
	/**
	 * Draws particle based on rgb and deltaY values
	 */
	public void display() {
		parent.pushStyle();
		parent.pushMatrix();
		parent.lights();
		//Since our life ranges from 255 to 0 we can use it for alpha
		parent.stroke(r,g,b,255);
		parent.fill(r,g,b,255);
		parent.translate(pos.x,(pos.y - deltaY), pos.z);
		
		
		p.setStroke(false);
		p.setFill(parent.color(r,g,b));
		parent.shape(p);
		
		
		parent.popMatrix();
		parent.popStyle();
	}
}