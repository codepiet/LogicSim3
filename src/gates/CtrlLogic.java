package gates;

import logicsim.Gate;
import logicsim.I18N;
import logicsim.LSLevelEvent;
import logicsim.Pin;

/**
 * control logic
 * 
 * @author Peter Gabriel
 * @version 1.0
 */
public class CtrlLogic extends Gate {

	private static final int INS1 = 0;
	private static final int CLK1 = 4;
	private static final int ZF1 = 5;
	private static final int CF1 = 6;
	private static final int CLR1 = 7;
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

	private static final long HLT = 0b1000000000000000;
	private static final long MI = 0b0100000000000000;
	private static final long RI = 0b0010000000000000;
	private static final long RO = 0b0001000000000000;
	private static final long IO = 0b0000100000000000;
	private static final long II = 0b0000010000000000;
	private static final long AI = 0b0000001000000000;
	private static final long AO = 0b0000000100000000;
	private static final long EO = 0b0000000010000000;
	private static final long SU = 0b0000000001000000;
	private static final long BI = 0b0000000000100000;
	private static final long OI = 0b0000000000010000;
	private static final long CE = 0b0000000000001000;
	private static final long CO = 0b0000000000000100;
	private static final long J = 0b0000000000000010;
	private static final long FI = 0b0000000000000001;

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
		for (int i = 0; i < 4; i++) {
			getPin(i).setX(getX() + i * 10 + 10);
			getPin(i).setProperty(TEXT, String.valueOf(i + 4));
		}
		getPin(CLK1).setProperty(TEXT, "/CLK");
		getPin(CLK1).setX(getX() + 110);
		getPin(ZF1).setProperty(TEXT, "ZF");
		getPin(ZF1).setX(getX() + 150);
		getPin(CF1).setProperty(TEXT, "CF");
		getPin(CF1).setX(getX() + 160);
		getPin(CLR1).setProperty(TEXT, "CLR");
		getPin(CLR1).setX(getX() + 120);
		getPin(NCLR1).setProperty(TEXT, "/CLR");
		getPin(NCLR1).setX(getX() + 130);

		for (int i = 9; i < 25; i++) {
			getPin(i).setX(getX() + i * 10 - 80);
			getPin(i).setY(getY() + height);
			getPin(i).paintDirection = Pin.UP;
		}
		getPin(HLT1).setProperty(TEXT, "HLT");
		getPin(MI1).setProperty(TEXT, "/MI");
		getPin(RI1).setProperty(TEXT, "RI");
		getPin(RO1).setProperty(TEXT, "/RO");
		getPin(IO1).setProperty(TEXT, "/IO");
		getPin(II1).setProperty(TEXT, "/II");
		getPin(AI1).setProperty(TEXT, "/AI");
		getPin(AO1).setProperty(TEXT, "/AO");
		getPin(EO1).setProperty(TEXT, "/EO");
		getPin(SU1).setProperty(TEXT, "/SU");
		getPin(BI1).setProperty(TEXT, "/BI");
		getPin(OI1).setProperty(TEXT, "OI");
		getPin(CE1).setProperty(TEXT, "CE");
		getPin(CO1).setProperty(TEXT, "/CO");
		getPin(J1).setProperty(TEXT, "/J");
		getPin(FI1).setProperty(TEXT, "/FI");
	}

	@Override
	public void changedLevel(LSLevelEvent e) {
		super.changedLevel(e);

		// edge detection for clock
		if (e.source.equals(getPin(CLK1)) && e.level == LOW) {

		}
	}

	@Override
	public void loadLanguage() {
		I18N.addGate(I18N.ALL, type, I18N.TITLE, "Control Logic");
		I18N.addGate(I18N.ALL, type, I18N.DESCRIPTION, "Control Logic");
	}
}