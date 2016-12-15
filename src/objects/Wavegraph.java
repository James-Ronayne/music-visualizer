package objects;

import ddf.minim.analysis.FFT;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PVector;

/**
 * Draws simple equaliser along one wall
 * 
 * @author James Ronayne
 */
public class Wavegraph extends DisplayableObject {
	FFT fft;

	private float offset;

	public Wavegraph(PApplet parent, float x, float y, float z) {
		super(parent);
		pos = new PVector(x, y, z);
	}

	public void setFFT(FFT fft) {
		this.fft = fft;

		offset = (parent.height * 4) / (fft.avgSize());
	}

	public void display() { // define display function (to be called by MyScene)
		parent.pushMatrix(); // save transformation state
		parent.colorMode(PConstants.HSB, 255);
		parent.pushStyle(); // save style attributes
		parent.fill(r, g, b, 127); // colour teal
		parent.specular(r, g, b);

		parent.lights();

		// Project from Object Space to World Space
		parent.translate(pos.x, pos.y, pos.z); // Position
		parent.scale(scale.x, scale.y, scale.z); // Scale
		parent.rotateY(rotation.y); // Set orientation (Y - roll)
		parent.rotateZ(rotation.z); // Set orientation (Z - yaw)
		parent.rotateX(rotation.x); // Set orientation (X - pitch)
		drawWavegraph(); // draw Wavegraph

		parent.popStyle(); // restore style attributes
		parent.colorMode(PConstants.RGB, 255);
		parent.popMatrix(); // restore transformation state
	}

	private void drawWavegraph() { // main drawing function

		parent.pushMatrix();
		for (int i = 0; i < fft.avgSize(); i++) {
			if (fft.getAvg(i) * 6 > parent.height * 2) {
				parent.translate(0, -parent.height, 0);
				parent.box(offset, parent.height * 2, 10);
				parent.translate(0, parent.height, 0);
			} else {
				parent.translate(0, -fft.getAvg(i) * 3, 0);
				parent.box(offset, fft.getAvg(i) * 6, 10);
				parent.translate(0, fft.getAvg(i) * 3, 0);
			}

			parent.translate(offset, 0, 0);
		}
		parent.popMatrix();
	}
}
