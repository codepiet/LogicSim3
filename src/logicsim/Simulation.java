package logicsim;

import java.util.Vector;

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
	private boolean running = false;
	private static Simulation instance = null;
	private Thread thread;

	public static final int WIRE = 0;
	public static final int GATE = 1;

	Vector<LSLevelEvent> queue = new Vector<LSLevelEvent>();

	public static Simulation getInstance() {
		if (instance == null)
			instance = new Simulation();
		return instance;
	}

	private Simulation() {
		thread = new Thread(this);
		thread.setPriority(Thread.MIN_PRIORITY);
		thread.start();
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

	public synchronized void putEvent(LSLevelEvent evt) {
		queue.add(evt);
		//System.out.println(evt);
		notifyAll();
	}

	@Override
	public synchronized void run() {
		int mode = WIRE;
		while (true) {
			if (queue.size() == 0)
				try {
					wait();
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			boolean stillpresent = true;
			while (stillpresent) {
				stillpresent = false;
				for (int i = 0; i < queue.size(); i++) {
					LSLevelEvent e = queue.get(i);
					LSLevelListener l = e.target;
					if (l != null) {
						if ((mode == WIRE && (l instanceof Wire || l instanceof WirePoint))
								|| (mode == GATE && (l instanceof Pin || l instanceof Gate))) {
							l.changedLevel(e);
							stillpresent = true;
							queue.remove(i);
							i--;
						}
					}
					// try {
					// Thread.sleep(10);
					// } catch (InterruptedException ex) {
					// ex.printStackTrace();
					// }
				}
			}
			// switch mode
			mode = mode == WIRE ? GATE : WIRE;
		}

	}

}
