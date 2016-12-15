package objects;

import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PVector;

/**
 * Draws raw pcm data in a circle outwards from one point using lines also
 * records lines and displays them until time alive is greater than fadeOutTime
 * 
 * @author James Ronayne
 *
 */
public class AbstractVisual extends DisplayableObject {

	/**
	 * 
	 * Class used to hold info about lines
	 */
	private class Lines {
		float time, x, x2, y, y2, r, g, b;

		Lines(float time, float x, float x2, float y, float y2, float r, float g, float b) {
			this.time = time;
			this.x = x;
			this.x2 = x2;
			this.y = y;
			this.y2 = y2;
			this.r = r;
			this.g = g;
			this.b = b;
		}
	}

	public AbstractVisual(PApplet parent, float x, float y, float z) {
		super(parent);
		pos = new PVector(x, y, z);
	}

	// raw pcm data
	private float[] data;

	/**
	 * set raw pcm data
	 * 
	 * @param data
	 */
	public void setData(float[] data) {
		this.data = data;
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

		drawWavegraph();

		parent.popStyle(); // restore style attributes
		parent.colorMode(PConstants.RGB, 255);
		parent.popMatrix(); // restore transformation state
	}

	ArrayList<Lines> points = new ArrayList<Lines>();
	float fadeOutTime = 400;

	/**
	 * Draws raw pcm data in a circle outwards from one point using lines also
	 * records lines and displays them until time alive is greater than
	 * fadeOutTime
	 */
	private void drawWavegraph() { // main drawing function

		parent.strokeWeight(2);
		float a = 0;
		float angle = (2 * PConstants.PI) / 100;
		int step = data.length / 100;
		for (int i = 0; i < data.length - step; i += step) {
			float x = 0 + PApplet.cos(a) * (parent.height * data[i] + 60);
			float y = 0 + PApplet.sin(a) * (parent.height * data[i] + 60);
			float x2 = 0 + PApplet.cos(a + angle) * (parent.height * data[i + step] + 60);
			float y2 = 0 + PApplet.sin(a + angle) * (parent.height * data[i + step] + 60);

			points.add(new Lines(parent.millis(), x, x2, y, y2, r, g, b));
			a += angle;
		}

		for (int i = points.size() - 1; i >= 0; i--) {
			Lines l = points.get(i);
			float timeAlive = parent.millis() - l.time;
			if (timeAlive > fadeOutTime) {
				points.remove(i);
			} else {
				float transparency = PApplet.map(timeAlive, 0, fadeOutTime, 255, 0);
				parent.stroke(l.r, l.g, l.b, transparency);
				parent.line(l.x, l.y, l.x2, l.y2);
			}
		}

	}
}
