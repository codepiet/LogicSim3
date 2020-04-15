package logicsim;

public class LSLevelEvent {
	CircuitPart source;

	public boolean level;

	public LSLevelEvent(CircuitPart source) {
		this.source = source;
	}

	public LSLevelEvent(CircuitPart source, boolean level) {
		this(source);
		this.level = level;
	}

	public void setLevel(boolean level) {
		this.level = level;
	}

	@Override
	public String toString() {
		return "LevelEvt: " + source.getId() + " is " + (level ? "HIGH" : "LOW");
	}
}
