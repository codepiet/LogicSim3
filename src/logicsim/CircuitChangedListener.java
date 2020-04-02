package logicsim;

public interface CircuitChangedListener {
	public void changedCircuit();

	public void changedStatusText(String text);

	public void changedCoordinates(String text);

	public void changedActivePart(CircuitPart activePart);

	public void setAction(int action);

	public void needsRepaint(CircuitPart circuitPart);
}
