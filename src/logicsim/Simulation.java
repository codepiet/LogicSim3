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
public class Simulation implements Runnable {
	private Thread thread;
	private boolean running = false;
	private LSPanel lspanel;
	boolean doReset = false;
	private static Simulation instance = null;

	public static Simulation getInstance() {
		if (instance == null)
			instance = new Simulation();
		return instance;
	}

	public void setPanel(LSPanel lspanel) {
		this.lspanel = lspanel;
	}

	public void start() {
		thread = new Thread(this);
		thread.setPriority(Thread.MIN_PRIORITY);
		thread.start();
	}

	public void stop() {
		running = false;
	}

	@Override
	public void run() {
		running = true;
		while (running) {
			lspanel.circuit.simulate();

			// reset
			if (doReset) {
				for (int i = 0; i < lspanel.circuit.gates.size(); i++) {
					Gate g = (Gate) lspanel.circuit.gates.get(i);
					g.reset();
				}
				doReset = false;
			}

			lspanel.repaint();

			try {
				Thread.sleep(100);
			} catch (Exception e) {
			}
		}
	}

	public void reset() {
		doReset = true;
	}

	public boolean isRunning() {
		return running;
	}

}
