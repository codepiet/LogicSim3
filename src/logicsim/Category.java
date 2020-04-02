package logicsim;

import java.util.ArrayList;

public class Category {
	String title;
	ArrayList<Gate> gates = new ArrayList<Gate>();

	public Category(String title) {
		this.title = title;
	}

	public String getTitle() {
		return title;
	}

	public ArrayList<Gate> getGates() {
		return gates;
	}

	public void addGate(Gate g) {
		gates.add(g);
	}

	@Override
	public String toString() {
		String s = "[Category: " + title + "/#gates: " + gates.size();
		s += "]";
		return s;
	}
}
