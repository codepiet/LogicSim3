package gates;

import java.awt.Font;
import java.awt.Graphics2D;

import logicsim.Gate;
import logicsim.I18N;
import logicsim.LSLevelEvent;
import logicsim.LSMouseEvent;
import logicsim.Pin;

/**
 * control logic
 * 
 * @author Peter Gabriel
 * @version 1.0
 */
public class CtrlLogic extends Gate {

	public static final int cNOP = 0;
	public static final int cLDA = 1;
	public static final int cADD = 2;
	public static final int cSUB = 3;
	public static final int cSTA = 4;
	public static final int cLDI = 5;
	public static final int cJMP = 6;
	public static final int cJC = 7;
	public static final int cJZ = 8;
	public static final int cOUT = 14;
	public static final int cHLT = 15;

	private static final int pINS = 0;
	private static final int pCLK = 4;
	private static final int pZF = 5;
	private static final int pCF = 6;
	private static final int pCLR = 7;
	private static final int NCLR1 = 8;
	private static final int HLT1 = 9;
	private static final int MI1 = 10;
	private static final int RI1 = 11;
	private static final int RO1 = 12;
	private static final int IO1 = 13;
	private static final int II1 = 14;
	private static final int AI1 = 15;
	private static final int AO1 = 16;
	private static final int EO1 = 17;
	private static final int SU1 = 18;
	private static final int BI1 = 19;
	private static final int OI1 = 20;
	private static final int CE1 = 21;
	private static final int CO1 = 22;
	private static final int J1 = 23;
	private static final int FI1 = 24;

	private static final int HLT = 0b1000000000000000;
	private static final int MI = 0b0100000000000000;
	private static final int RI = 0b0010000000000000;
	private static final int RO = 0b0001000000000000;
	private static final int IO = 0b0000100000000000;
	private static final int II = 0b0000010000000000;
	private static final int AI = 0b0000001000000000;
	private static final int AO = 0b0000000100000000;
	private static final int EO = 0b0000000010000000;
	private static final int SU = 0b0000000001000000;
	private static final int BI = 0b0000000000100000;
	private static final int OI = 0b0000000000010000;
	private static final int CE = 0b0000000000001000;
	private static final int CO = 0b0000000000000100;
	private static final int J = 0b0000000000000010;
	private static final int FI = 0b0000000000000001;

	private int mstep = 0;
	private int[][] mem;
	private int instruction = 0;

	public CtrlLogic() {
		super("cpu");
		type = "ctrllogic";
		height = 60;
		width = 170;

		createInputs(7);
		createOutputs(18);

		for (int i = 0; i < 9; i++) {
			getPin(i).setY(getY());
			getPin(i).paintDirection = Pin.DOWN;
		}
		for (int i = pINS; i < pINS + 4; i++) {
			getPin(i).setX(getX() + i * 10 + 10);
			getPin(i).setProperty(TEXT, String.valueOf(i + 4));
		}
		getPin(pCLK).setProperty(TEXT, "/CLK");
		getPin(pCLK).setX(getX() + 110);
		getPin(pZF).setProperty(TEXT, "ZF");
		getPin(pZF).setX(getX() + 150);
		getPin(pCF).setProperty(TEXT, "CF");
		getPin(pCF).setX(getX() + 160);
		getPin(pCLR).setProperty(TEXT, "CLR");
		getPin(pCLR).setX(getX() + 120);
		getPin(NCLR1).setProperty(TEXT, "/CLR");
		getPin(NCLR1).setLevelType(Pin.INVERTED);
		getPin(NCLR1).setX(getX() + 130);

		for (int i = 9; i < 25; i++) {
			getPin(i).setX(getX() + i * 10 - 80);
			getPin(i).setY(getY() + height);
			getPin(i).paintDirection = Pin.UP;
		}
		getPin(HLT1).setProperty(TEXT, "HLT");
		getPin(MI1).setProperty(TEXT, "MI");
		getPin(RI1).setProperty(TEXT, "RI");
		getPin(RO1).setProperty(TEXT, "RO");
		getPin(IO1).setProperty(TEXT, "IO");
		getPin(II1).setProperty(TEXT, "II");
		getPin(AI1).setProperty(TEXT, "AI");
		getPin(AO1).setProperty(TEXT, "AO");
		getPin(EO1).setProperty(TEXT, "EO");
		getPin(SU1).setProperty(TEXT, "SU");
		getPin(BI1).setProperty(TEXT, "BI");
		getPin(OI1).setProperty(TEXT, "OI");
		getPin(CE1).setProperty(TEXT, "CE");
		getPin(CO1).setProperty(TEXT, "CO");
		getPin(J1).setProperty(TEXT, "J");
		getPin(FI1).setProperty(TEXT, "FI");

		setupMemory();
	}

	private void setupMemory() {
		mem = new int[][] { { MI | CO, RO | II | CE, 0, 0, 0, 0, 0, 0 }, // 0000 - NOP
				{ MI | CO, RO | II | CE, IO | MI, RO | AI, 0, 0, 0, 0 }, // 0001 - LDA
				{ MI | CO, RO | II | CE, IO | MI, RO | BI, EO | AI | FI, 0, 0, 0 }, // 0010 - ADD
				{ MI | CO, RO | II | CE, IO | MI, RO | BI, EO | AI | SU | FI, 0, 0, 0 }, // 0011 - SUB
				{ MI | CO, RO | II | CE, IO | MI, AO | RI, 0, 0, 0, 0 }, // 0100 - STA
				{ MI | CO, RO | II | CE, IO | AI, 0, 0, 0, 0, 0 }, // 0101 - LDI
				{ MI | CO, RO | II | CE, IO | J, 0, 0, 0, 0, 0 }, // 0110 - JMP
				{ MI | CO, RO | II | CE, 0, 0, 0, 0, 0, 0 }, // 0111 - JC
				{ MI | CO, RO | II | CE, 0, 0, 0, 0, 0, 0 }, // 1000 - JZ
				{ MI | CO, RO | II | CE, 0, 0, 0, 0, 0, 0 }, // 1001
				{ MI | CO, RO | II | CE, 0, 0, 0, 0, 0, 0 }, // 1010
				{ MI | CO, RO | II | CE, 0, 0, 0, 0, 0, 0 }, // 1011
				{ MI | CO, RO | II | CE, 0, 0, 0, 0, 0, 0 }, // 1100
				{ MI | CO, RO | II | CE, 0, 0, 0, 0, 0, 0 }, // 1101
				{ MI | CO, RO | II | CE, AO | OI, 0, 0, 0, 0, 0 }, // 1110 - OUT
				{ MI | CO, RO | II | CE, HLT, 0, 0, 0, 0, 0 }, // 1111 - HLT
		};
	}

	@Override
	protected void drawLabel(Graphics2D g2, String lbl, Font font) {
		super.drawLabel(g2, "Ins: " + insToMn(instruction) + " / " + "\u03BC" + "Step: " + mstep, font);
	}

	private String insToMn(int instruction2) {
		switch (instruction) {
		case cNOP:
			return "NOP";
		case cLDA:
			return "LDA";
		case cADD:
			return "ADD";
		case cSUB:
			return "SUB";
		case cSTA:
			return "STA";
		case cLDI:
			return "LDI";
		case cJMP:
			return "JMP";
		case cJC:
			return "JC";
		case cJZ:
			return "JZ";
		case cOUT:
			return "OUT";
		default:
			return "HLT";
		}
	}

	@Override
	public void reset() {
		super.reset();
		// reset
		force = true;
		getPin(pCLR).changedLevel(new LSLevelEvent(this, HIGH, force));
		getPin(NCLR1).changedLevel(new LSLevelEvent(this, HIGH, force));
		mstep = 0;
		getPin(pCLR).changedLevel(new LSLevelEvent(this, LOW, force));
		getPin(NCLR1).changedLevel(new LSLevelEvent(this, LOW, force));
		force = false;
	}

	@Override
	public void mousePressedSim(LSMouseEvent e) {
		super.mousePressedSim(e);
		reset();
	}

	@Override
	public void interact() {
		reset();
	}
	
	@Override
	public void changedLevel(LSLevelEvent e) {
		super.changedLevel(e);

		// rising edge detection for negative clock
		if (e.source.equals(getPin(pCLK)) && e.level == HIGH) {
			instruction = 0;
			for (int i = pINS; i < pINS + 4; i++) {
				int pow = (int) Math.pow(2, i);
				if (getPin(i).getLevel() == HIGH)
					instruction += pow;
			}
			int data = mem[instruction][mstep];

			if (instruction == cJC && mstep == 2 && getPin(pCF).getLevel()) {
				// exception: JC and C-Flag
				data = IO | J;
			} else if (instruction == cJZ && mstep == 2 && getPin(pZF).getLevel()) {
				// exception: JZ and Z-Flag
				data = IO | J;
			}

			mstep = (mstep + 1) % 6;

			for (int i = HLT1; i < FI1; i++)
				getPin(i).changedLevel(new LSLevelEvent(this, LOW));
			// update outputs
			checkValue(data, HLT, HLT1);
			checkValue(data, MI, MI1);
			checkValue(data, RI, RI1);
			checkValue(data, RO, RO1);
			checkValue(data, IO, IO1);
			checkValue(data, II, II1);
			checkValue(data, AI, AI1);
			checkValue(data, AO, AO1);
			checkValue(data, EO, EO1);
			checkValue(data, SU, SU1);
			checkValue(data, BI, BI1);
			checkValue(data, OI, OI1);
			checkValue(data, CE, CE1);
			checkValue(data, CO, CO1);
			checkValue(data, J, J1);
			checkValue(data, FI, FI1);
		}
	}

	private void checkValue(int data, int check, int pinNr) {
		boolean b = (data & check) == check;
		getPin(pinNr).changedLevel(new LSLevelEvent(this, b));
	}

	@Override
	public void loadLanguage() {
		I18N.addGate(I18N.ALL, type, I18N.TITLE, "Control Logic");
		I18N.addGate(I18N.ALL, type, I18N.DESCRIPTION, "Control Logic");
	}
}