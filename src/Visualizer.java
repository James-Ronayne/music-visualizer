import processing.core.*;

import java.awt.AWTException;
import java.awt.Robot;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ddf.minim.AudioInput;
import ddf.minim.AudioPlayer;
import ddf.minim.Minim;
import ddf.minim.analysis.BeatDetect;
import ddf.minim.analysis.FFT;
import objects.AbstractVisual;
import objects.Ball;
import objects.ConcenticParticleWavegraph;
import objects.DisplayableObject;
import objects.SquareSpin;
import objects.OscilloscopeWaveform;
import objects.Stage;
import objects.Wavegraph;

/**
 * Music visualizer scene based in a room with visuals on each side of the room and a rgb floor.
 * Balls can also be generated to float around the room or bounce in time with the music.
 * Music source defaults to the default microphone, but clicking 5 loads the LonelyBoy.mp3
 * in the root directory. To test the scene if microphone has not been set up.
 * @author James Ronayne
 */
public class Visualizer extends PApplet {

	// Audio stuff
	Minim minim = new Minim(this);
	AudioInput in;
	AudioPlayer player;
	FFT fft;
	BeatDetect beat;
	
    // initialise vector of DisplayableObjects
    Map<String,DisplayableObject> objects = new HashMap<String,DisplayableObject>();
    
    // Camera
    PVector eye = new PVector();
    PVector cen = new PVector();
    
    /**
     * Must be fullscreen due to use of <em>java.awt.Robot</em>
     * to keep mouse fixed in centre of screen for first person camera
     * @see java.awt.Robot
     */
	public void settings() {
		fullScreen(P3D);
	}
	
	// global object variables
	OscilloscopeWaveform waveform;
	ConcenticParticleWavegraph wavegraph;
	SquareSpin squareSpin;
	Stage stage;
	ArrayList<Ball> balls = new ArrayList<Ball>();
	
	// Render balls or not
	boolean isBalls = false;
	// Move balls or not
	boolean isBallsMoving = true;
	// Use default microphone or load audio file
	boolean isUsingMicrophone = true;
	
	/**
	 * Sets up audio, loads textures and creates objects
	 * @see processing.core.PApplet#setup()
	 */
	public void setup() {
		
		// audio setup
		audioSetup();

		this.keyRepeatEnabled = true;
		lighting();
		
		frameRate(30);
		
		PImage[] imgs = new PImage[3];
		imgs[0] = loadImage("earthmap1k.jpg");
		imgs[1] = loadImage("BasketballColor.jpg");
		imgs[2] = loadImage("SoftballColor.jpg");
		
		//
		for (int i = 0; i < (height*0.02); i++) { 
		    int radius = (int) (random(height*0.037f,height*0.046f));
		    float vel = height*0.0115f;
		    PVector speed = new PVector(random(-vel,vel),random(-vel,vel), random(-vel,vel));
		    balls.add(new Ball(this, speed, radius, random(-height*4,height*4), random(0,-height*4), random(-height*4,height*4), imgs[(int) random(0,3)]));
		}
		
		squareSpin = new SquareSpin(this, height*2, -height, 0);
		objects.put("squareSpin", squareSpin);
		
		AbstractVisual abstractVisual = new AbstractVisual(this, -height*2, -height, 0);
		abstractVisual.orientation(0, radians(90), 0);
		objects.put("abstractVisual", abstractVisual);

		wavegraph = new ConcenticParticleWavegraph(this, new PVector(0,-height*2,0), height*2/30, fft.avgSize(), 3);
		objects.put("wavegraph", wavegraph);
		
		stage = new Stage(this);
		objects.put("stage", stage);
		
		float offset = (height * 2) / fft.avgSize();
		Wavegraph wavegraph2 = new Wavegraph(this, -height*2 + offset, 0, -height*2);
		objects.put("wavegraph2", wavegraph2);
		
		waveform = new OscilloscopeWaveform(this, -height * 2, -height*2, height*4);
		objects.put("waveform", waveform);
		
	    cen.set(width/2,height/3,0.f);

	    reshape();

		surface.setResizable(true);
	}

	/**
	 * Switches between default microphone being used as audio source
	 * or the LonelyBoy.mp3 contained in the root directory
	 */
	public void audioSetup() {
		if(isUsingMicrophone) {
			if(player != null) player.pause();
			in = minim.getLineIn();
			fft = new FFT(in.bufferSize(),in.sampleRate());
			fft.logAverages(11, 3); // 33 bands i think\
			beat = new BeatDetect(in.bufferSize(),in.sampleRate());
			beat.detectMode(BeatDetect.FREQ_ENERGY);
			beat.setSensitivity(300);

    		isUsingMicrophone = false;
    	} else {
    		player = minim.loadFile("LonelyBoy.mp3");
    		player.play();
    		fft = new FFT(player.bufferSize(),player.sampleRate());
			fft.logAverages(11, 3); // 33 bands i think\
			beat = new BeatDetect(player.bufferSize(),player.sampleRate());
			beat.detectMode(BeatDetect.FREQ_ENERGY);
			beat.setSensitivity(300);
			
    		isUsingMicrophone = true;
    	}
	}
	
	/*
	 * 
	 */
	private void reshape(){
	    objects.get("stage").size(height*2);                          // Resize stage based on camera radius

	  perspective();  // perspective
	}
	
	float x=0, y=0, z=0;
	
	/*
	 * setup lighting
	 */
	void lighting() {
		// Set lighting effect colours and directional parameter
	    float ambience[] = {51f, 51f, 51f};          // ambient light (20% white)
	    float diffuse[] = {128.f, 128.f, 128.f};        // diffuse light (50% white)
	    float direction[] = {-1.f, 0.5f, -1.f, 0.f};    // direction of light
	    
	    ambientLight(ambience[0], ambience[1], ambience[2]);    // set ambient lighting in the scene
	    directionalLight(diffuse[0],   diffuse[1],   diffuse[2],    // create directional diffuse lighting
	                     direction[0], direction[1], direction[2]);
	}
	
	/**
	 * Main draw loop
	 */
	public void draw() {

		positionCamera();                           // set camera position before anything else
		noCursor();
		mouseMove(width,height);
		
		
		// Detect kick
		if(!isUsingMicrophone) {
			beat.detect(in.mix);
			// update waveform data
			waveform.setData(in.mix.toArray());
			// perform fft
			fft.forward(in.mix);
		}
		else {
			beat.detect(player.mix);
			// update waveform data
			waveform.setData(player.mix.toArray());
			// perform fft
			fft.forward(player.mix);
		}
		
		// detect snare
		if (beat.isKick()) {
			for(DisplayableObject obj : objects.values()) {
				obj.newSeed();
			}
			squareSpin.newSquare();
		}
		if (beat.isSnare()) {
			for(Ball ball : balls) {
				ball.beat();
			}
			squareSpin.newSquare();
		}
		
		
		background(0);               // black background
	    noFill();
	    
	    lighting();

		translate((width/2),height/2,0);
		
		float[] rgb = wavegraph.getRGB();
		
		// Adds light at top of stage that minics colour of the concentric wavegraph
    	colorMode(PConstants.HSB,255);
		pointLight(rgb[0],rgb[1],rgb[2],-height,-height*2,-height);
    	colorMode(PConstants.RGB,255);
		
		// scaling
		objects.get("stage").size(height*2);
		((ConcenticParticleWavegraph) objects.get("wavegraph")).scaleToResolution(new PVector(0,-height*2,0), height*2/25);;

		if(isBalls) {
			for (int i = 0; i < balls.size() -1; i++) { 
			    Ball thisBall = (Ball) balls.get(i);
			    if(isBallsMoving) thisBall.move();
			    thisBall.display();
			    collideBalls();
			}
		}
		
		// for each DisplayableObject (in this case, one Human)
		for(DisplayableObject obj : objects.values()) {
			
			if(obj instanceof OscilloscopeWaveform) {
				((OscilloscopeWaveform) obj).position(-height*2, -height*2, height*4);
			}
			
			if(obj instanceof AbstractVisual) {
				((AbstractVisual) obj).setData(in.mix.toArray());
			}
			
			if(obj instanceof ConcenticParticleWavegraph) {
				((ConcenticParticleWavegraph) obj).setFFT(fft);
			} else if(obj instanceof Wavegraph) {
				((Wavegraph) obj).setFFT(fft);
				
				// scaling
				float offset = (height * 2) / fft.avgSize();
				((Wavegraph) obj).position( -height*2 + offset, 0, -height*2);
			}
			
		    obj.display();             // call display method on DisplayableObject
		}

		flush();
	}
	
	/**
	 * Positions camera initially
	 */
	private void positionCamera() {	
	    camera(eye.x, eye.y, eye.z,                 // eye position 
	           cen.x, cen.y, cen.z,                 // point that looking at (origin)
	           0.0f, 1.0f, 0.0f);                   // up vector (0, 1, 0)
	}
	
	private static float CAMERASPEED = 0.2f;
	
	/**
	 * Handles key presses
	 */
	@Override
	public void keyPressed() {
	    if (key == CODED){                      // if non-ASCII key pressed
	        if (keyCode == LEFT){               // left arrow
	        	// rotates camera left
	        	rotateView(-CAMERASPEED);
	        } else if (keyCode == RIGHT){       // right arrow
	        	// rotates camera right
	        	rotateView(CAMERASPEED);
	        } else if (keyCode == UP) {			// up arrow
	        	// moves camera forward
	        	moveCamera(CAMERASPEED);
	        } else if(keyCode == DOWN) {		// down arrow
	        	// moves camera back
	        	moveCamera(-CAMERASPEED);
	        }
	    } else if (key == 'w') {				// w key
        	// moves camera forward
        	moveCamera(CAMERASPEED);
        } else if (key == 's') {				// s key
        	// moves camera back
        	moveCamera(-CAMERASPEED);
        } else if (key == 'a') {				// a key
        	// rotates camera left
        	rotateView(-CAMERASPEED);
        } else if (key == 'd') {				// s key
        	// rotates camera right
        	rotateView(CAMERASPEED);		
        } else if (key == 'q') {				// q key
        	// moves camera up
        	eye.y += 40f;
        	cen.y += 40f;
        } else if (key == 'e') {				// e key
        	// moves camera down
        	eye.y -= 40f;
        	cen.y -= 40f;
        } else if (key == '1') {				// 1 key
        	// turns balls displaying on or off
        	if(isBalls) isBalls = false;
        	else isBalls = true;
        } else if (key == '2') {				// 2 key
        	// Turns gravity on or off for balls
        	for(Ball ball : balls) ball.gravity();
        } else if (key == '3') {				// 3 key
        	// Turns beat detection on or off for balls
        	for(Ball ball : balls) ball.beatDetection();
        } else if (key == '4') {				// 4 key
        	// Turns on or off whether balls move or not
        	if(isBallsMoving) isBallsMoving = false;
        	else isBallsMoving = true;
        } else if (key == '5') {				// 5 key
        	// Switches between default microphone being used as audio source
        	// or the LonelyBoy.mp3 contained in the root directory
    		audioSetup();
        }
	}
	
	/**
	 * Moves forqards or backwards in xz direction camera is facing
	 * @param speed to move in pixels
	 */
	private void moveCamera(float speed) {
		PVector vec = PVector.sub(cen, eye);	// Get the view vector

		// forward positive cameraspeed and backward negative -cameraspeed.
		eye.x  = eye.x + vec.x * speed;
		eye.z  = eye.z + vec.z * speed;
		cen.x = cen.x + vec.x * speed;
		cen.z = cen.z + vec.z * speed;
	}
	
	/**
	 * Rotates cen around eye in the xz direction
	 * @param speed to rotate in pixels
	 */
	private void rotateView(float speed) {
		PVector vec = PVector.sub(cen, eye);	// Get the view vector

		cen.z = (float)(eye.z + sin(speed)*vec.x + cos(speed)*vec.z);
		cen.x = (float)(eye.x + cos(speed)*vec.x - sin(speed)*vec.z);
	}
	
	/**
	 * Allows mouse to control direction camera faces by moving cen around eye
	 * in a 3d space
	 * @param wndWidth width of screen in pixels
	 * @param wndHeight height of screen in pixels
	 */
	private void mouseMove(int wndWidth, int wndHeight) {
		try {
			Robot r = new Robot();
			
			int mid_x = displayWidth /2;
			int mid_y = displayHeight /2;
			float angle_y  = 0.0f;
			float angle_z  = 0.0f;

			if( (mouseX == mid_x) && (mouseY == mid_y) ) return;

			r.mouseMove(mid_x, mid_y);	

			// Get the direction from the mouse cursor, set a resonable maneuvering speed
			angle_y = (float)( (mid_x - mouseX) ) / 1000;		
			angle_z = (float)( (mid_y - mouseY) ) / 1000;

			// The higher the value is the faster the camera looks around.
			cen.y -= angle_z * 1000;

			rotateView(-angle_y); // Rotate
			
		} catch (AWTException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Handles collision of balls  
	 */
	public void collideBalls() {
	    for (int i = 0; i < balls.size() - 1; i++) {
	    	Ball ballA = balls.get(i);
	    	for (int j = i + 1; j < balls.size(); j++) {
	    		Ball ballB = balls.get(j);
	    		if (!ballA.equals(ballB) && ballA.position().dist(ballB.position()) < (ballA.radius + ballB.radius)) {
	    			bounce(ballA,ballB);
	    		}
	    	}
	    }
	}
	
	/**
	 * Calculates resulting PVectors for speed of two balls which are colliding 
	 * @param ballA
	 * @param ballB
	 */
	void bounce(Ball ballA, Ball ballB) {
	    PVector ab = new PVector();
	    ab.set(ballA.position());
	    ab.sub(ballB.position());
	    ab.normalize();
	    while(ballA.position().dist(ballB.position()) < (ballA.radius + ballB.radius)) { 
	    	ballA.position().add(ab);
	    }
	    PVector n = PVector.sub(ballA.position(), ballB.position());
	    n.normalize();
	    PVector u = PVector.sub(ballA.speed, ballB.speed);
	    PVector un = componentVector(u,n);
	    u.sub(un);
	    ballA.speed = PVector.add(u, ballB.speed);
	    ballB.speed = PVector.add(un, ballB.speed);
	}
	 
	PVector componentVector (PVector vector, PVector directionVector) {
	    directionVector.normalize();
	    directionVector.mult(vector.dot(directionVector));
	    return directionVector;
	}
	
	/*
	 * Main function which runs sketch
	 */
	public static void main(String args[]) {
		String[] a = {"MAIN"};
		PApplet.runSketch(a, new Visualizer());
	}
}
