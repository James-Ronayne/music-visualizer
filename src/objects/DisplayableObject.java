package objects;

import processing.core.*;

public abstract class DisplayableObject {
	protected PApplet parent; // renderer (MyScene)
	// initialise world space properties
	protected PVector pos = new PVector(0.f, 0.f, 0.f); // position in World
														// Space
	protected PVector scale = new PVector(1.f, 1.f, 1.f); // size in World Space
	protected PVector rotation = new PVector(0.f, 0.f, 0.f); // orientation in
																// World Space

	public DisplayableObject(PApplet parent) {
		this.parent = parent;
	} // Constructor

	public abstract void display(); // display function (MUST OVERLOAD)

	public void position(float x, float y, float z) {
		pos.set(x, y, z);
	} // set World Space position

	public void size(float s) {
		size(s, s, s);
	} // set size in World Space

	public void size(float x, float y, float z) {
		scale.set(x, y, z);
	} // set size in World Space

	public void orientation(float rx, float ry, float rz) {
		rotation.set(rx, ry, rz);
	}

	public PVector position() {
		return pos;
	} // get position in World Space

	public PVector size() {
		return scale;
	} // get size in World Space

	public PVector orientation() {
		return rotation;
	} // get World Space orientation

	/**
	 * Used to generate new bright colours for objects as r value is used in
	 * conjunction with HSB colour mode with b and g being the value 255
	 */
	public void newSeed() {
		r = parent.random(0, 255);
	}

	float r = 0, g = 255, b = 255;
}