package logicsim;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.border.BevelBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

public class LSFrame extends JFrame implements ActionListener, CircuitChangedListener {

	private static final long serialVersionUID = -5281157929385660575L;

	LogicSimFile lsFile;

	JMenuBar mnuBar;
	JToolBar btnBar;

	DefaultListModel<Object> partListModel = new DefaultListModel<Object>();
	JList<Object> lstParts = new JList<Object>(partListModel);
	JComboBox<String> cbNumInputs = null;
	LSPanel lspanel = new LSPanel();

	JLabel sbText = new JLabel();
	JLabel sbCoordinates = new JLabel();

	int popupGateIdx;
	JPopupMenu popup;
	JMenuItem menuItem_remove;
	JMenuItem menuItem_properties;

	public LSFrame(String title) {
		enableEvents(AWTEvent.WINDOW_EVENT_MASK);

		lsFile = new LogicSimFile(defaultCircuitFileName());
		lspanel.setChangeListener(this);

		try {
			createUI();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * ask if we should close
	 */
	@Override
	protected void processWindowEvent(WindowEvent e) {
		if (e.getID() == WindowEvent.WINDOW_CLOSING) {
			if (showDiscardDialog(I18N.tr(Lang.EXIT)) == false)
				return;
			System.exit(0);
		}
	}

	private String defaultModuleFileName() {
		String fn = App.getModulePath();
		fn += I18N.tr(Lang.UNNAMED);
		fn += "." + App.MODULE_FILE_SUFFIX;
		return fn;
	}

	private String defaultCircuitFileName() {
		String fn = App.getCircuitPath();
		fn += I18N.tr(Lang.UNNAMED);
		fn += "." + App.CIRCUIT_FILE_SUFFIX;
		return fn;
	}

	/** Component initialization */
	private void createUI() {
		setTitle("LogicSim");

		String mode = LSProperties.getInstance().getProperty(LSProperties.MODE, LSProperties.MODE_NORMAL);

		mnuBar = new JMenuBar();

		JMenu mnu = new JMenu(I18N.tr(Lang.FILE));

		JMenuItem m = createMenuItem(Lang.NEW, KeyEvent.VK_N, false);
		m.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				actionNew(e);
			}
		});
		mnu.add(m);

		m = createMenuItem(Lang.OPEN, KeyEvent.VK_O, true);
		m.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				actionOpen(e);
			}
		});
		mnu.add(m);

		mnu.addSeparator();

		m = createMenuItem(Lang.SAVE, KeyEvent.VK_S, true);
		m.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				actionSave(e);
			}
		});
		mnu.add(m);

		m = createMenuItem(Lang.SAVEAS, 0, true);
		m.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				actionSave(e);
			}
		});
		mnu.add(m);

		mnu.addSeparator();

		m = createMenuItem(Lang.MODULECREATE, 0, true);
		m.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				actionCreateModule(e);
			}
		});
		mnu.add(m);

		m = createMenuItem(Lang.PROPERTIES, 0, true);
		m.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (FileInfoDialog.showFileInfo(LSFrame.this, lsFile)) {
					setAppTitle();
				}
			}
		});
		mnu.add(m);

		mnu.addSeparator();

		m = createMenuItem(Lang.EXPORT, 0, true);
		m.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				exportImage();
			}
		});
		mnu.add(m);

		m = createMenuItem(Lang.PRINT, 0, true);
		m.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				lspanel.doPrint();
			}
		});
		mnu.add(m);

		mnu.addSeparator();

		m = createMenuItem(Lang.EXIT, KeyEvent.VK_X, false);
		m.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (showDiscardDialog(I18N.tr(Lang.EXIT)) == false)
					return;
				System.exit(0);
			}
		});
		mnu.add(m);

		mnuBar.add(mnu);

		// ------------------------------------------------------------------
		// EDIT
		mnu = new JMenu(I18N.tr(Lang.EDIT));

		m = createMenuItem(Lang.SELECTALL, KeyEvent.VK_A, false);
		m.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				lspanel.circuit.selectAll();
				lspanel.repaint();
			}
		});
		mnu.add(m);

		m = createMenuItem(Lang.SELECT, 0, false);
		m.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				lspanel.setAction(LSPanel.ACTION_SELECT);
				lspanel.requestFocusInWindow();
			}
		});
		mnu.add(m);

		m = createMenuItem(Lang.SELECTNONE, 0, false);
		m.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				lspanel.circuit.deselectAll();
				lspanel.repaint();
			}
		});
		mnu.add(m);

		mnu.addSeparator();

		m = createMenuItem(Lang.NEWWIRE, KeyEvent.VK_W, false);
		m.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				lspanel.setAction(LSPanel.ACTION_ADDWIRE);
			}
		});
		m.setEnabled(LSProperties.MODE_EXPERT.equals(mode));
		mnu.add(m);

		mnu.addSeparator();

		m = createMenuItem(Lang.INPUTHIGH, 0, false);
		m.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				lspanel.setAction(Pin.HIGH);
				setStatusText(I18N.tr(Lang.INPUTHIGH_HELP));
			}
		});
		mnu.add(m);

		m = createMenuItem(Lang.INPUTLOW, 0, false);
		m.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				lspanel.setAction(Pin.LOW);
				setStatusText(I18N.tr(Lang.INPUTLOW_HELP));
			}
		});
		mnu.add(m);

		m = createMenuItem(Lang.INPUTINV, 0, false);
		m.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				lspanel.setAction(Pin.INVERTED);
				setStatusText(I18N.tr(Lang.INPUTINV_HELP));
			}
		});
		mnu.add(m);

		m = createMenuItem(Lang.INPUTNORM, 0, false);
		m.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				lspanel.setAction(Pin.NORMAL);
				setStatusText(I18N.tr(Lang.INPUTNORM_HELP));
			}
		});
		mnu.add(m);

		mnu.addSeparator();

		m = createMenuItem(Lang.ROTATE, 0, false);
		m.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_DOWN_MASK, false));
		m.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				lspanel.rotateSelected();
			}
		});
		mnu.add(m);

		m = createMenuItem(Lang.MIRROR, KeyEvent.VK_M, false);
		m.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				lspanel.mirrorSelected();
			}
		});
		mnu.add(m);

		mnuBar.add(mnu);
		// ------------------------------------------------------------------
		// SETTINGS
		mnu = new JMenu(I18N.tr(Lang.SETTINGS));

		boolean sel = LSProperties.getInstance().getPropertyBoolean(LSProperties.PAINTGRID, true);
		final JCheckBoxMenuItem mSettingsPaintGrid = new JCheckBoxMenuItem(I18N.tr(Lang.PAINTGRID));
		mSettingsPaintGrid.setSelected(sel);
		mSettingsPaintGrid.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				LSProperties.getInstance().setPropertyBoolean(LSProperties.PAINTGRID, mSettingsPaintGrid.isSelected());
				lspanel.repaint();
			}
		});
		mnu.add(mSettingsPaintGrid);

		boolean autowire = LSProperties.getInstance().getPropertyBoolean(LSProperties.AUTOWIRE, true);
		final JCheckBoxMenuItem cbMenuItem = new JCheckBoxMenuItem(I18N.tr(Lang.AUTOWIRE));
		cbMenuItem.setSelected(autowire);
		cbMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JCheckBoxMenuItem bmi = (JCheckBoxMenuItem) e.getSource();
				LSProperties.getInstance().setPropertyBoolean(LSProperties.AUTOWIRE, bmi.isSelected());
				lspanel.repaint();
			}
		});
		mnu.add(cbMenuItem);

		m = new JMenu(I18N.tr(Lang.GATEDESIGN));
		String gatedesign = LSProperties.getInstance().getProperty(LSProperties.GATEDESIGN,
				LSProperties.GATEDESIGN_IEC);

		JRadioButtonMenuItem mGatedesignIEC = new JRadioButtonMenuItem();
		mGatedesignIEC.setText(I18N.tr(Lang.GATEDESIGN_IEC));
		mGatedesignIEC.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				actionGateDesign(e);
			}
		});
		mGatedesignIEC.setSelected(LSProperties.GATEDESIGN_IEC.equals(gatedesign));
		m.add(mGatedesignIEC);

		JRadioButtonMenuItem mGatedesignANSI = new JRadioButtonMenuItem();
		mGatedesignANSI.setText(I18N.tr(Lang.GATEDESIGN_ANSI));
		mGatedesignANSI.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				actionGateDesign(e);
			}
		});
		mGatedesignANSI.setSelected(LSProperties.GATEDESIGN_ANSI.equals(gatedesign));
		m.add(mGatedesignANSI);

		ButtonGroup btnGroup = new ButtonGroup();
		btnGroup.add(mGatedesignIEC);
		btnGroup.add(mGatedesignANSI);

		mnu.add(m);

		JMenu mnuMode = new JMenu(I18N.tr(Lang.MODE));
		btnGroup = new ButtonGroup();

		JRadioButtonMenuItem mnuItem = new JRadioButtonMenuItem(I18N.tr(Lang.NORMAL));
		mnuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				actionMode(e);
			}
		});
		mnuItem.setSelected(LSProperties.MODE_NORMAL.equals(mode));
		btnGroup.add(mnuItem);
		mnuMode.add(mnuItem);

		mnuItem = new JRadioButtonMenuItem(I18N.tr(Lang.EXPERT));
		mnuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				actionMode(e);
			}
		});
		mnuItem.setSelected(LSProperties.MODE_EXPERT.equals(mode));
		btnGroup.add(mnuItem);
		mnuMode.add(mnuItem);

		mnu.add(mnuMode);

		JMenu mnuLang = new JMenu(I18N.tr(Lang.LANGUAGE));
		String currentLanguage = LSProperties.getInstance().getProperty(LSProperties.LANGUAGE, "de");
		createLanguageMenu(mnuLang, currentLanguage);
		mnu.add(mnuLang);

		mnu.addSeparator();

		m = createMenuItem(Lang.GATESETTINGS, 0, true);
		m.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				lspanel.gateSettings();
			}
		});
		mnu.add(m);

		mnuBar.add(mnu);

		// ------------------------------------------------------------------
		// HELP
		mnu = new JMenu(I18N.tr(Lang.HELP));

		m = createMenuItem(Lang.HELP, 0, true);
		m.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new HTMLHelp();
			}
		});
		mnu.add(m);

		m = createMenuItem(Lang.ABOUT, 0, true);
		m.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new LSFrame_AboutBox(LSFrame.this);
			}
		});
		mnu.add(m);

		mnuBar.add(mnu);

		setJMenuBar(mnuBar);

		// ------------------------------------------------------------------
		// compose GUI

		JPanel statusBar = new JPanel();
		statusBar.setLayout(new BorderLayout());
		statusBar.add(sbText, BorderLayout.WEST);
		statusBar.add(sbCoordinates, BorderLayout.EAST);
		statusBar.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		setStatusText(" ");
		// sbText.setPreferredSize(new Dimension(700, 15));
		sbCoordinates.setText(" ");
		// sbCoordinates.setPreferredSize(new Dimension(200, 20));
		add(statusBar, BorderLayout.SOUTH);

		lspanel.setPreferredSize(new Dimension(1000, 600));
		lspanel.setBackground(Color.white);
		lspanel.setDoubleBuffered(true);

		lstParts.addMouseListener(new PopupListener());
		lstParts.setCellRenderer(new GateListRenderer());
		lstParts.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				actionLstGatesSelected(e);
			}
		});

		String[] gateInputNums = new String[4];
		for (int i = 0; i < 4; i++) {
			gateInputNums[i] = (i + 2) + " " + I18N.tr(Lang.INPUTS);
		}
		cbNumInputs = new JComboBox<String>(gateInputNums);

		JPanel pnlGateList = new JPanel();
		pnlGateList.setLayout(new BorderLayout());
		pnlGateList.setPreferredSize(new Dimension(120, 200));
		pnlGateList.setMinimumSize(new Dimension(100, 200));
		pnlGateList.add(new JScrollPane(lstParts), BorderLayout.CENTER);
		pnlGateList.add(cbNumInputs, BorderLayout.SOUTH);

		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true);
		splitPane.setOneTouchExpandable(true);
		splitPane.setDividerLocation(170);
		splitPane.add(pnlGateList, JSplitPane.LEFT);
		splitPane.add(lspanel, JSplitPane.RIGHT);

		getContentPane().add(splitPane, BorderLayout.CENTER);

		btnBar = new JToolBar();

		LSButton btnLS = new LSButton("new", Lang.NEW);
		btnLS.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				actionNew(e);
			}
		});
		btnBar.add(btnLS, null);

		btnLS = new LSButton("open", Lang.OPEN);
		btnLS.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				actionOpen(e);
			}
		});
		btnBar.add(btnLS);

		btnLS = new LSButton("save", Lang.SAVE);
		btnLS.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				actionSave(e);
			}
		});
		btnBar.add(btnLS);

		btnBar.add(getMenuGap());

		LSToggleButton btnToggle = new LSToggleButton("play", Lang.SIMULATE);
		btnToggle.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				actionSimulate(e);
			}
		});
		btnBar.add(btnToggle, null);

		btnLS = new LSButton("reset", Lang.RESET);
		btnLS.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				lspanel.circuit.reset();
				lspanel.repaint();
			}
		});
		btnBar.add(btnLS, null);

		btnBar.add(getMenuGap());

		btnLS = new LSButton("zoomm", Lang.ZOOMOUT);
		btnLS.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				lspanel.zoomOut();
				lspanel.requestFocusInWindow();
			}
		});
		btnBar.add(btnLS, null);

		btnLS = new LSButton("zoomp", Lang.ZOOMIN);
		btnLS.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				lspanel.zoomIn();
				lspanel.requestFocusInWindow();
			}
		});
		btnBar.add(btnLS, null);

		btnLS = new LSButton("select", Lang.SELECT);
		btnLS.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				lspanel.setAction(LSPanel.ACTION_SELECT);
				lspanel.requestFocusInWindow();
			}
		});
		btnBar.add(btnLS, null);

		btnBar.add(getMenuGap());

		btnLS = new LSButton("rotate", Lang.ROTATE);
		btnLS.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				lspanel.rotateSelected();
				lspanel.requestFocusInWindow();
			}
		});
		btnBar.add(btnLS, null);

		btnLS = new LSButton("mirror", Lang.MIRROR);
		btnLS.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				lspanel.mirrorSelected();
				lspanel.requestFocusInWindow();
			}
		});
		btnBar.add(btnLS);

		btnBar.add(getMenuGap());

		btnLS = new LSButton("inputnorm", Lang.INPUTNORM);
		btnLS.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				lspanel.setAction(Pin.NORMAL);
				setStatusText(I18N.tr(Lang.INPUTNORM_HELP));
				lspanel.requestFocusInWindow();
			}
		});
		btnBar.add(btnLS, null);

		btnLS = new LSButton("inputinv", Lang.INPUTINV);
		btnLS.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				lspanel.setAction(Pin.INVERTED);
				setStatusText(I18N.tr(Lang.INPUTINV_HELP));
				lspanel.requestFocusInWindow();
			}
		});
		btnBar.add(btnLS, null);

		btnLS = new LSButton("inputhigh", Lang.INPUTHIGH);
		btnLS.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				lspanel.setAction(Pin.HIGH);
				setStatusText(I18N.tr(Lang.INPUTHIGH_HELP));
			}
		});
		btnBar.add(btnLS, null);

		btnLS = new LSButton("inputlow", Lang.INPUTLOW);
		btnLS.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				lspanel.setAction(Pin.LOW);
				setStatusText(I18N.tr(Lang.INPUTLOW_HELP));
				lspanel.requestFocusInWindow();
			}
		});
		btnBar.add(btnLS, null);

		btnBar.add(getMenuGap());

		btnLS = new LSButton("newwire", Lang.NEWWIRE);
		btnLS.setEnabled(getMenuWidget(Lang.NEWWIRE).isEnabled());
		btnLS.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				lspanel.setAction(LSPanel.ACTION_ADDPOINT);
				setStatusText(I18N.tr(Lang.ADDPOINT_HELP));
				lspanel.requestFocusInWindow();
			}
		});
		btnBar.add(btnLS, null);

		btnLS = new LSButton("addpoint", Lang.ADDPOINT);
		btnLS.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				lspanel.setAction(LSPanel.ACTION_ADDPOINT);
				setStatusText(I18N.tr(Lang.ADDPOINT_HELP));
				lspanel.requestFocusInWindow();
			}
		});
		btnBar.add(btnLS, null);

		btnLS = new LSButton("delpoint", Lang.REMOVEPOINT);
		btnLS.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				lspanel.setAction(LSPanel.ACTION_DELPOINT);
				setStatusText(I18N.tr(Lang.REMOVEPOINT_HELP));
				lspanel.requestFocusInWindow();
			}
		});
		btnBar.add(btnLS, null);

		add(btnBar, BorderLayout.NORTH);

		// ------------------------------------------------------------------
		// Create the popup menu.
		popup = new JPopupMenu();
		menuItem_remove = new JMenuItem(I18N.tr(Lang.REMOVEGATE));
		menuItem_remove.addActionListener(this);
		popup.add(menuItem_remove);
		menuItem_properties = new JMenuItem(I18N.tr(Lang.PROPERTIES));
		menuItem_properties.addActionListener(this);
		popup.add(menuItem_properties);
		// Add listener to components that can bring up popup menus.
		lspanel.addMouseListener(new PopupListener());

		fillGateList();
		setAppTitle();

		lspanel.requestFocusInWindow();
	}

	private JMenuItem createMenuItem(Lang lang, int key, boolean isDialog) {
		JMenuItem m = new JMenuItem(I18N.tr(lang) + (isDialog ? "..." : ""));
		if (key != 0)
			m.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK, false));
		m.setName(lang.toString());
		return m;
	}

	private void setStatusText(String string) {
		sbText.setText("  " + string);
	}

	private Component getMenuGap() {
		int is = LSProperties.getInstance().getPropertyInteger("iconsize", 48);
		return Box.createHorizontalStrut(is / 2);
	}

	/**
	 * handles popup menus
	 */
	public void actionPerformed(ActionEvent e) { // popup menu
		JMenuItem source = (JMenuItem) (e.getSource());
		if (source == menuItem_remove) {
			lspanel.circuit.removeGateIdx(popupGateIdx);
			lspanel.repaint();
		} else if (source == menuItem_properties) {
			if (popupGateIdx >= 0) {
				Gate g = lspanel.circuit.gates.get(popupGateIdx);
				g.showPropertiesUI(this);
				lspanel.repaint();
			}
		}
	}

	class PopupListener extends MouseAdapter {
		public void mousePressed(MouseEvent e) {
			maybeShowPopup(e);
		}

		public void mouseReleased(MouseEvent e) {
			maybeShowPopup(e);
		}

		private void maybeShowPopup(MouseEvent e) {
			if (e.isPopupTrigger()) {
				if (e.getSource() == lspanel) {
					for (int i = 0; i < lspanel.circuit.gates.size(); i++) {
						Gate g = lspanel.circuit.gates.get(i);
						if (g.insideFrame(e.getX(), e.getY())) {
							popupGateIdx = i;
							menuItem_properties.setEnabled(g.hasPropertiesUI());
							popup.show(e.getComponent(), e.getX(), e.getY());
							break;
						}
					}
				}
			}
		}
	}

	void actionSimulate(ActionEvent e) {
		LSToggleButton btn = (LSToggleButton) e.getSource();

		if (btn.isSelected()) {
			if (!Simulation.getInstance().isRunning()) {
				lspanel.circuit.deselectAll();
				repaint();
				Simulation.getInstance().start();
			}
		} else {
			if (Simulation.getInstance().isRunning())
				Simulation.getInstance().stop();
		}
	}

	boolean showDiscardDialog(String title) {
		if (lsFile.changed) {
			int result = Dialogs.confirmDiscardDialog();
			return (result == JOptionPane.YES_OPTION);
		}
		return true;
	}

	/**
	 * handles initial steps to create a new circuit file
	 * 
	 * @param e
	 */
	void actionNew(ActionEvent e) {
		if (showDiscardDialog(I18N.tr(Lang.NEW)) == false)
			return;
		lsFile = new LogicSimFile(defaultCircuitFileName());
		setAppTitle();
		lspanel.clear();
	}

	/**
	 * handles opening of files
	 * 
	 * @param e
	 */
	void actionOpen(ActionEvent e) {
		if (Simulation.getInstance().isRunning())
			Simulation.getInstance().stop();

		if (showDiscardDialog(I18N.tr(Lang.OPEN)) == false)
			return;

		File file = new File(lsFile.fileName);
		JFileChooser chooser = new JFileChooser(file.getParent());
		chooser.setFileFilter(setupFilter());
		if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			lsFile.fileName = chooser.getSelectedFile().getAbsolutePath();
		} else
			return;

		try {
			lsFile = XMLLoader.loadXmlFile(lsFile.fileName);
		} catch (RuntimeException x) {
			System.err.println(x);
			x.printStackTrace(System.err);
			JOptionPane.showMessageDialog(this, I18N.tr(Lang.READERROR) + " " + x.getMessage());
		}
		setAppTitle();
		lspanel.clear();
		lspanel.circuit.setGates(lsFile.getGates());
		lspanel.circuit.setWires(lsFile.getWires());
		lspanel.circuit.reset();
	}

	/**
	 * setup a file filter for displaying files who have the correct ending
	 * 
	 * @return
	 */
	private FileFilter setupFilter() {
		LogicSimFileFilter filter = new LogicSimFileFilter();
		filter.addExtension(App.CIRCUIT_FILE_SUFFIX);
		filter.addExtension(App.MODULE_FILE_SUFFIX);
		filter.setDescription(
				"LogicSim Files (" + "." + App.CIRCUIT_FILE_SUFFIX + ", " + "." + App.MODULE_FILE_SUFFIX + ")");
		return filter;
	}

	/**
	 * set window title
	 */
	private void setAppTitle() {
		String name = lsFile.extractFileName();
		name = "LogicSim - " + name;
		if (lsFile.changed)
			name += "*";
		this.setTitle(name);
	}

	/**
	 * handles saving of circuit file
	 * 
	 * @param e
	 */
	void actionSave(ActionEvent e) {
		String fileName = lsFile.fileName;
		boolean unnamed = false;
		if (lsFile.extractFileName().equals(I18N.tr(Lang.UNNAMED))) {
			unnamed = true;
		}
		boolean showDialog = fileName == null || fileName.length() == 0;
		showDialog = showDialog || unnamed;

		if (showDialog)
			if (showSaveDialog() == false)
				return;
		lsFile.circuit = lspanel.circuit;
		try {
			XMLCreator.createXML(lsFile);
		} catch (RuntimeException err) {
			System.err.println(err);
			err.printStackTrace(System.err);
			JOptionPane.showMessageDialog(this, I18N.tr(Lang.SAVEERROR) + " " + err.getMessage());
		}

		setAppTitle();
		setStatusText(String.format(I18N.tr(Lang.SAVED), lsFile.fileName));
		lsFile.changed = false;
		fillGateList();
	}

	/**
	 * helper method to show the save dialog
	 * 
	 * @return
	 */
	public boolean showSaveDialog() {
		File file = new File(lsFile.fileName);
		String parentDirName = file.getParent();

		JFileChooser chooser = new JFileChooser(parentDirName);
		chooser.setDialogTitle(I18N.tr(Lang.SAVECIRCUIT));

		String s = "LogicSim Files (" + "." + App.CIRCUIT_FILE_SUFFIX + ", " + "." + App.MODULE_FILE_SUFFIX + ")";
		FileNameExtensionFilter filter = new FileNameExtensionFilter(s, App.CIRCUIT_FILE_SUFFIX,
				App.MODULE_FILE_SUFFIX);
		chooser.setFileFilter(filter);

		if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
			lsFile.fileName = chooser.getSelectedFile().getAbsolutePath();
			// check fileName
			int lastSeparator = lsFile.fileName.lastIndexOf(File.separatorChar);
			int lastDot = lsFile.fileName.lastIndexOf(".");
			if (lastDot < lastSeparator) {
				// ending is missing
				if (lsFile.circuit.isModule())
					lsFile.fileName += "." + App.MODULE_FILE_SUFFIX;
				else
					lsFile.fileName += "." + App.CIRCUIT_FILE_SUFFIX;
			}
			return true;
		} else
			return false;
	}

	/**
	 * handles initial steps to create a new module
	 * 
	 * @param e
	 */
	void actionCreateModule(ActionEvent e) {
		lsFile = new LogicSimFile(defaultModuleFileName());

		if (!FileInfoDialog.showFileInfo(this, lsFile))
			return;

		setAppTitle();

		Gate g = new MODIN();
		g.moveTo(150, 100);
		lsFile.circuit.addGate(g);
		g = new MODOUT();
		g.moveTo(650, 100);
		lsFile.circuit.addGate(g);
		lspanel.circuit = lsFile.circuit;
		lspanel.repaint();
	}

	/**
	 * save image in file system
	 */
	void exportImage() {
		String filename = "logicsim.png";
		JFileChooser chooser = new JFileChooser();
		LogicSimFileFilter filter = new LogicSimFileFilter();
		filter.addExtension(".png");
		filter.setDescription("Portable Network Graphics");
		chooser.setFileFilter(filter);

		chooser.setDialogTitle(I18N.tr(Lang.SAVECIRCUIT));
		if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
			filename = chooser.getSelectedFile().getAbsolutePath();
			if (!filename.endsWith(".png")) {
				filename += ".png";
			}
		} else {
			return;
		}

		BufferedImage image = (BufferedImage) this.createImage(this.lspanel.getWidth(), this.lspanel.getHeight());
		Graphics g = image.getGraphics();
		lspanel.circuit.deselectAll();
		lspanel.paint(g);
		try {
			ImageIO.write(image, "png", new File(filename));
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * fill gate list
	 */
	void fillGateList() {
		partListModel.clear();
		for (Category cat : App.cats) {
			if ("hidden".equals(cat.title))
				continue;
			if (cat.getGates().size() == 0)
				continue;
			partListModel.addElement(cat.title);
			for (Gate g : cat.getGates()) {
				partListModel.addElement(g);
			}
		}
	}

	/**
	 * handles gates list
	 * 
	 * @param e
	 */
	void actionLstGatesSelected(ListSelectionEvent e) {
		int sel = lstParts.getSelectedIndex();
		if (sel < 0)
			return;
		int numInputs = Integer.parseInt(cbNumInputs.getSelectedItem().toString().substring(0, 1));

		Object o = lstParts.getSelectedValue();
		if (!(o instanceof Gate))
			return;

		Gate gate = (Gate) o;
		// gate is normal gate or module
		gate = GateLoaderHelper.create((Gate) o);
		if (gate instanceof Module) {
			Module m = (Module) gate;
			lspanel.setAction(m);
			if (m.lsFile.getDescription() != null)
				setStatusText(m.lsFile.getDescription());
			else
				setStatusText(m.type);
			lspanel.requestFocusInWindow();
		} else {
			// gate is normal Gate-Object
			if (gate.supportsVariableInputs())
				gate.createDynamicInputs(numInputs);
			lspanel.setAction(gate);

			if (gate.type.contains("test"))
				setStatusText(gate.type);
			else if (I18N.hasString(gate.type, "description")) {
				setStatusText(I18N.getString(gate.type, "description"));
			} else {
				setStatusText(I18N.getString(gate.type, "title"));
			}
			lspanel.requestFocusInWindow();
		}
		lstParts.clearSelection();
	}

	/**
	 * handles gate design (IEC/ISO)
	 * 
	 * @param e
	 */
	void actionGateDesign(ActionEvent e) {
		String gatedesign = null;
		JRadioButtonMenuItem src = (JRadioButtonMenuItem) e.getSource();
		if (src.getText().equals(I18N.tr(Lang.GATEDESIGN_IEC))) {
			if (src.isSelected())
				gatedesign = LSProperties.GATEDESIGN_IEC;
			else
				gatedesign = LSProperties.GATEDESIGN_ANSI;
		} else {
			if (src.isSelected())
				gatedesign = LSProperties.GATEDESIGN_ANSI;
			else
				gatedesign = LSProperties.GATEDESIGN_IEC;
		}
		LSProperties.getInstance().setProperty(LSProperties.GATEDESIGN, gatedesign);
		this.lspanel.repaint();
	}

	/**
	 * handles mode (normal/expert)
	 * 
	 * @param e
	 */
	void actionMode(ActionEvent e) {
		String mode = null;
		JRadioButtonMenuItem src = (JRadioButtonMenuItem) e.getSource();
		if (src.getText().equals(I18N.tr(Lang.NORMAL))) {
			if (src.isSelected())
				mode = LSProperties.MODE_NORMAL;
			else
				mode = LSProperties.MODE_EXPERT;
		} else {
			// the expert item is clicked
			if (src.isSelected()) {
				mode = LSProperties.MODE_EXPERT;
			} else {
				mode = LSProperties.MODE_NORMAL;
			}
		}
		LSProperties.getInstance().setProperty(LSProperties.MODE, mode);

		// activate widgets
		getMenuWidget(Lang.NEWWIRE).setEnabled(LSProperties.MODE_EXPERT.equals(mode));
		getButtonWidget(Lang.NEWWIRE).setEnabled(LSProperties.MODE_EXPERT.equals(mode));

		this.lspanel.repaint();
	}

	/**
	 * helper method to get a certain menu component
	 * 
	 * so we don't have to set every item as member variable
	 * 
	 * @param lang
	 * @return
	 */
	private AbstractButton getMenuWidget(Lang lang) {
		for (int i = 0; i < mnuBar.getMenuCount(); i++) {
			JMenu mnu = mnuBar.getMenu(i);
			for (Component c : mnu.getMenuComponents()) {
				if (lang.toString().equals(c.getName()))
					return (AbstractButton) c;
			}
		}
		return null;
	}

	/**
	 * helper method to get a certain button component
	 * 
	 * so we don't have to set every button as member variable
	 * 
	 * @param lang
	 * @return
	 */
	private AbstractButton getButtonWidget(Lang lang) {
		for (Component c : btnBar.getComponents()) {
			if (lang.toString().equals(c.getName()))
				return (AbstractButton) c;
		}
		return null;
	}

	/**
	 * add all languages from file system to languages menu
	 * 
	 * @param menu
	 * @param currentLanguage
	 */
	void createLanguageMenu(JMenu menu, String currentLanguage) {
		List<String> langs = I18N.getLanguages();
		ButtonGroup btnGroup = new ButtonGroup();
		for (String lang : langs) {
			JMenuItem item = new JRadioButtonMenuItem(lang);
			if (lang.equals(currentLanguage))
				item.setSelected(true);
			item.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					LSProperties.getInstance().setProperty(LSProperties.LANGUAGE,
							((JMenuItem) e.getSource()).getText());
					JOptionPane.showMessageDialog(LSFrame.this, I18N.tr(Lang.LSRESTART));
				}
			});
			btnGroup.add(item);
			menu.add(item);
		}
	}

	@Override
	public void changedCircuit() {
		if (lsFile != null) {
			lsFile.changed = true;
		}
		setAppTitle();
	}

	@Override
	public void changedStatusText(String text) {
		// this is a hack - maybe it is ok...
		if ("DESELECT_BUTTONS".contentEquals(text)) {
			for (Component c : btnBar.getComponents()) {
				if (c instanceof LSToggleButton) {
					LSToggleButton b = (LSToggleButton) c;
					b.setSelected(false);
				}
			}
			repaint();
		}
		setStatusText(text);
	}

	@Override
	public void changedZoomPos(double zoom, Point pos) {
		sbCoordinates.setText(
				"X: " + pos.x / 10 * 10 + ", Y: " + pos.y / 10 * 10 + "   Zoom: " + Math.round(zoom * 100) + "%");
	}

	@Override
	public void setAction(int action) {
	}

	@Override
	public void needsRepaint(CircuitPart circuitPart) {
	}

}
