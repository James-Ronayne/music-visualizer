package objects;

import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PVector;

/**
 * Draws squares on wall that are generated in time with music. The squares
 * rotate, shrink and fade until they are destroyed after there life span
 * 
 * @author James Ronayne
 */
public class SquareSpin extends DisplayableObject {

	private ArrayList<Square> squares = new ArrayList<Square>();

	private boolean newSquare = true;

	public SquareSpin(PApplet parent, float x, float y, float z) {
		super(parent);
		pos = new PVector(x, y, z);
	}

	/**
	 * Requests new square to be added
	 */
	public void newSquare() {
		newSquare = true;
	}

	@Override
	public void display() {
		parent.pushMatrix(); // save transformation state
		parent.colorMode(PConstants.HSB, 255);
		parent.pushStyle(); // save style attributes
		parent.noFill();
		parent.specular(r, g, b);

		parent.lights();

		// Project from Object Space to World Space
		parent.translate(pos.x, pos.y, pos.z); // Position
		parent.scale(scale.x, scale.y, scale.z); // Scale
		parent.rotateY(rotation.y); // Set orientation (Y - roll)
		parent.rotateZ(rotation.z); // Set orientation (Z - yaw)
		parent.rotateX(rotation.x); // Set orientation (X - pitch)

		drawSquare();

		parent.popStyle(); // restore style attributes
		parent.colorMode(PConstants.RGB, 255);
		parent.popMatrix(); // restore transformation state
	}

	/**
	 * Draws all squares contained in squares arraylist and adds new square if
	 * requested
	 */
	private void drawSquare() {
		if (newSquare) {
			squares.add(new Square(parent));

			newSquare = false;
		}

		for (int i = squares.size() - 1; i >= 0; i--) {
			Square square = squares.get(i);

			parent.translate(-parent.height / 50, 0);
			square.run();

			// If dead remove from arraylist
			if (square.isDead()) {
				squares.remove(i);
			}
		}
	}
}
