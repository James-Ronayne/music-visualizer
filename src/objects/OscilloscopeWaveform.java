package objects;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PVector;

/**
 * Simple draws raw pcm data waveform
 * 
 * @author James Ronayne
 *
 */
public class OscilloscopeWaveform extends DisplayableObject {

	public OscilloscopeWaveform(PApplet parent, float x, float y, float z) {
		super(parent);

		pos = new PVector(x, y, z);
	}

	// raw pcm data
	private float[] data;

	/**
	 * sets raw pcm data
	 * 
	 * @param data
	 */
	public void setData(float[] data) {
		this.data = data;
	}

	@Override
	public void display() {
		parent.colorMode(PConstants.HSB, 255);
		parent.pushMatrix(); // save transformation state
		parent.pushStyle(); // save style attributes

		parent.lights();
		parent.strokeWeight(2);

		parent.stroke(r, g, b);

		// Project from Object Space to World Space
		parent.translate(pos.x, pos.y, pos.z); // Position
		parent.scale(scale.x, scale.y, scale.z); // Scale
		parent.rotateY(rotation.y); // Set orientation (Y - roll)
		parent.rotateZ(rotation.z); // Set orientation (Z - yaw)
		parent.rotateX(rotation.x); // Set orientation (X - pitch)
		draw(); // draw Wavegraph

		parent.popStyle(); // restore style attributes
		parent.popMatrix(); // restore transformation state
		parent.colorMode(PConstants.RGB, 255);
	}

	/**
	 * draws waveform ising lines
	 */
	void draw() {
		float width = parent.height * 4;
		float height = parent.height;
		float inc = width / data.length;

		parent.pushMatrix();

		parent.translate(0, 0, -height * 2);

		float total = 0;
		for (int i = 0; i < data.length - 1; i++) {
			float mix1 = (height * 1.1f) + data[i] * 700;
			float mix2 = (height * 1.1f) + data[i + 1] * 700;
			parent.line(total, mix1, total + inc, mix2);
			total = total + inc;
		}

		parent.popMatrix();
	}
}
