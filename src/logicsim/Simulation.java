package logicsim;

/**
 * Simulation of circuits
 * 
 * uses singleton pattern. gates can see if the simulation is running
 * 
 * @author Andreas Tetzl
 * @author Peter Gabriel
 * @version 2.0
 */
public class Simulation {
	private boolean running = false;
	private static Simulation instance = null;

	public static Simulation getInstance() {
		if (instance == null)
			instance = new Simulation();
		return instance;
	}

	public void start() {
		running = true;
	}

	public void stop() {
		running = false;
	}

	public boolean isRunning() {
		return running;
	}

}
