package logicsim;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;
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
import javax.swing.border.BevelBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

public class LSFrame extends JFrame implements ActionListener, CircuitChangedListener {

	private static final long serialVersionUID = -5281157929385660575L;

	LogicSimFile lsFile;

	JPopupMenu popup;
	JPopupMenu popup_list;

	JMenuItem menuItem_remove;
	JMenuItem menuItem_properties;
	JMenuItem menuItem_list_delmod;

	LSPanel lspanel = new LSPanel();
	JLabel sbText = new JLabel();
	JLabel sbCoordinates = new JLabel();

	DefaultListModel<Object> partListModel = new DefaultListModel<Object>();
	JList<Object> lstParts = new JList<Object>(partListModel);

	JRadioButtonMenuItem mGatedesignIEC = new JRadioButtonMenuItem();
	JRadioButtonMenuItem mGatedesignANSI = new JRadioButtonMenuItem();

	int popupGateIdx;
	int popupModule;

	Simulation sim;

	ButtonGroup buttongroup_language = new ButtonGroup();
	JComboBox<String> jComboBox_numinput = null;

	public LSFrame(String title) {
		super(title);
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

		JMenuBar mnuBar = new JMenuBar();

		JMenu mnuFile = new JMenu();
		mnuFile.setText(I18N.tr(Lang.FILE));

		JMenuItem mFileExit = new JMenuItem();
		mFileExit.setText(I18N.tr(Lang.EXIT));
		mFileExit.setAccelerator(javax.swing.KeyStroke.getKeyStroke(88, InputEvent.CTRL_DOWN_MASK, false));
		mFileExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (showDiscardDialog(I18N.tr(Lang.EXIT)) == false)
					return;
				System.exit(0);
			}
		});

		JMenuItem mFileCreateMode = new JMenuItem(I18N.tr(Lang.MODULECREATE));
		mFileCreateMode.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				actionCreateModule(e);
			}
		});

		JMenuItem mFileProperties = new JMenuItem(I18N.tr(Lang.PROPERTIES));
		mFileProperties.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (FileInfoDialog.showFileInfo(LSFrame.this, lsFile)) {
					setAppTitle();
				}
			}
		});

		JMenuItem mFileExportImage = new JMenuItem(I18N.tr(Lang.EXPORT));
		mFileExportImage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				exportImage();
			}
		});

		JMenuItem mFilePrint = new JMenuItem();
		mFilePrint.setText(I18N.tr(Lang.PRINT));
		mFilePrint.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				lspanel.doPrint();
			}
		});

		JMenuItem mFileNew = new JMenuItem();
		mFileNew.setText(I18N.tr(Lang.NEW));
		mFileNew.setAccelerator(javax.swing.KeyStroke.getKeyStroke(78, InputEvent.CTRL_DOWN_MASK, false));
		mFileNew.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				actionNew(e);
			}
		});

		JMenuItem mFileOpen = new JMenuItem();
		mFileOpen.setText(I18N.tr(Lang.OPEN));
		mFileOpen.setAccelerator(javax.swing.KeyStroke.getKeyStroke(79, InputEvent.CTRL_DOWN_MASK, false));
		mFileOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				actionOpen(e);
			}
		});

		JMenuItem mFileSave = new JMenuItem();
		mFileSave.setText(I18N.tr(Lang.SAVE));
		mFileSave.setAccelerator(javax.swing.KeyStroke.getKeyStroke(87, InputEvent.CTRL_DOWN_MASK, false));
		mFileSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				actionSave(e);
			}
		});

		JMenuItem mFileSaveAs = new JMenuItem();
		mFileSaveAs.setText(I18N.tr(Lang.SAVEAS));
		mFileSaveAs.setAccelerator(javax.swing.KeyStroke.getKeyStroke(83, InputEvent.CTRL_DOWN_MASK, false));
		mFileSaveAs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (showSaveDialog() == false)
					return;
				actionSave(e);
			}
		});

		mnuBar.add(mnuFile);

		mnuFile.add(mFileNew);
		mnuFile.add(mFileCreateMode);
		mnuFile.add(mFileProperties);
		mnuFile.add(mFileOpen);
		mnuFile.add(mFileSave);
		mnuFile.add(mFileSaveAs);
		mnuFile.add(mFileExportImage);
		mnuFile.add(mFilePrint);
		mnuFile.add(mFileExit);

		// ------------------------------------------------------------------
		// SETTINGS
		JMenu mnuSettings = new JMenu();
		mnuSettings.setText(I18N.tr(Lang.SETTINGS));

		boolean sel = LSProperties.getInstance().getPropertyBoolean(LSProperties.PAINTGRID, true);
		final JCheckBoxMenuItem mSettingsPaintGrid = new JCheckBoxMenuItem();
		mSettingsPaintGrid.setText(I18N.tr(Lang.PAINTGRID));
		mSettingsPaintGrid.setSelected(sel);
		mSettingsPaintGrid.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				LSProperties.getInstance().setPropertyBoolean(LSProperties.PAINTGRID, mSettingsPaintGrid.isSelected());
				lspanel.repaint();
			}
		});
		mnuSettings.add(mSettingsPaintGrid);

		boolean autowire = LSProperties.getInstance().getPropertyBoolean(LSProperties.AUTOWIRE, true);
		final JCheckBoxMenuItem mSettingsAutoWire = new JCheckBoxMenuItem();
		mSettingsAutoWire.setText(I18N.tr(Lang.AUTOWIRE));
		mSettingsAutoWire.setSelected(sel);
		mSettingsAutoWire.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				LSProperties.getInstance().setPropertyBoolean(LSProperties.AUTOWIRE, mSettingsAutoWire.isSelected());
				lspanel.repaint();
			}
		});
		mnuSettings.add(mSettingsAutoWire);

		JMenu mGatedesign = new JMenu(I18N.tr(Lang.GATEDESIGN));
		String gatedesign = LSProperties.getInstance().getProperty(LSProperties.GATEDESIGN,
				LSProperties.GATEDESIGN_IEC);

		mGatedesignIEC.setText(I18N.tr(Lang.GATEDESIGN_IEC));
		mGatedesignIEC.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				actionGateDesign(e);
			}
		});
		mGatedesignIEC.setSelected("iec".equals(gatedesign));
		mGatedesign.add(mGatedesignIEC);

		mGatedesignANSI.setText(I18N.tr(Lang.GATEDESIGN_ANSI));
		mGatedesignANSI.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				actionGateDesign(e);
			}
		});
		mGatedesignANSI.setSelected("ansi".equals(gatedesign));
		mGatedesign.add(mGatedesignANSI);

		ButtonGroup buttongroup_gatedesign = new ButtonGroup();
		buttongroup_gatedesign.add(mGatedesignIEC);
		buttongroup_gatedesign.add(mGatedesignANSI);

		mnuSettings.add(mGatedesign);

		JMenu mnuLanguage = new JMenu();
		mnuLanguage.setText(I18N.tr(Lang.LANGUAGE));
		String currentLanguage = LSProperties.getInstance().getProperty(LSProperties.LANGUAGE, "de");
		createLanguageMenu(mnuLanguage, currentLanguage);
		mnuSettings.add(mnuLanguage);

		mnuBar.add(mnuSettings);

		// ------------------------------------------------------------------
		// HELP
		JMenu mnuHelp = new JMenu();
		mnuHelp.setText(I18N.tr(Lang.HELP));

		JMenuItem mHelp = new JMenuItem();
		mHelp.setText(I18N.tr(Lang.HELP));
		mHelp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new HTMLHelp();
			}
		});

		JMenuItem mHelpAbout = new JMenuItem();
		mHelpAbout.setText(I18N.tr(Lang.ABOUT));
		mHelpAbout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new LSFrame_AboutBox(LSFrame.this);
			}
		});

		mnuHelp.add(mHelpAbout);
		mnuHelp.add(mHelp);
		mnuBar.add(mnuHelp);

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
		jComboBox_numinput = new JComboBox<String>(gateInputNums);

		JPanel pnlGateList = new JPanel();
		pnlGateList.setLayout(new BorderLayout());
		pnlGateList.setPreferredSize(new Dimension(120, 200));
		pnlGateList.setMinimumSize(new Dimension(100, 200));
		pnlGateList.add(new JScrollPane(lstParts), BorderLayout.CENTER);
		pnlGateList.add(jComboBox_numinput, BorderLayout.SOUTH);

		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true);
		splitPane.setOneTouchExpandable(true);
		splitPane.setDividerLocation(170);
		splitPane.add(pnlGateList, JSplitPane.LEFT);
		splitPane.add(lspanel, JSplitPane.RIGHT);

		getContentPane().add(splitPane, BorderLayout.CENTER);

		// ------------------------------------------------------------------
		// button bar
		JToolBar btnBar = new JToolBar();

		LSButton btnNew = new LSButton("new", Lang.NEW);
		btnNew.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				actionNew(e);
			}
		});
		btnBar.add(btnNew, null);

		LSButton btnOpen = new LSButton("open", Lang.OPEN);
		btnOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				actionOpen(e);
			}
		});
		btnBar.add(btnOpen);

		LSButton btnSave = new LSButton("save", Lang.SAVE);
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				actionSave(e);
			}
		});
		btnBar.add(btnSave);

		btnBar.add(getMenuGap());

		LSToggleButton btnSimulate = new LSToggleButton("play", Lang.SIMULATE);
		btnSimulate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				actionSimulate(e);
			}
		});
		btnBar.add(btnSimulate, null);

		LSButton btnReset = new LSButton("reset", Lang.RESET);
		btnReset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				staticReset();
				if (sim != null)
					sim.reset();
			}
		});
		btnBar.add(btnReset, null);

		btnBar.add(getMenuGap());

		LSButton btnZoomM = new LSButton("zoomm", Lang.ZOOMOUT);
		btnZoomM.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				lspanel.zoomOut();
				changedZoomPos();
				lspanel.requestFocusInWindow();
			}
		});
		btnBar.add(btnZoomM, null);

		LSButton btnZoomP = new LSButton("zoomp", Lang.ZOOMIN);
		btnZoomP.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				lspanel.zoomIn();
				changedZoomPos();
				lspanel.requestFocusInWindow();
			}
		});
		btnBar.add(btnZoomP, null);

		LSButton btnSelect = new LSButton("select", Lang.SELECT);
		btnSelect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				lspanel.setAction(LSPanel.ACTION_SELECT);
				lspanel.requestFocusInWindow();
			}
		});
		btnBar.add(btnSelect, null);

		btnBar.add(getMenuGap());

		LSButton btnRotate = new LSButton("rotate", Lang.ROTATE);
		btnRotate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				lspanel.rotateSelected();
				lspanel.requestFocusInWindow();
			}
		});
		btnBar.add(btnRotate, null);

		LSButton btnMirror = new LSButton("mirror", Lang.MIRROR);
		btnMirror.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				lspanel.mirrorSelected();
				lspanel.requestFocusInWindow();
			}
		});
		btnBar.add(btnMirror);

		btnBar.add(getMenuGap());

		LSButton btnInputNorm = new LSButton("inputnorm", Lang.INPUTNORM);
		btnInputNorm.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				lspanel.setAction(Pin.NORMAL);
				setStatusText(I18N.tr(Lang.INPUTNORM_HELP));
				lspanel.requestFocusInWindow();
			}
		});
		btnBar.add(btnInputNorm, null);

		LSButton btnInputInv = new LSButton("inputinv", Lang.INPUTINV);
		btnInputInv.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				lspanel.setAction(Pin.INVERTED);
				setStatusText(I18N.tr(Lang.INPUTINV_HELP));
				lspanel.requestFocusInWindow();
			}
		});
		btnBar.add(btnInputInv, null);

		LSButton btnInputHigh = new LSButton("inputhigh", Lang.INPUTHIGH);
		btnInputHigh.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				lspanel.setAction(Pin.HIGH);
				setStatusText(I18N.tr(Lang.INPUTHIGH_HELP));
				lspanel.requestFocusInWindow();
			}
		});
		btnBar.add(btnInputHigh, null);

		LSButton btnInputLow = new LSButton("inputlow", Lang.INPUTLOW);
		btnInputLow.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				lspanel.setAction(Pin.LOW);
				setStatusText(I18N.tr(Lang.INPUTLOW_HELP));
				lspanel.requestFocusInWindow();
			}
		});
		btnBar.add(btnInputLow, null);

		btnBar.add(getMenuGap());

		LSButton btnAddPoint = new LSButton("addpoint", Lang.ADDPOINT);
		btnAddPoint.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				lspanel.setAction(LSPanel.ACTION_ADDPOINT);
				setStatusText(I18N.tr(Lang.ADDPOINT_HELP));
				lspanel.requestFocusInWindow();
			}
		});
		btnBar.add(btnAddPoint, null);

		LSButton btnDelPoint = new LSButton("delpoint", Lang.REMOVEPOINT);
		btnDelPoint.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				lspanel.setAction(LSPanel.ACTION_DELPOINT);
				setStatusText(I18N.tr(Lang.REMOVEPOINT_HELP));
				lspanel.requestFocusInWindow();
			}
		});
		btnBar.add(btnDelPoint, null);

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

		popup_list = new JPopupMenu();
		menuItem_list_delmod = new JMenuItem(I18N.tr(Lang.DELETE));
		menuItem_list_delmod.addActionListener(this);
		popup_list.add(menuItem_list_delmod);

		fillGateList();
		setAppTitle();

		lspanel.requestFocusInWindow();
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
		} else if (source == menuItem_list_delmod) {
			String fname = App.getModulePath() + partListModel.getElementAt(popupModule) + ".mod";
			String s = I18N.tr(Lang.QUESTION_DELETE).replaceFirst("%s", fname);
			int r = JOptionPane.showConfirmDialog(this, s);
			if (r == 0) {
				File f = new File(fname);
				f.delete();
				fillGateList();
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
				} else if (e.getSource() == lstParts) {
					int idx = lstParts.locationToIndex(e.getPoint());
					// TODO vor 6 stand hier actions.length
					if (idx >= 6) {
						popupModule = idx;
						popup_list.show(e.getComponent(), e.getX(), e.getY());
					}
				}
			}
		}
	}

	void actionSimulate(ActionEvent e) {
		LSToggleButton btn = (LSToggleButton) e.getSource();
		sim = Simulation.getInstance();
		sim.setPanel(lspanel);
		if (btn.isSelected()) {
			if (!sim.isRunning()) {
				lspanel.circuit.deselectAll();
				repaint();
				sim.start();
			}
		} else {
			if (sim.isRunning())
				sim.stop();
		}
	}

	void staticReset() {
		if (sim != null && !sim.isRunning()) {
			for (int i = 0; i < lspanel.circuit.gates.size(); i++) {
				Gate g = (Gate) lspanel.circuit.gates.get(i);
				g.reset();
			}
			lspanel.repaint();
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
		if (showDiscardDialog(I18N.tr(Lang.OPEN)) == false)
			return;

		File file = new File(lsFile.fileName);
		JFileChooser chooser = new JFileChooser(file.getParent());
		chooser.setFileFilter(setupFilter());
		if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			lsFile.fileName = chooser.getSelectedFile().getAbsolutePath();
		} else
			return;

		// stop simulation and do reset
		if (sim != null) {
			sim.stop();
			// btnSimulate.setSelected(false);
		}

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
		staticReset();
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
		String s = I18N.tr(Lang.SAVED).replaceFirst("%s", lsFile.fileName);
		setStatusText(s);
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
		int numInputs = Integer.parseInt(jComboBox_numinput.getSelectedItem().toString().substring(0, 1));

		Object o = lstParts.getSelectedValue();
		if (!(o instanceof Gate))
			return;

		Gate gate = (Gate) o;
		// gate is normal gate or module
		gate = GateLoaderHelper.create((Gate) o);
		if (gate instanceof Module) {
			Module m = (Module) gate;
			lspanel.setAction(m);
			setStatusText(m.lsFile.getDescription());
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

		// TODO - this removes the selection from the list
		// it would be nicer to hold the selection until a gate is placed on lspanel
		// or any other button has been pressed
		lstParts.clearSelection();
	}

	/**
	 * handles gate design (IEC/ISO)
	 * 
	 * @param e
	 */
	void actionGateDesign(ActionEvent e) {
		String gatedesign = null;
		if (mGatedesignIEC.isSelected())
			gatedesign = "iec";
		else
			gatedesign = "ansi";
		LSProperties.getInstance().setProperty(LSProperties.GATEDESIGN, gatedesign);
		this.lspanel.repaint();
	}

	/**
	 * add all languages from file system to languages menu
	 * 
	 * @param menu
	 * @param currentLanguage
	 */
	void createLanguageMenu(JMenu menu, String currentLanguage) {
		List<String> langs = I18N.getLanguages();
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
			buttongroup_language.add(item);
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
		setStatusText(text);
	}

	@Override
	public void changedZoomPos() {
		sbCoordinates.setText("X: " + lspanel.previousPoint.x + ", Y: " + lspanel.previousPoint.y + "   Zoom: "
				+ Math.round(lspanel.scaleX * 100) + "%");
	}

	@Override
	public void setAction(int action) {
	}

	@Override
	public void needsRepaint(CircuitPart circuitPart) {
	}

}
