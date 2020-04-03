package logicsim;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
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
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.border.BevelBorder;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

public class LSFrame extends JFrame implements java.awt.event.ActionListener, CircuitChangedListener {

	private static final long serialVersionUID = -5281157929385660575L;

	LogicSimFile lsFile;

	JPopupMenu popup;
	JPopupMenu popup_list;
	JMenuItem menuItem_remove, menuItem_properties;
	JMenuItem menuItem_list_delmod;

	JPanel contentPane;
	JMenuBar mnuBar = new JMenuBar();
	JMenu mnuFile = new JMenu();
	JMenuItem mFileExit = new JMenuItem();
	JMenu mnuHelp = new JMenu();
	JMenuItem mHelpAbout = new JMenuItem();
	JPanel statusBar = new JPanel();
	JLabel sbText = new JLabel();
	JLabel sbCoordinates = new JLabel();
	BorderLayout borderLayout1 = new BorderLayout();
	JPanel jPanel1 = new JPanel();
	JPanel pnlGateList = new JPanel();

	DefaultListModel<Object> jList_gates_model = new DefaultListModel<Object>();
	JList<Object> lstGates = new JList<Object>(jList_gates_model);

	LSPanel lspanel = new LSPanel();

	JScrollPane jScrollPane_lspanel = new JScrollPane(lspanel);
	JSplitPane jSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true);

	int popupGateIdx; // das Gatter �ber dem das Kontext-Menu ge�ffnet wurde
	int popupModule; // die Nummer des Listeneintrags, �ber dem das KM ge�ffnet wurde
	JScrollPane jScrollPane_gates = new JScrollPane();

	Simulation sim;

	JFrame window;

	// button bar
	JButton btnOpen = new JButton();
	JButton btnNew = new JButton();
	JButton btnSave = new JButton();
	JButton btnAddPoint = new JButton();
	JButton btnDelPoint = new JButton();
	JToggleButton btnSimulate = new JToggleButton();
	JButton btnReset = new JButton();
	JToolBar btnBar = new JToolBar();

	JMenuItem mFileCreateMode = new JMenuItem();
	JMenuItem mFileProperties = new JMenuItem();

	JMenuItem mFileExportImage = new JMenuItem();
	JMenuItem mFilePrint = new JMenuItem();
	BorderLayout borderLayout2 = new BorderLayout();
	JPanel jPanel2 = new JPanel();
	JComboBox<String> jComboBox_numinput = null;
	BorderLayout borderLayout3 = new BorderLayout();

	JMenuItem mFileNew = new JMenuItem();
	JMenuItem mFileOpen = new JMenuItem();
	JMenuItem mFileSave = new JMenuItem();
	JMenuItem mFileSaveAs = new JMenuItem();

	JMenuItem mHelp = new JMenuItem();

	Component component1;
	Component component2;
	JMenu mnuSettings = new JMenu();
	JCheckBoxMenuItem mSettingsPaintGrid = new JCheckBoxMenuItem();
	JMenu jMenu_gatedesign = new JMenu();
	ButtonGroup buttongroup_gatedesign = new ButtonGroup();
	JRadioButtonMenuItem jMenuItem_gatedesign_din = new JRadioButtonMenuItem();
	JRadioButtonMenuItem jMenuItem_gatedesign_iso = new JRadioButtonMenuItem();
	JMenu jMenu_language = new JMenu();
	ButtonGroup buttongroup_language = new ButtonGroup();

	public static String gatedesign = "din"; // globale statische Variable auf die auch von Gate aus zugegriffen werden
												// kann
	Properties userProperties = new Properties();
	String use_language;

	public LSFrame() {
		super();
//		String iconloc = "images/icon.png";
//		URL iconURL = getClass().getResource(iconloc);
//		Toolkit kit = Toolkit.getDefaultToolkit();
//		Image img = kit.createImage(iconURL);
//		window.setIconImage(img);

		enableEvents(AWTEvent.WINDOW_EVENT_MASK);

		lsFile = new LogicSimFile(defaultCircuitFileName());
		lspanel.setChangeListener(this);
		try {
			jbInit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Overridden so we can exit when window is closed
	 */
	@Override
	protected void processWindowEvent(WindowEvent e) {
		if (e.getID() == WindowEvent.WINDOW_CLOSING) {
			if (showDiscardDialog(I18N.getString(Lang.MNU_EXIT)) == false)
				return;
			System.exit(0);
		}
	}

	private String defaultModuleFileName() {
		String fn = App.getModulePath();
		fn += I18N.getString(Lang.FILE_UNNAMED);
		fn += "." + App.MODULE_FILE_SUFFIX;
		return fn;
	}

	private String defaultCircuitFileName() {
		String fn = App.getCircuitPath();
		fn += I18N.getString(Lang.FILE_UNNAMED);
		fn += "." + App.CIRCUIT_FILE_SUFFIX;
		return fn;
	}

	/** Component initialization */
	private void jbInit() throws Exception {
		contentPane = (JPanel) this.getContentPane();
		component1 = Box.createHorizontalStrut(8);
		component2 = Box.createHorizontalStrut(8);
		contentPane.setLayout(borderLayout1);
		if (window != null) {
			// window.setSize(new Dimension(1024, 768));
			window.setTitle("LogicSim");
		}
		statusBar.add(sbText, BorderLayout.WEST);
		statusBar.add(sbCoordinates, BorderLayout.EAST);
		statusBar.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		sbText.setText(" ");
		sbText.setPreferredSize(new Dimension(700, 20));
		sbCoordinates.setText(" ");
		sbCoordinates.setPreferredSize(new Dimension(200, 20));
		mnuFile.setText(I18N.getString(Lang.MNU_FILE));
		mFileExit.setText(I18N.getString(Lang.MNU_EXIT));

		mFileExit.setAccelerator(
				javax.swing.KeyStroke.getKeyStroke(88, java.awt.event.InputEvent.CTRL_DOWN_MASK, false));
		mFileExit.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jMenuFileExit_actionPerformed(e);
			}
		});
		mnuHelp.setText(I18N.getString(Lang.MNU_HELP));
		mHelpAbout.setText(I18N.getString(Lang.MNU_ABOUT));
		mHelpAbout.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jMenuHelpAbout_actionPerformed(e);
			}
		});
		jScrollPane_lspanel.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		jScrollPane_lspanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		jPanel1.setLayout(borderLayout3);
		btnOpen.setIcon(new ImageIcon(logicsim.LSFrame.class.getResource("images/open.gif")));
		btnOpen.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jButton_open_actionPerformed(e);
			}
		});
		btnOpen.setToolTipText(I18N.getString(Lang.MNU_OPEN));
		btnNew.setIcon(new ImageIcon(logicsim.LSFrame.class.getResource("images/new.gif")));
		btnNew.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jButton_new_actionPerformed(e);
			}
		});
		btnSave.setIcon(new ImageIcon(logicsim.LSFrame.class.getResource("images/save.gif")));
		btnSave.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jButton_save_actionPerformed(e);
			}
		});
		btnSave.setToolTipText(I18N.getString(Lang.MNU_SAVE));
		btnSimulate.setText(I18N.getString(Lang.BTN_SIMULATE));
		btnSimulate.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jToggleButton_simulate_actionPerformed(e);
			}
		});
		btnReset.setText(I18N.getString(Lang.BTN_RESET));
		btnReset.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jButton_reset_actionPerformed(e);
			}
		});
		mFileCreateMode.setText(I18N.getString(Lang.MNU_CREATEMODULE));
		mFileCreateMode.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jMenuItem_createmod_actionPerformed(e);
			}
		});
		mFileProperties.setText(I18N.getString(Lang.MNU_FILE_PROPERTIES));
		mFileProperties.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jMenuItem_modproperties_actionPerformed(e);
			}
		});

		mFileExportImage.setText(I18N.getString(Lang.MNU_EXPORT));
		mFileExportImage.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				exportImage();
			}
		});
		mFilePrint.setText(I18N.getString(Lang.MNU_PRINT));
		mFilePrint.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jMenuItem_print_actionPerformed(e);
			}
		});

		String[] gateInputNums = new String[4];
		for (int i = 0; i < 4; i++) {
			gateInputNums[i] = (i + 2) + " " + I18N.getString(Lang.MSG_INPUTS);
		}
		jComboBox_numinput = new JComboBox<String>(gateInputNums);

		jSplitPane.setOneTouchExpandable(true);
		// jSplitPane.setDividerLocation(120);
		// ** DM 26.12.2008 ** //
		jSplitPane.setDividerLocation(170);

		pnlGateList.setLayout(borderLayout2);
		lstGates.addMouseListener(new LSFrame_jList_gates_mouseAdapter(this));
		pnlGateList.setPreferredSize(new Dimension(120, 200));
		pnlGateList.setMinimumSize(new Dimension(80, 200));

		mFileNew.setText(I18N.getString(Lang.MNU_NEW));
		mFileNew.setAccelerator(
				javax.swing.KeyStroke.getKeyStroke(78, java.awt.event.InputEvent.CTRL_DOWN_MASK, false));
		mFileNew.addActionListener(new LSFrame_jMenuItem_new_actionAdapter(this));
		mFileOpen.setText(I18N.getString(Lang.MNU_OPEN));
		mFileOpen.setAccelerator(
				javax.swing.KeyStroke.getKeyStroke(79, java.awt.event.InputEvent.CTRL_DOWN_MASK, false));
		mFileOpen.addActionListener(new LSFrame_jMenuItem_open_actionAdapter(this));
		mFileSave.setText(I18N.getString(Lang.MNU_SAVE));
		mFileSave.setAccelerator(
				javax.swing.KeyStroke.getKeyStroke(87, java.awt.event.InputEvent.CTRL_DOWN_MASK, false));
		mFileSave.addActionListener(new LSFrame_jMenuItem_save_actionAdapter(this));
		mFileSaveAs.setText(I18N.getString(Lang.MNU_SAVEAS));
		mFileSaveAs.setAccelerator(
				javax.swing.KeyStroke.getKeyStroke(83, java.awt.event.InputEvent.CTRL_DOWN_MASK, false));
		mFileSaveAs.addActionListener(new LSFrame_jMenuItem_saveas_actionAdapter(this));

		mHelp.setText(I18N.getString(Lang.MNU_HELP));
		mHelp.addActionListener(new LSFrame_jMenuItem_help_actionAdapter(this));
		btnDelPoint.setToolTipText(I18N.getString(Lang.BTN_REMOVEPOINT));
		btnDelPoint.setIcon(new ImageIcon(logicsim.LSFrame.class.getResource("images/delpoint.gif")));
		btnDelPoint.addActionListener(new LSFrame_jButton_delpoint_actionAdapter(this));
		btnAddPoint.setToolTipText(I18N.getString(Lang.MSG_ADDPOINT));
		btnAddPoint.setIcon(new ImageIcon(logicsim.LSFrame.class.getResource("images/addpoint.gif")));
		btnAddPoint.addActionListener(new LSFrame_jButton_addpoint_actionAdapter(this));

		mnuSettings.setText(I18N.getString(Lang.MNU_SETTINGS));
		mSettingsPaintGrid.setText(I18N.getString(Lang.MNU_PAINTGRID));
		mSettingsPaintGrid.setSelected(true);
		mSettingsPaintGrid.addActionListener(new LSFrame_jCheckBoxMenuItem_paintGrid_actionAdapter(this));

		mnuFile.add(mFileNew);
		mnuFile.add(mFileCreateMode);
		mnuFile.add(mFileProperties);
		mnuFile.add(mFileOpen);
		mnuFile.add(mFileSave);
		mnuFile.add(mFileSaveAs);
		mnuFile.add(mFileExportImage);
		mnuFile.add(mFilePrint);
		mnuFile.add(mFileExit);

		mnuHelp.add(mHelpAbout);
		mnuHelp.add(mHelp);

		mnuBar.add(mnuFile);
		mnuBar.add(mnuSettings);
		mnuBar.add(mnuHelp);

		this.setJMenuBar(mnuBar);
		contentPane.add(statusBar, BorderLayout.SOUTH);

		lspanel.setBackground(Color.white);
		lspanel.setDoubleBuffered(true);

		pnlGateList.add(jScrollPane_gates, BorderLayout.CENTER);
		pnlGateList.add(jComboBox_numinput, BorderLayout.SOUTH);

		jSplitPane.add(pnlGateList, JSplitPane.LEFT);
		jSplitPane.add(jScrollPane_lspanel, JSplitPane.RIGHT);
		contentPane.add(jSplitPane, BorderLayout.CENTER);
		jScrollPane_gates.getViewport().add(lstGates, null);
		btnBar.add(btnNew, null);
		btnBar.add(btnOpen);
		btnBar.add(btnSave);
		btnBar.add(component1, null);
		btnBar.add(btnAddPoint, null);
		btnBar.add(btnDelPoint, null);
		btnBar.add(component2, null);
		btnBar.add(btnSimulate, null);
		btnBar.add(btnReset, null);
		jPanel1.add(btnBar, BorderLayout.CENTER);
		contentPane.add(jPanel1, BorderLayout.NORTH);

		jMenu_gatedesign.setText(I18N.getString(Lang.MNU_GATEDESIGN));
		buttongroup_gatedesign.add(jMenuItem_gatedesign_din);
		buttongroup_gatedesign.add(jMenuItem_gatedesign_iso);
		jMenuItem_gatedesign_din.setText(I18N.getString(Lang.MNU_GATEDESIGN_DIN));
		jMenuItem_gatedesign_iso.setText(I18N.getString(Lang.MNU_GATEDESIGN_ISO));
		jMenu_gatedesign.add(jMenuItem_gatedesign_din);
		jMenu_gatedesign.add(jMenuItem_gatedesign_iso);
		jMenuItem_gatedesign_din.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jMenuItem_gatedesign_actionPerformed(e);
			}
		});
		jMenuItem_gatedesign_iso.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jMenuItem_gatedesign_actionPerformed(e);
			}
		});

		// Properties laden und Men�punkte entsprechend setzen
		boolean paintgrid = true;
		jMenuItem_gatedesign_din.setSelected(true);
		use_language = "en";
		try {
			userProperties.load(new FileInputStream("logicsim.cfg"));

			if (userProperties.containsKey("paint_grid"))
				paintgrid = userProperties.getProperty("paint_grid").equals("true");
			String s = userProperties.getProperty("gatedesign");
			if (s != null && s.equals("iso")) {
				jMenuItem_gatedesign_iso.setSelected(true);
				LSFrame.gatedesign = "iso";
			}
			if (userProperties.containsKey("language"))
				use_language = userProperties.getProperty("language");
		} catch (Exception ex) {
		}

		jMenu_language.setText(I18N.getString(Lang.MNU_LANGUAGE));
		create_language_menu(jMenu_language, use_language);

		mSettingsPaintGrid.setSelected(paintgrid);
		lspanel.setPaintGrid(paintgrid);
		mnuSettings.add(mSettingsPaintGrid);
		mnuSettings.add(jMenu_gatedesign);
		mnuSettings.add(jMenu_language);

		// Create the popup menu.
		popup = new JPopupMenu();
		menuItem_remove = new JMenuItem(I18N.getString(Lang.MNU_REMOVEGATE));
		menuItem_remove.addActionListener(this);
		popup.add(menuItem_remove);
		menuItem_properties = new JMenuItem(I18N.getString(Lang.MNU_PROPERTIES));
		menuItem_properties.addActionListener(this);
		popup.add(menuItem_properties);
		// Add listener to components that can bring up popup menus.
		lspanel.addMouseListener(new PopupListener());

		popup_list = new JPopupMenu();
		menuItem_list_delmod = new JMenuItem(I18N.getString(Lang.MNU_DELETE));
		menuItem_list_delmod.addActionListener(this);
		popup_list.add(menuItem_list_delmod);
		lstGates.addMouseListener(new PopupListener());
		lstGates.setCellRenderer(new GateListRenderer());
		fillGateList();
		setAppTitle();
		this.requestFocus();
	}

	/** File | Exit action performed */
	public void jMenuFileExit_actionPerformed(ActionEvent e) {
		if (showDiscardDialog(I18N.getString(Lang.MNU_EXIT)) == false)
			return;
		System.exit(0);
	}

	/** Help | About action performed */
	public void jMenuHelpAbout_actionPerformed(ActionEvent e) {
		new LSFrame_AboutBox(window);
		// JOptionPane.showMessageDialog(this, "LogicSim 2.0 BETA\n\nCopyright 2001
		// Andreas Tetzl\nandreas@tetzl.de\nwww.tetzl.de");
	}

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
			String fname = App.getModulePath() + jList_gates_model.getElementAt(popupModule) + ".mod";
			String s = I18N.getString(Lang.MSG_DELETE).replaceFirst("%s", fname);
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
				} else if (e.getSource() == lstGates) {
					int idx = lstGates.locationToIndex(e.getPoint());
					// TODO vor 6 stand hier actions.length
					if (idx >= 6) {
						popupModule = idx;
						popup_list.show(e.getComponent(), e.getX(), e.getY());
					}
				}
			}
		}
	}

	void jButton_addpoint_actionPerformed(ActionEvent e) {
		lspanel.setAction(LSPanel.ACTION_ADDPOINT);
	}

	void jButton_delpoint_actionPerformed(ActionEvent e) {
		lspanel.setAction(LSPanel.ACTION_DELPOINT);
	}

	void jToggleButton_simulate_actionPerformed(ActionEvent e) {
		JToggleButton btn = (JToggleButton) e.getSource();
		sim = Simulation.getInstance();
		sim.setPanel(lspanel);
		if (btn.isSelected()) {
			btn.setOpaque(true);
			btn.setBackground(Color.green);
			if (!sim.isRunning()) {
				lspanel.circuit.deactivateAll();
				repaint();
				sim.start();
			}
		} else {
			btn.setOpaque(false);
			if (sim.isRunning())
				sim.stop();
		}
	}

	void jButton_reset_actionPerformed(ActionEvent e) {
		staticReset();
		if (sim != null)
			sim.reset();
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

	void showMessage(String s) {
		JOptionPane.showMessageDialog(this, s);
	}

	boolean showDiscardDialog(String title) {
		if (lsFile.changed) {
			int result = Dialogs.confirmDiscardDialog();
			return (result == JOptionPane.YES_OPTION);
		}
		return true;
	}

	void jMenuItem_new_actionPerformed(ActionEvent e) {

		if (showDiscardDialog(I18N.getString(Lang.MNU_NEW)) == false)
			return;
		lspanel.clear();
		if (window != null) {
			window.setTitle("LogicSim");
		}

		lsFile = new LogicSimFile(defaultCircuitFileName());
		lspanel.setChangeListener(this);
		lspanel.repaint();
	}

	void jMenuItem_open_actionPerformed(ActionEvent e) {
		if (showDiscardDialog(I18N.getString(Lang.MNU_OPEN)) == false)
			return;
		lspanel.clear();

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
			btnSimulate.setSelected(false);
		}

		try {
			lsFile = XMLLoader.loadXmlFile(lsFile.fileName);
		} catch (RuntimeException x) {
			System.err.println(x);
			x.printStackTrace(System.err);
			showMessage(I18N.getString(Lang.ERR_READ) + " " + x.getMessage());
		}
		setAppTitle();
		lspanel.circuit.setGates(lsFile.getGates());
		lspanel.repaint();
//		if (lspanel.circuit.reconnect()) {
//			changedCircuit();
//			JOptionPane.showMessageDialog(this, I18N.getString(Lang.MSG_CIRCUITCHANGED));
//		}
		staticReset();
	}

	private FileFilter setupFilter() {
		LogicSimFileFilter filter = new LogicSimFileFilter();
		filter.addExtension(App.CIRCUIT_FILE_SUFFIX);
		filter.addExtension(App.MODULE_FILE_SUFFIX);
		filter.setDescription(
				"LogicSim Files (" + "." + App.CIRCUIT_FILE_SUFFIX + ", " + "." + App.MODULE_FILE_SUFFIX + ")");
		return filter;
	}

	private void setAppTitle() {
		String name = LSFrame.extractFileName(lsFile.fileName);
		name = "LogicSim - " + name;
		if (lsFile.changed)
			name += "*";
		if (window != null) {
			window.setTitle(name);
		}
	}

	private static String extractFileName(String fileName) {
		File f = new File(fileName);
		String name = f.getName();
		// strip extension
		name = name.substring(0, name.lastIndexOf('.'));
		return name;
	}

	void jMenuItem_save_actionPerformed(ActionEvent e) {
		String fileName = lsFile.fileName;
		boolean unnamed = false;
		if (extractFileName(lsFile.fileName).equals(I18N.getString(Lang.FILE_UNNAMED))) {
			unnamed = true;
//			if (lsFile.gateList.isModule()) {
//				lsFile.fileName = App.getModulePath() + lsFile.fileName + "." + App.MODULE_FILE_SUFFIX;
//			} else {
//				lsFile.fileName = App.getCircuitPath() + lsFile.fileName + "." + App.CIRCUIT_FILE_SUFFIX;
//			}
		}
		boolean showDialog = fileName == null || fileName.length() == 0;
		showDialog = showDialog || unnamed;

		if (showDialog)
			if (showSaveDialog() == false)
				return;
		lsFile.circuit = lspanel.circuit;
		XMLCreator.createXML(lsFile);
		// lsFile.changed = false;
		setAppTitle();

		String s = I18N.getString(Lang.STAT_SAVED).replaceFirst("%s", lsFile.fileName);
		sbText.setText(s);
		fillGateList();
	}

	public boolean showSaveDialog() {
		File file = new File(lsFile.fileName);
		String parentDirName = file.getParent();

		JFileChooser chooser = new JFileChooser(parentDirName);
		chooser.setDialogTitle(I18N.getString(Lang.MSG_SAVECIRCUIT));

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

	void jMenuItem_saveas_actionPerformed(ActionEvent e) {
		if (showSaveDialog() == false)
			return;
		jMenuItem_save_actionPerformed(e);
	}

	void jMenuItem_help_actionPerformed(ActionEvent e) {
		new HTMLHelp(use_language);
	}

	void jButton_open_actionPerformed(ActionEvent e) {
		this.jMenuItem_open_actionPerformed(e);
	}

	void jButton_save_actionPerformed(ActionEvent e) {
		this.jMenuItem_save_actionPerformed(e);
	}

	void jButton_new_actionPerformed(ActionEvent e) {
		this.jMenuItem_new_actionPerformed(e);
	}

	void jMenuItem_createmod_actionPerformed(ActionEvent e) {
		lsFile = new LogicSimFile(defaultModuleFileName());
		lspanel.setChangeListener(this);
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

	void jMenuItem_modproperties_actionPerformed(ActionEvent e) {
		if (FileInfoDialog.showFileInfo(this, lsFile)) {
			setAppTitle();
			// fillGateList();
		}
	}

	void jMenuItem_print_actionPerformed(ActionEvent e) {
		lspanel.doPrint();
	}

	void exportImage() {
		String filename = "logicsim.png";
		JFileChooser chooser = new JFileChooser();
		LogicSimFileFilter filter = new LogicSimFileFilter();
		filter.addExtension(".png");
		filter.setDescription("Portable Network Graphics");
		chooser.setFileFilter(filter);

		chooser.setDialogTitle(I18N.getString(Lang.MSG_SAVECIRCUIT));
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
		lspanel.circuit.deactivateAll();
		lspanel.paint(g);
		try {
			ImageIO.write(image, "png", new File(filename));
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	void fillGateList() {
		jList_gates_model.clear();

		for (Category cat : App.cats) {
			if ("hidden".equals(cat.title))
				continue;
			if (cat.getGates().size() == 0)
				continue;
			jList_gates_model.addElement(cat.title);
			for (Gate g : cat.getGates()) {
				jList_gates_model.addElement(g);
			}
		}
	}

	void jList_gates_mouseClicked(MouseEvent e) {
		int sel = lstGates.getSelectedIndex();
		if (sel < 0)
			return;
		int numInputs = Integer.parseInt(jComboBox_numinput.getSelectedItem().toString().substring(0, 1));

		Object o = lstGates.getSelectedValue();
		if (!(o instanceof Gate))
			return;

		Gate gate = (Gate) o;

		// gate is dummy gate, e.g. setting another input to high...
		if (gate.actionid > 0) {
			lspanel.setAction(gate.actionid);
			lstGates.clearSelection();
			return;
		}

		// gate is normal gate or module
		gate = GateLoaderHelper.create((Gate) o);
		if (gate instanceof Module) {
			Module m = (Module) gate;
			lspanel.setAction(m);
			sbText.setText(m.lsFile.getDescription());
		} else {
			// gate is normal Gate-Object
			if (gate.supportsVariableInputs())
				gate.setNumInputs(numInputs);

			if (gate.type.contains("test"))
				sbText.setText(gate.type);
			else if (I18N.hasString(gate.type, "description")) {
				sbText.setText(I18N.getString(gate.type, "description"));
			} else {
				sbText.setText(I18N.getString(gate.type, "title"));
			}
			lspanel.setAction(gate);
		}
		// TODO - this removes the selection from the list
		// it would be nicer to hold the selection until a gate is placed on lspanel
		// or any other button has been pressed
		lstGates.clearSelection();
	}

	void jCheckBoxMenuItem_paintGrid_actionPerformed(ActionEvent e) {
		lspanel.setPaintGrid(mSettingsPaintGrid.isSelected());
		lspanel.repaint();
		this.userProperties.setProperty("paint_grid", "" + mSettingsPaintGrid.isSelected());
		try {
			userProperties.store(new FileOutputStream("logicsim.cfg"), "LogicSim Configuration");
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	void jMenuItem_gatedesign_actionPerformed(ActionEvent e) {
		// Gate Design ausgew�hlt
		String gatedesign = null;
		if (this.jMenuItem_gatedesign_din.isSelected())
			gatedesign = "din";
		else
			gatedesign = "iso";
		this.userProperties.setProperty("gatedesign", gatedesign);
		LSFrame.gatedesign = gatedesign;
		// this.lspanel.gateList.reloadImages();
		this.lspanel.repaint();
		try {
			userProperties.store(new FileOutputStream("logicsim.cfg"), "LogicSim Configuration");
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	void jMenuItem_language_actionPerformed(ActionEvent e, String name) {
		// Sprache ausgew�hlt
		this.userProperties.setProperty("language", name);
		try {
			userProperties.store(new FileOutputStream("logicsim.cfg"), "LogicSim Configuration");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		showMessage(I18N.getString(Lang.MSG_LS_RESTART));
	}

	void create_language_menu(JMenu menu, String activeitem) {
		File dir = new File("languages/");
		String[] files = dir.list();
		java.util.Arrays.sort(files);
		for (int i = 0; i < files.length; i++) {
			if (files[i].endsWith(".txt")) {
				final String name = files[i].substring(0, files[i].length() - 4);
				JMenuItem item = new JRadioButtonMenuItem(name);
				if (name.equals(activeitem))
					item.setSelected(true);
				item.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(ActionEvent e) {
						jMenuItem_language_actionPerformed(e, name);
					}
				});
				buttongroup_language.add(item);
				menu.add(item);
			}
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
		sbText.setText(text);
	}

	@Override
	public void changedCoordinates(String text) {
		sbCoordinates.setText(text);
	}

	@Override
	public void changedActivePart(CircuitPart activePart) {
	}

	@Override
	public void setAction(int action) {
	}

	@Override
	public void needsRepaint(CircuitPart circuitPart) {
	}

}

class LSFrame_jList_gates_mouseAdapter extends java.awt.event.MouseAdapter {
	LSFrame adaptee;

	LSFrame_jList_gates_mouseAdapter(LSFrame adaptee) {
		this.adaptee = adaptee;
	}

	public void mouseClicked(MouseEvent e) {
		adaptee.jList_gates_mouseClicked(e);
	}
}

class LSFrame_jButton_addpoint_actionAdapter implements java.awt.event.ActionListener {
	LSFrame adaptee;

	LSFrame_jButton_addpoint_actionAdapter(LSFrame adaptee) {
		this.adaptee = adaptee;
	}

	public void actionPerformed(ActionEvent e) {
		adaptee.jButton_addpoint_actionPerformed(e);
	}
}

class LSFrame_jMenuItem_new_actionAdapter implements java.awt.event.ActionListener {
	LSFrame adaptee;

	LSFrame_jMenuItem_new_actionAdapter(LSFrame adaptee) {
		this.adaptee = adaptee;
	}

	public void actionPerformed(ActionEvent e) {
		adaptee.jMenuItem_new_actionPerformed(e);
	}
}

class LSFrame_jMenuItem_open_actionAdapter implements java.awt.event.ActionListener {
	LSFrame adaptee;

	LSFrame_jMenuItem_open_actionAdapter(LSFrame adaptee) {
		this.adaptee = adaptee;
	}

	public void actionPerformed(ActionEvent e) {
		adaptee.jMenuItem_open_actionPerformed(e);
	}
}

class LSFrame_jMenuItem_save_actionAdapter implements java.awt.event.ActionListener {
	LSFrame adaptee;

	LSFrame_jMenuItem_save_actionAdapter(LSFrame adaptee) {
		this.adaptee = adaptee;
	}

	public void actionPerformed(ActionEvent e) {
		adaptee.jMenuItem_save_actionPerformed(e);
	}
}

class LSFrame_jMenuItem_saveas_actionAdapter implements java.awt.event.ActionListener {
	LSFrame adaptee;

	LSFrame_jMenuItem_saveas_actionAdapter(LSFrame adaptee) {
		this.adaptee = adaptee;
	}

	public void actionPerformed(ActionEvent e) {
		adaptee.jMenuItem_saveas_actionPerformed(e);
	}
}

class LSFrame_jMenuItem_help_actionAdapter implements java.awt.event.ActionListener {
	LSFrame adaptee;

	LSFrame_jMenuItem_help_actionAdapter(LSFrame adaptee) {
		this.adaptee = adaptee;
	}

	public void actionPerformed(ActionEvent e) {
		adaptee.jMenuItem_help_actionPerformed(e);
	}
}

class LSFrame_jButton_delpoint_actionAdapter implements java.awt.event.ActionListener {
	LSFrame adaptee;

	LSFrame_jButton_delpoint_actionAdapter(LSFrame adaptee) {
		this.adaptee = adaptee;
	}

	public void actionPerformed(ActionEvent e) {
		adaptee.jButton_delpoint_actionPerformed(e);
	}
}

class LSFrame_jCheckBoxMenuItem_paintGrid_actionAdapter implements java.awt.event.ActionListener {
	LSFrame adaptee;

	LSFrame_jCheckBoxMenuItem_paintGrid_actionAdapter(LSFrame adaptee) {
		this.adaptee = adaptee;
	}

	public void actionPerformed(ActionEvent e) {
		adaptee.jCheckBoxMenuItem_paintGrid_actionPerformed(e);
	}
}