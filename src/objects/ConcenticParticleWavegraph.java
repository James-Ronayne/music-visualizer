package objects;

import java.util.ArrayList;

import ddf.minim.analysis.FFT;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PVector;

/**
 * 
 * @author James Ronayne
 *
 */
public class ConcenticParticleWavegraph extends DisplayableObject {

	ArrayList<ArrayList<Particle>> particles = new ArrayList<ArrayList<Particle>>();
	PVector origin;
	
	// fft from minim containing freq amplitude data
	FFT fft;
	
	//for every pixel calculate an amplitude
	float[] pixeledAmplitudes;
	
	private float radiusSpacing;
	
	private ArrayList<ArrayList<float[]>> particlePositions = new ArrayList<ArrayList<float[]>>();
	
	private int particlesPerLayer = 5, binlength;
	
	/**
	 * Returns rgb value
	 * @return rgb array
	 */
	public float[] getRGB() {
		float[] arr = {r, 255f, 255f};
		return arr;
	}
	
	/**
	 * Constructor calculates particles positions and creates particles
	 * @param parent
	 * @param location
	 * @param radiusSpacing
	 * @param binlength
	 * @param particlesPerLayer
	 */
	public ConcenticParticleWavegraph(PApplet parent, PVector location, float radiusSpacing, int binlength, int particlesPerLayer) {
		super(parent);
		this.radiusSpacing = radiusSpacing;
		this.binlength = binlength;
		this.particlesPerLayer = particlesPerLayer;
		origin = location.copy();
		
		calculatePositions();
		addParticles();
	}
	
	/**
	 * Scales object for scene by adjusting radius spacing
	 * @param location
	 * @param radiusSpacing
	 */
	public void scaleToResolution(PVector location, float radiusSpacing) {
		this.radiusSpacing = radiusSpacing;
		origin = location.copy();
		particlePositions.clear();
		calculatePositions();
	}
	
	/**
	 * Calculates xz coords for particles based on spacing, binLength and particles per layer
	 */
	public void calculatePositions() {
		ArrayList<float[]> positions = new ArrayList<float[]>();
		
		float[] particlesZX =  { origin.z, origin.x };
		positions.add(particlesZX);
		
		particlePositions.add(positions);
		
		float radiusTotal = radiusSpacing;
		float totalDegreeCircle = 360f;
		
		// number of layers is the length of the amplitudes
		for(int i = 1; i < binlength; i++) {
			positions = new ArrayList<float[]>();
			
			for(int j = 1; j <= particlesPerLayer*i; j++) {
				
				float angle = (totalDegreeCircle / (particlesPerLayer*i)) * j;
				angle = (float) (angle * Math.PI / 180F);
				
				// circle equation
				//x = cx + r * cos(a)
				//z = cz + r * sin(a)
				float x = (float) (origin.x + (radiusTotal * Math.cos(angle)));
				float z = (float) (origin.z + (radiusTotal * Math.sin(angle)));
				
				particlesZX = new float[] {z,x};
				
				positions.add(particlesZX);
			}
			particlePositions.add(positions);
			radiusTotal += radiusSpacing;
		}
		
	}
	
	/**
	 * Adds particles to arraylist based with there position data
	 */
	void addParticles() {
		ArrayList<Particle> particleLayer = new ArrayList<Particle>();
		for(int i = 0; i < particlePositions.size(); i++) {
			particleLayer = new ArrayList<Particle>();
			for(int j = 0; j < particlePositions.get(i).size(); j++) {
				float z = particlePositions.get(i).get(j)[0];
				float x = particlePositions.get(i).get(j)[1];
				
				particleLayer.add(new Particle(parent, x, origin.y, z));
			}
			
			particles.add(particleLayer);
		}

	}
	
	/**
	 * sets fft from minim
	 * @param fft
	 */
	public void setFFT(FFT fft) {
		this.fft = fft;
	}

	/**
	 * Draws particles contained in particles arraylist
	 */
	@Override
	public void display() {
		
		float max = 0.0f;
		for(int i = 0; i < fft.avgSize(); i++) {
			if(fft.getAvg(i) > max) max = fft.getAvg(i);
		}

    	parent.colorMode(PConstants.HSB,255);
		
		for(int i = 0; i < particles.size(); i++) {
			
			for(int j = 0; j < particles.get(i).size(); j++) {
				Particle particle = particles.get(i).get(j);
				particle.position(particlePositions.get(i).get(j)[1],origin.y,particlePositions.get(i).get(j)[0]);
				particle.deltaY( (fft.getAvg(i)) * 10 );
				particle.setRGB(r, 255, 255);
				particle.display();
			}
			
		}
		parent.colorMode(PConstants.RGB,255);
	}
}
