package objects;

import java.util.Random;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PImage;
import processing.core.PShape;

/**
 * Creates Stage with four walls and ceiling textured and RGB square floor made
 * of different coloured tiles that change colour in time with audio
 * 
 * @author James Ronayne
 *
 */
public class Stage extends DisplayableObject {

	private int noTiles = 10;

	double seed = 1;
	Random generator = new Random(new Double(seed).longValue());
	float[][] r = new float[noTiles][noTiles], g = new float[noTiles][noTiles], b = new float[noTiles][noTiles];

	PShape p;
	PImage right, left, front, back, up;

	/**
	 * Loads textures, creates PShape and calculates width of rgb tiles
	 * 
	 * @param parent
	 */
	public Stage(PApplet parent) {
		super(parent);

		float width = 1.f / (noTiles / 2);
		// p =
		// parent.createShape(PConstants.RECT,-width/2,-width/2,width,width);
		// p =
		// parent.createShape(PConstants.QUAD,-width/2,-width/2,-width/2,width/2,width/2,width/2,width/2,-width/2);
		p = parent.createShape(PConstants.QUAD, width / 2, -width / 2, width / 2, width / 2, -width / 2, width / 2,
				-width / 2, -width / 2);

		right = parent.loadImage("space.jpg");
		left = parent.loadImage("space2.jpg");
		front = parent.loadImage("space3.jpg");
		back = parent.loadImage("space4.jpg");
		up = back;
	}

	/**
	 * Generates new colours for rgb floor
	 */
	@Override
	public void newSeed() {
		fillRGBArray();
	}

	// Filles 2D array r with new values for the colours on the RGB floor
	private void fillRGBArray() {
		for (int i = 0; i < noTiles; i++) {
			for (int j = 0; j < noTiles; j++) {
				r[i][j] = parent.random(0, 255);
			}
		}
	}

	public void display() {
		parent.pushMatrix(); // Save state
		parent.pushStyle(); // Save style attributes
		// Project from Object Space to World Space
		parent.translate(pos.x, pos.y, pos.z); // Position
		parent.scale(scale.x, scale.y, scale.z); // Scale
		parent.rotateY(rotation.y); // Set orientation (Y - roll)
		parent.rotateZ(rotation.z); // Set orientation (Z - yaw)
		parent.rotateX(rotation.x); // Set orientation (X - pitch)

		parent.noFill(); // Transparent fill
		parent.stroke(0.f); // Create black outline
		parent.strokeWeight(2 / scale.x); // Reduce effects of scaling edges
		drawStage();
		parent.popStyle(); // Restore style attributes
		parent.popMatrix(); // Restore state
	}

	private void drawStage() {
		parent.textureMode(PConstants.NORMAL);

		parent.pushStyle();
		parent.beginShape(PApplet.QUADS);

		parent.beginShape(PApplet.QUAD);
		// LEFT SIDE
		parent.normal(-1f, 0f, 0f);
		parent.texture(left);
		parent.vertex(-1.f, -1.f, -1.f, 1, 0);
		parent.vertex(-1.f, -1.f, 1.f, 0, 0);
		parent.vertex(-1.f, 0.f, 1.f, 0, 1);
		parent.vertex(-1.f, 0.f, -1.f, 1, 1);
		parent.endShape();

		parent.beginShape(PApplet.QUAD);
		// RIGHT SIDE
		parent.texture(right);
		parent.normal(1f, 0f, 0f);
		parent.vertex(1.f, -1.f, 1.f, 1, 0);
		parent.vertex(1.f, -1.f, -1.f, 0, 0);
		parent.vertex(1.f, 0.f, -1.f, 0, 1);
		parent.vertex(1.f, 0.f, 1.f, 1, 1);
		parent.endShape();

		parent.beginShape(PApplet.QUAD);
		// FAR SIDE
		parent.normal(0f, 0f, -1f);
		parent.texture(front);
		parent.vertex(1.f, -1.f, -1.f, 1, 0);
		parent.vertex(-1.f, -1.f, -1.f, 0, 0);
		parent.vertex(-1.f, 0.f, -1.f, 0, 1);
		parent.vertex(1.f, 0.f, -1.f, 1, 1);
		parent.endShape();

		parent.beginShape(PApplet.QUAD);
		// NEAR SIDE
		parent.texture(back);
		parent.normal(0f, 0f, 1f);
		parent.vertex(-1.f, -1.f, 1.f, 1, 0);
		parent.vertex(1.f, -1.f, 1.f, 0, 0);
		parent.vertex(1.f, 0.f, 1.f, 0, 1);
		parent.vertex(-1.f, 0.f, 1.f, 1, 1);
		parent.endShape();
		parent.popStyle();

		drawDancefloor();

		// Ceiling
		parent.pushMatrix();
		parent.beginShape(PApplet.QUAD);
		parent.texture(up);
		parent.normal(0f, 0f, 1f);
		parent.vertex(-1.5f, -1.2f, -1.5f, 0, 1);
		parent.vertex(1.5f, -1.2f, -1.5f, 1, 1);
		parent.vertex(1.5f, -1.2f, 1.5f, 1, 0);
		parent.vertex(-1.5f, -1.2f, 1.5f, 0, 0);
		parent.endShape();
		parent.popMatrix();
	}

	// Draws RGB floor
	private void drawDancefloor() {
		float width = 1.f / (noTiles / 2);
		parent.colorMode(PConstants.HSB, 255);

		parent.pushMatrix();
		parent.translate(-1.f - width / 2, 0, -1.f - width / 2);

		for (int i = 0; i < noTiles; i++) {

			parent.translate(0, 0, width);
			parent.pushMatrix();
			for (int j = 0; j < noTiles; j++) {

				parent.translate(width, 0, 0);
				parent.pushMatrix();
				parent.pushStyle();

				parent.rotateX((float) Math.toRadians(90));

				p.setFill(parent.color(r[i][j], 255, 255));
				p.setStrokeWeight(7);

				parent.shape(p);

				parent.popMatrix();
				parent.popStyle();
			}

			parent.popMatrix();
		}

		parent.popMatrix();
		parent.colorMode(PConstants.RGB);
	}
}