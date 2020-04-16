package logicsim;

public class LSLevelEvent {
	public CircuitPart source;

	public boolean level;

	public boolean force;

	public LSLevelEvent(CircuitPart source) {
		this.source = source;
		this.level = false;
		this.force = false;
	}

	public LSLevelEvent(CircuitPart source, boolean level) {
		this(source);
		this.level = level;
		this.force = false;
	}

	public LSLevelEvent(CircuitPart source, boolean level, boolean force) {
		this(source, level);
		this.force = force;
	}

	public void setLevel(boolean level) {
		this.level = level;
	}

	@Override
	public String toString() {
		return "LevelEvt: " + source.getId() + " is " + (level ? "HIGH" : "LOW") + " force: " + force;
	}
}
