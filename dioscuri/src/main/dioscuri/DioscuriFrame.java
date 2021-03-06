/* $Revision$ $Date$ $Author$ 
 * 
 * Copyright (C) 2007-2009  National Library of the Netherlands, 
 *                          Nationaal Archief of the Netherlands, 
 *                          Planets
 *                          KEEP
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, 
 * MA  02110-1301, USA.
 *
 * For more information about this project, visit
 * http://dioscuri.sourceforge.net/
 * or contact us via email:
 *   jrvanderhoeven at users.sourceforge.net
 *   blohman at users.sourceforge.net
 *   bkiers at users.sourceforge.net
 * 
 * Developed by:
 *   Nationaal Archief               <www.nationaalarchief.nl>
 *   Koninklijke Bibliotheek         <www.kb.nl>
 *   Tessella Support Services plc   <www.tessella.com>
 *   Planets                         <www.planets-project.eu>
 *   KEEP                            <www.keep-project.eu>
 * 
 * Project Title: DIOSCURI
 */

package dioscuri;

import dioscuri.config.ConfigController;
import dioscuri.config.ConfigDialog;
import dioscuri.datatransfer.TextTransfer;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import gnu.rfb.server.*;
import gnu.vnc.WebServer;
import dioscuri.vnc.*;

/**
 * Graphical User Interface for emulator.
 */
@SuppressWarnings("serial")
public class DioscuriFrame extends JFrame implements GUI, ActionListener, KeyListener {

    // Attributes
    private Emulator emu;
    private TextTransfer textTransfer;
    boolean readOnlyConfig = false;

    // Panels
    private JScrollPane screenPane;
    private JPanel statusPanel;
    private JPanel scrolllockPanel;
    private JPanel numlockPanel;
    private JPanel capslockPanel;
    private JPanel floppyAPanel;
    private JPanel hd1Panel;
    private JPanel hd2Panel;

    // Menus
    JMenuBar menuBar;
    JMenu menuEmulator;
    JMenu menuEdit;
    JMenu menuMedia;
    JMenu menuDevices;
    JMenu menuConfig;
    JMenu menuHelp;

    // Menu items
    // Menu emulator
    JMenuItem miEmulatorStart;
    JMenuItem miEmulatorStop;
    JMenuItem miEmulatorReset;
    JMenuItem miEmulatorQuit;
    // Menu edit
    JMenuItem miEditCopyText;
    JMenuItem miEditScreenShot;

    // Menu media
    JMenuItem miMediaEjectA;
    JMenuItem miMediaInsertA;
    // Menu devices
    JMenuItem miDevicesMouseEnabled;
    JMenuItem miDevicesMouseDisabled;
    // Menu configure
    JMenuItem miEditConfig;
    // Menu help
    JMenuItem miHelpAbout;

    // Input/output devices
    private JPanel screen; // Defines the screen of the emulator
    private MouseHandler mouseHandler;

    // File selection
    private JFileChooser fcFloppy;

    private JLabel cpuTypeLabel;

    // Logging
    private static final Logger logger = Logger.getLogger(DioscuriFrame.class.getName());

    // Frame and refresh properties

    private String configFilePath;

    // Emulator configuration
    dioscuri.config.Emulator emuConfig;

    // command line parsing options
    CommandLineInterface cli;

    // VNC
    private RFBHost rfbHost;
    VNCTopFrame vncTopFrame;

    // Constructors

    /**
     * Class constructor
     */
    public DioscuriFrame() {
        // Create graphical user interface

        // Add handlers and listeners
        // Window closing listener
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                exitDioscuri();
            }
        });

        super.setIconImage(new ImageIcon(Constants.EMULATOR_ICON_IMAGE).getImage());

        // Create menubar
        this.initMenuBar();

        // Create panel: screen (canvas)
        screenPane = new JScrollPane();
        screenPane.setBackground(Color.BLACK);
        this.setScreen(this.getStartupScreen());

        // Create panel: statusbar (including panels w/ borders for Num Lock,
        // Caps Lock and Scroll Lock status)
        statusPanel = new JPanel(new FlowLayout(FlowLayout.LEADING, 0, 0));
        this.initStatusBar();

        // Add panels to frame (arranged in borderlayout)
        this.getContentPane().add(screenPane, BorderLayout.CENTER);
        this.getContentPane().add(statusPanel, BorderLayout.SOUTH);

        // Create file choosers
        fcFloppy = new JFileChooser();

        // Set dimensions
        int guiWidth = screenPane.getWidth() + 10;
        // value
        int guiHeight = screenPane.getHeight() + (2 * 38) + 5;
        // & statusbar height

        // Actions
        // Key handler to the GUI, disabling focus traversal so Tab events are
        // available
        // KeyEvents will be handled here in screen
        this.addKeyListener(this);

        // Build frame
        this.setLocation(GUI_X_LOCATION, GUI_Y_LOCATION);
        this.setSize(guiWidth, guiHeight + 10);
        this.setTitle(this.getEmulatorName());
        this.setResizable(false);
        this.updateGUI(GUI_RESET);

        // Create clipboard functionality
        textTransfer = new TextTransfer(this);

        // Disable that TAB and SHIFT-TAB key strokes don't result in
        // component-traversals.
        super.setFocusTraversalKeysEnabled(false);

        System.out.println(this);
    }

    /**
     * @param arguments
     * @throws Exception
     */
    public DioscuriFrame(String[] arguments) {
        // Define GUI
        this();

        try {
            cli = new CommandLineInterface(arguments);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error while paring the command line parameters:");
            e.printStackTrace();
        }

        configFilePath = cli.configFilePath;

        // Show / hide GUI (based on command line parameter)
        this.setVisible(cli.visible);

        logger.log(Level.INFO, cli.visible ? "[gui] GUI is visible and has focus" : "[gui] GUI is hidden");

        if (cli.autorun) {
            emu = new Emulator(this);
            new Thread(emu).start();
            this.updateGUI(EMU_PROCESS_START);
        }
    }

    // Methods

    /**
     * Initialise menu bar
     */
    private void initMenuBar() {
        // Create a menubar
        menuBar = new JMenuBar();

        // Create menu: emulator
        menuEmulator = new JMenu("Emulator");
        miEmulatorStart = new JMenuItem("Start process (power on)");
        miEmulatorStop = new JMenuItem("Stop process (shutdown)");
        miEmulatorReset = new JMenuItem("Reset process (warm reset)");
        miEmulatorQuit = new JMenuItem("Quit");
        menuEmulator.add(miEmulatorStart);
        menuEmulator.add(miEmulatorStop);
        menuEmulator.add(miEmulatorReset);
        menuEmulator.add(miEmulatorQuit);

        // Create menu: edit
        menuEdit = new JMenu("Edit");
        miEditCopyText = new JMenuItem("Copy text");
        miEditScreenShot = new JMenuItem("Make screen shot");
        menuEdit.add(miEditCopyText);
        menuEdit.add(miEditScreenShot);

        // Create menu: media
        menuMedia = new JMenu("Media");
        miMediaEjectA = new JMenuItem("Eject floppy A:");
        miMediaInsertA = new JMenuItem("Insert floppy A:");
        menuMedia.add(miMediaEjectA);
        menuMedia.add(miMediaInsertA);

        // Create menu: devices
        menuDevices = new JMenu("Devices");
        miDevicesMouseEnabled = new JMenuItem("Mouse: enable");
        miDevicesMouseDisabled = new JMenuItem("Mouse: disable");
        menuDevices.add(miDevicesMouseEnabled);
        menuDevices.add(miDevicesMouseDisabled);

        // Create menu: config
        menuConfig = new JMenu("Configure");
        miEditConfig = new JMenuItem("Edit Config");
        menuConfig.add(miEditConfig);

        // Create menu: help
        menuHelp = new JMenu("Help");
        miHelpAbout = new JMenuItem("About..");
        menuHelp.add(miHelpAbout);

        // Assign all menus to the menubar
        menuBar.add(menuEmulator);
        menuBar.add(menuEdit);
        menuBar.add(menuMedia);
        menuBar.add(menuDevices);
        menuBar.add(menuConfig);
        menuBar.add(menuHelp);

        // Add action listeners for tracing events
        miEmulatorStart.addActionListener(this);
        miEmulatorStop.addActionListener(this);
        miEmulatorReset.addActionListener(this);
        miEmulatorQuit.addActionListener(this);
        miEditCopyText.addActionListener(this);
        miMediaEjectA.addActionListener(this);
        miMediaInsertA.addActionListener(this);
        miDevicesMouseEnabled.addActionListener(this);
        miDevicesMouseDisabled.addActionListener(this);
        miEditConfig.addActionListener(this);
        miHelpAbout.addActionListener(this);
        miEditScreenShot.addActionListener(this);

        // Assign menubar to frame
        this.setJMenuBar(menuBar);

        // Disable the default behavior of F10 and ALT in all major "look-and-feel"-s
        // (set the focus on the first JMenu in the JMenuBar):
        this.menuBar.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke(KeyEvent.VK_F10, 0), "none");
    }

    /**
     * Initialise status bar
     */
    private void initStatusBar() {
        Border blackline;
        blackline = BorderFactory.createLineBorder(Color.black);

        numlockPanel = new JPanel();
        // numlockPanel.setLayout(new BoxLayout(numlockPanel,
        // BoxLayout.X_AXIS));
        JLabel numlockPanelLabel = new JLabel("1");
        numlockPanelLabel.setAlignmentX(JLabel.CENTER);
        numlockPanel.add(numlockPanelLabel);
        numlockPanel.setBorder(blackline);
        numlockPanel.setSize(20, 20);

        capslockPanel = new JPanel();
        // capslockPanel.setLayout(new BoxLayout(capslockPanel,
        // BoxLayout.X_AXIS));
        JLabel capslockPanelLabel = new JLabel("A");
        capslockPanelLabel.setHorizontalAlignment(JLabel.CENTER);
        capslockPanel.add(capslockPanelLabel);
        capslockPanel.setBorder(blackline);

        scrolllockPanel = new JPanel();
        // scrolllockPanel.setLayout(new BoxLayout(scrolllockPanel,
        // BoxLayout.X_AXIS));
        JLabel scrolllockPanelLabel = new JLabel("S");
        scrolllockPanelLabel.setHorizontalAlignment(JLabel.CENTER);
        scrolllockPanel.add(scrolllockPanelLabel);
        scrolllockPanel.setBorder(blackline);

        floppyAPanel = new JPanel();
        // floppyAPanel.setLayout(new BoxLayout(floppyAPanel,
        // BoxLayout.X_AXIS));
        JLabel floppyAPanelLabel = new JLabel("A:");
        floppyAPanelLabel.setHorizontalAlignment(JLabel.CENTER);
        floppyAPanel.add(floppyAPanelLabel);
        floppyAPanel.setBorder(blackline);

        hd1Panel = new JPanel();
        JLabel hd1PanelLabel = new JLabel("HD1");
        hd1PanelLabel.setHorizontalAlignment(JLabel.CENTER);
        hd1Panel.add(hd1PanelLabel);
        hd1Panel.setBorder(blackline);

        hd2Panel = new JPanel();
        JLabel hd2PanelLabel = new JLabel("HD2");
        hd2PanelLabel.setHorizontalAlignment(JLabel.CENTER);
        hd2Panel.add(hd2PanelLabel);
        hd2Panel.setBorder(blackline);

        // Add panels to statusbar (with spaces inbetween)
        statusPanel.add(Box.createHorizontalGlue());
        statusPanel.add(numlockPanel);
        statusPanel.add(Box.createRigidArea(new Dimension(5, 0)));
        statusPanel.add(capslockPanel);
        statusPanel.add(Box.createRigidArea(new Dimension(5, 0)));
        statusPanel.add(scrolllockPanel);
        statusPanel.add(Box.createRigidArea(new Dimension(5, 0)));
        statusPanel.add(floppyAPanel);
        statusPanel.add(Box.createRigidArea(new Dimension(5, 0)));
        statusPanel.add(hd1Panel);
        statusPanel.add(Box.createRigidArea(new Dimension(5, 0)));
        statusPanel.add(hd2Panel);
        statusPanel.add(Box.createRigidArea(new Dimension(5, 0)));
        cpuTypeLabel = new JLabel("");
        statusPanel.add(cpuTypeLabel);
    }

    /**
     * Returns the name of emulator
     *
     * @return String containing name
     */
    public String getEmulatorName() {
        return Constants.EMULATOR_NAME;
    }

    /**
     * Returns the version stamp of emulator
     *
     * @return String containing version
     */
    public String getEmulatorVersion() {
        return Constants.EMULATOR_VERSION;
    }

    /**
     * Returns the date stamp of emulator
     *
     * @return String containing date
     */
    public String getEmulatorDate() {
        return Constants.EMULATOR_DATE;
    }

    /**
     * Returns the startup screen for GUI
     *
     * @return Canvas startup screen
     */
    private JPanel getStartupScreen() {
        // Create startup screen
        StartupPanel startup = new StartupPanel();
        startup.setSize(710, 401);

        ImageIcon pic = new ImageIcon(Constants.EMULATOR_SPLASHSCREEN_IMAGE);
        startup.add(new JLabel(pic));

        return startup;
    }

    /**
     * Set given screen to existing screen of GUI
     *
     * @param screen containing a reference to canvas of module screen
     */
    public void setScreen(JPanel screen) {
        // Replace current canvas with new one
        screenPane.removeAll();
        screenPane.add(screen);

        // Attach current screen to given screen
        this.screen = screen;

        // Update panel
        this.updateScreenPanel();
    }

    /**
     * Enable mouse support in GUI
     *
     * @return true if mouse enabled, false otherwise
     */
    public boolean setMouseEnabled() {

        // Mouse handler to the GUI
        mouseHandler = new MouseHandler();
        screen.addMouseListener(mouseHandler);
        screen.addMouseMotionListener(mouseHandler);
        logger.log(Level.INFO, "[gui] Mouse in GUI enabled");

        return true;
    }

    /**
     * Disable mouse support in GUI
     *
     * @return true if mouse enabled, false otherwise
     */
    public boolean setMouseDisabled() {
        // Mouse handler to the GUI
        if (mouseHandler != null) {
            screen.removeMouseListener(mouseHandler);
            screen.removeMouseMotionListener(mouseHandler);
            mouseHandler.setMouseCursorVisibility(true);
            mouseHandler = null;
            logger.log(Level.INFO, "[gui] Mouse in GUI disabled");

            return true;
        }

        logger.log(Level.INFO, "[gui] Mouse does not exist or is already disabled");

        return true;
    }

    /**
     * Returns a buffered image loaded from specified location
     *
     * @param path location where image resides
     */
    private BufferedImage getImageFromFile(URL path) {
        BufferedImage image = null;
        try {
            image = ImageIO.read(path);
        } catch (IOException e) {
            logger.log(Level.WARNING,
                    "GUI error: problem during loading image.");
        }
        return image;
    }

    /**
     * Update the screen panel on screen frame.
     */
    protected void updateScreenPanel() {
        // Repaint canvas
        screenPane.setSize(screen.getWidth(), screen.getHeight());
        // FIXME: notice when canvas of screen has changed (different size) and
        // update screenPanel.setSize(width, height);
        // guiWidth = width + 10; // width + 2 * 5 px sidebars
        // guiHeight = height + 2 * panelHeight; // height + menu & statuspanels
        // this.setSize(guiWidth, guiHeight);
        // this.repaint();

        screen.repaint();
    }

    /**
     * Updates the status panel on screen frame.
     */
    protected void updateStatusPanel() {
        // TODO: implement
        statusPanel.repaint();
    }

    /**
     * Update the GUI, including menu and statusbar.
     *
     * @param activity defining the kind of update is required
     */
    public void updateGUI(int activity) {
        switch (activity) {
            case EMU_PROCESS_START:

                // Redefine menu options
                miEmulatorStart.setEnabled(false);
                miEmulatorStop.setEnabled(true);
                miEmulatorReset.setEnabled(true);
                miEditScreenShot.setEnabled(true);
                miEditConfig.setEnabled(false);
                miEditCopyText.setEnabled(true);
                break;

            case EMU_PROCESS_STOP:

                // Redefine menu options
                miEmulatorStart.setEnabled(true);
                miEmulatorStop.setEnabled(false);
                miEmulatorReset.setEnabled(false);
                miMediaEjectA.setEnabled(false);
                miMediaInsertA.setEnabled(false);
                miEditCopyText.setEnabled(false);
                miDevicesMouseEnabled.setEnabled(false);
                miDevicesMouseDisabled.setEnabled(false);

                // Redefine statusbar
                floppyAPanel.setVisible(false);
                hd1Panel.setVisible(false);
                hd2Panel.setVisible(false);
                miEditConfig.setEnabled(true);
                cpuTypeLabel.setText("");
                break;

            case EMU_PROCESS_RESET:

                // Redefine menu options
                miEmulatorStart.setEnabled(false);
                miEmulatorStop.setEnabled(true);
                miEmulatorReset.setEnabled(true);

                miEditConfig.setEnabled(false);

                break;

            case EMU_FLOPPYA_INSERT:

                // Redefine menu options
                miMediaEjectA.setEnabled(true);
                miMediaInsertA.setEnabled(false);
                // Show A: in statusbar
                floppyAPanel.setVisible(true);

                break;

            case EMU_FLOPPYA_EJECT:
                // Redefine menu options
                miMediaEjectA.setEnabled(false);
                miMediaInsertA.setEnabled(true);
                // Hide A: in statusbar
                floppyAPanel.setVisible(false);
                break;

            case EMU_FLOPPYA_TRANSFER_START:
                // Highlight A: in statusbar
                // NOTE: Used to use floppyAPanel.getComponent(0) to retrieve comp,
                // but as it is not part of layout anymore it is not necesarry
                floppyAPanel.setBackground(Color.GREEN);
                break;

            case EMU_FLOPPYA_TRANSFER_STOP:
                // Shadow A: in statusbar
                floppyAPanel.setBackground(Color.LIGHT_GRAY);
                break;

            case EMU_HD1_INSERT:
                hd1Panel.setVisible(true);
                break;

            case EMU_HD2_INSERT:
                hd2Panel.setVisible(true);
                break;

            case EMU_HD1_EJECT:
                hd1Panel.setVisible(false);
                break;

            case EMU_HD2_EJECT:
                hd2Panel.setVisible(false);
                break;

            case EMU_HD1_TRANSFER_START:
                hd1Panel.setBackground(Color.GREEN);
                break;

            case EMU_HD2_TRANSFER_START:
                hd2Panel.setBackground(Color.GREEN);
                break;

            case EMU_HD1_TRANSFER_STOP:
                hd1Panel.setBackground(Color.LIGHT_GRAY);
                break;

            case EMU_HD2_TRANSFER_STOP:
                hd2Panel.setBackground(Color.LIGHT_GRAY);
                break;

            case EMU_KEYBOARD_NUMLOCK_ON:
                numlockPanel.setBackground(Color.YELLOW);
                break;

            case EMU_KEYBOARD_NUMLOCK_OFF:
                numlockPanel.setBackground(Color.LIGHT_GRAY);
                break;

            case EMU_KEYBOARD_CAPSLOCK_ON:
                capslockPanel.setBackground(Color.YELLOW);
                break;

            case EMU_KEYBOARD_CAPSLOCK_OFF:
                capslockPanel.setBackground(Color.LIGHT_GRAY);
                break;

            case EMU_KEYBOARD_SCROLLLOCK_ON:
                scrolllockPanel.setBackground(Color.YELLOW);
                break;

            case EMU_KEYBOARD_SCROLLLOCK_OFF:
                scrolllockPanel.setBackground(Color.LIGHT_GRAY);
                break;

            case EMU_DEVICES_MOUSE_ENABLED:
                miDevicesMouseEnabled.setEnabled(false);
                miDevicesMouseDisabled.setEnabled(true);
                break;

            case EMU_DEVICES_MOUSE_DISABLED:
                miDevicesMouseEnabled.setEnabled(true);
                miDevicesMouseDisabled.setEnabled(false);
                break;

            case GUI_RESET:
                // Enable/disable menu items
                miEmulatorStop.setEnabled(false);
                miEmulatorReset.setEnabled(false);
                miEditCopyText.setEnabled(false);
                miEditScreenShot.setEnabled(false);
                miMediaInsertA.setEnabled(false);
                miMediaEjectA.setEnabled(false);
                miDevicesMouseEnabled.setEnabled(false);
                miDevicesMouseDisabled.setEnabled(false);

                // Enable/disable status bar items
                floppyAPanel.setVisible(false);
                hd1Panel.setVisible(false);
                hd2Panel.setVisible(false);
                cpuTypeLabel.setText("");
                break;

            default:
                logger.log(Level.WARNING, "No update on GUI could be performed.");
        }
    }

    /**
     * Implementation of the interface ActionListener. Takes care of events.
     *
     * @param e
     */
    public void actionPerformed(ActionEvent e)
    {
        JComponent c = (JComponent) e.getSource();
        if (c == (JComponent) miEmulatorStart) {
            this.loadConfigFile();
            boolean vncEnabled = emuConfig.getArchitecture().getModules().getVnc().isEnabled();
            if (vncEnabled)
                this.startVncServer();
            else {
                // Start emulation process
                emu = new Emulator(this);
                new Thread(emu).start();
                this.updateGUI(EMU_PROCESS_START);
            }
        } else if (c == (JComponent) miEmulatorStop) {
            // Stop emulation process
            emu.stop();
            this.setMouseDisabled();
            if (this.rfbHost != null)
                this.stopVncServer();
            this.updateGUI(EMU_PROCESS_STOP);
        } else if (c == (JComponent) miEmulatorReset) {
            // Reset emulation process
            emu.reset();
            this.updateGUI(EMU_PROCESS_RESET);
        } else if (c == (JComponent) miEmulatorQuit) {
            // Quit application
            this.exitDioscuri();
        } else if (c == (JComponent) miEditCopyText) {
            // Copy text from screen to clipboard
            if (emu != null) {
                String text = emu.getScreenText();
                if (text != null) {
                    // Text has been extracted from emulation process
                    // Send text to clipboard
                    textTransfer.setClipboardContents(text);

                    // TODO: update GUI to allow pasting text
                }
            }
        } else if (c == (JComponent) miMediaEjectA) {
            // Eject floppy in drive A
            if (emu.ejectFloppy("A")) {
                this.updateGUI(EMU_FLOPPYA_EJECT);
            }
        } else if (c == (JComponent) miMediaInsertA) {
            // Insert floppy in drive A
            // Open file select dialog box
            int retval = fcFloppy.showOpenDialog(this);
            if (retval == JFileChooser.APPROVE_OPTION) {
                File imageFile = fcFloppy.getSelectedFile();

                // Insert floppy
                boolean writeProtected = false;
                if (emu.insertFloppy("A", (byte) 0x04, imageFile, writeProtected)) {
                    this.updateGUI(EMU_FLOPPYA_INSERT);
                }
            } else if (retval == JFileChooser.ERROR_OPTION) {
                JOptionPane.showMessageDialog(this,
                        "Could not select image from file system.");
            }
        } else if (c == (JComponent) miDevicesMouseEnabled) {
            // Enable mouse
            this.setMouseEnabled();
            this.updateGUI(EMU_DEVICES_MOUSE_ENABLED);
        } else if (c == (JComponent) miDevicesMouseDisabled) {
            // Disable mouse
            this.setMouseDisabled();
            this.updateGUI(EMU_DEVICES_MOUSE_DISABLED);
        } else if (c == (JComponent) miEditConfig) {
            if (emuConfig == null) {
                File config = new File(this.configFilePath);
                try {
                    emuConfig = ConfigController.loadFromXML(config);
                } catch (Exception ex) {
                    logger.log(Level.SEVERE, "[GUI] Config file not readable: " + ex.toString());
                    return;
                }
            }

            // Load edit screen or show warning config is read-only
            if (readOnlyConfig) {
                JOptionPane.showMessageDialog(this,
                        "No editable configuration found.\nDefault configuration loaded from jar file and is read-only",
                        "Configuration", JOptionPane.WARNING_MESSAGE);
            } else {
                //new SelectionConfigDialog(this);
                new ConfigDialog(this);
            }
        } else if (c == (JComponent) miHelpAbout) {
            // Show About dialog
            JOptionPane.showMessageDialog(this,
                    this.getEmulatorName()
                            + "\n"
                            + "Version "
                            + this.getEmulatorVersion()
                            + ", Copyright (C) "
                            + this.getEmulatorDate()
                            + " by "
                            + "\n\n"
                            + " Koninklijke Bibliotheek (KB, the national Library of the Netherlands)\n"
                            + " The Nationaal Archief of the Netherlands\n"
                            + " Planets project\n"
                            + " KEEP project\n"
                            + "\n"
                            + " This program is free software; you can redistribute it and/or\n"
                            + " modify it under the terms of the GNU General Public License\n"
                            + " as published by the Free Software Foundation; either version 2\n"
                            + " of the License, or (at your option) any later version.\n"
                            + "\n"
                            + " This program is distributed in the hope that it will be useful,\n"
                            + " but WITHOUT ANY WARRANTY; without even the implied warranty of\n"
                            + " MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the\n"
                            + " GNU General Public License for more details.\n\n\n"
                            + " Credits: Bram Lohman, Chris Rose, Bart Kiers, Jeffrey van der Hoeven",
                    "About", JOptionPane.INFORMATION_MESSAGE);
        } else if (c == miEditScreenShot) {
            String fileName = "Screenshot_" + System.currentTimeMillis() + ".png";
            File file = new File(fileName);
            BufferedImage image = new BufferedImage(screen.getWidth(), screen.getHeight(), BufferedImage.TYPE_INT_RGB);
            screen.paint(image.createGraphics());
            try {
                ImageIO.write(image, "png", file);
                JOptionPane.showMessageDialog(this, "An image was created and saved as:\n\n" +
                        file.getAbsolutePath());
            } catch (IOException e1) {
                JOptionPane.showMessageDialog(this, "Could not create an image at:\n\n" +
                        file.getAbsolutePath() + "\n\n" + e1.getMessage());
            }
        }
    }

    /**
     * Implement the KeyListener method keyTyped Empty method, not used
     *
     * @param keyEvent
     */
    public void keyTyped(KeyEvent keyEvent) {
        logger.log(Level.INFO, displayInfo(keyEvent, "KEY TYPED: "));
        // FIXME: handle key typed event emu.generateScancode(keyEvent ,
        // KEY_TYPED);
    }

    /**
     * Implement the KeyListener method keyPressed Handles key press events
     *
     * @param keyEvent
     */
    public void keyPressed(KeyEvent keyEvent) {
        // Pass keyPress on to keyboard to generate scancode from it
        logger.log(Level.INFO, displayInfo(keyEvent, "KEY PRESSED: "));
        if (emu != null) {
            emu.notifyKeyboard(keyEvent, KEY_PRESSED);
        }
    }

    /**
     * Implement the KeyListener method keyReleased Handles key release events
     *
     * @param keyEvent
     */
    public void keyReleased(KeyEvent keyEvent) {
        // Pass keyPress on to keyboard to generate scancode from it
        logger.log(Level.INFO, displayInfo(keyEvent, "KEY RELEASED: "));
        if (emu != null) {
            emu.notifyKeyboard(keyEvent, KEY_RELEASED);
        }
    }

    /**
     * Creates a string containing keypress events
     *
     * @param keyEvent           KeyEvent handled (either Pressed or Released)
     * @param pressReleaseString String passed from the KeyEvent, indicating a press or release
     * @return String containing keypress events
     */
    protected String displayInfo(KeyEvent keyEvent, String pressReleaseString) {
        String output;
        String keyString, modString, tmpString, actionString, locationString;
        String newline = "\n";

        int keyCode = keyEvent.getKeyCode();
        keyString = "key code = " + keyCode + " ("
                + KeyEvent.getKeyText(keyCode) + ")";

        int modifiers = keyEvent.getModifiersEx();
        modString = "modifiers = " + modifiers;
        tmpString = KeyEvent.getModifiersExText(modifiers);
        if (tmpString.length() > 0) {
            modString += " (" + tmpString + ")";
        } else {
            modString += " (no modifiers)";
        }

        actionString = "action key? ";
        if (keyEvent.isActionKey()) {
            actionString += "YES";
        } else {
            actionString += "NO";
        }

        locationString = "key location: ";
        int location = keyEvent.getKeyLocation();
        if (location == KeyEvent.KEY_LOCATION_STANDARD) {
            locationString += "standard";
        } else if (location == KeyEvent.KEY_LOCATION_LEFT) {
            locationString += "left";
        } else if (location == KeyEvent.KEY_LOCATION_RIGHT) {
            locationString += "right";
        } else if (location == KeyEvent.KEY_LOCATION_NUMPAD) {
            locationString += "numpad";
        } else { // (location == KeyEvent.KEY_LOCATION_UNKNOWN)
            locationString += "unknown";
        }

        output = "[GUI] "
                + (pressReleaseString + newline + "    " + keyString + newline
                + "    " + modString + newline + "    " + actionString
                + newline + "    " + locationString + newline);

        return output;
    }

    public dioscuri.config.Emulator getEmuConfig() {
        return emuConfig;
    }

    @Override
    public String getConfigFilePath() {
        return this.configFilePath;
    }

    /**
     * @param emuObject
     * @return -
     */
    public boolean saveXML(dioscuri.config.Emulator emuObject) {
        try {
            ConfigController.saveToXML(emuObject, new File(this.configFilePath));
        } catch (Exception e) {
            logger.log(Level.SEVERE, " [gui] Failed to save config file");
            return false;
        }
        return true;
    }

    /**
     * Notify GUI about status of emulation process and take appropriate GUI
     * action
     *
     * @param emulatorStatus indicates the state change of the emulator
     */
    public void notifyGUI(int emulatorStatus) {
        // Check which kind of notification is given
        if (emulatorStatus == GUI.EMU_PROCESS_STOP) {
            if (cli.autoshutdown) {
                // Exit application
                this.exitDioscuri();
            } else {
                // Update GUI status
                this.updateGUI(GUI.EMU_PROCESS_STOP);
            }
        }

        // TODO: add all notifications here that are done by emulator class.
        // Currently, emulator class is directly calling gui.update(..)

    }

    /**
     * Exit the GUI and stop the application
     */
    private void exitDioscuri() {
        dispose();
        if (emu != null) {
            emu.setActive(false);
        }
        System.exit(0);
    }

    /**
     * Start the VNC Server
     */
    private void startVncServer()
    {
        //variables
        int vncPort = Integer.parseInt(emuConfig.getArchitecture().getModules().getVnc().getPort().toString());
        String vncPassword = emuConfig.getArchitecture().getModules().getVnc().getPassword();
        int tcpPort = 5900+vncPort;
        String vncClassName = "VNCTopFrame";

        JLabel label = null;

        boolean portAvail = VNCUtil.available(tcpPort);
        if (!portAvail)
        {
            label = new JLabel("Port " + (tcpPort) + " occupied. Please choose another port!");
            JOptionPane.showMessageDialog(this, label);
        }
        else
        {
            RFBAuthenticator vncAuthenticator = new DefaultRFBAuthenticator(vncPassword);
            try
            {
                this.rfbHost = new RFBHost(vncPort, vncClassName, VNCTopFrame.class, vncAuthenticator);
                new WebServer( vncPort, vncClassName, screenPane.getWidth(), screenPane.getHeight() );
            }
            catch( NoSuchMethodException ex )
            {
                System.out.println( "  Unsupported class: " + vncClassName );
                Logger.getLogger(VNCUtil.class.getName()).log(Level.SEVERE, null, ex);
            }

            // wait for the SharedServer initialization
            this.vncTopFrame = null;
            while (vncTopFrame == null)
            {
                vncTopFrame = (VNCTopFrame)rfbHost.getSharedServer();
                try {
                    Thread.sleep(20);
                } catch (InterruptedException event) {
                    event.printStackTrace();
                }
            }

            // attach the screenPane to the vncTopFrame
            // TODO: reattach the screenPane on stop
            vncTopFrame.setInternalFrame(screenPane, this, mouseHandler);

            // change the title of the emulator
            this.setTitle(Constants.EMULATOR_NAME + " -= VNC Mode =-");

            // add a text to alert, that the emulator runs in VNC mode
            JPanel infoPanel = new JPanel(new GridLayout(0, 1));
            infoPanel.setPreferredSize(new Dimension(200, 100));

            final String TAB = "             ";
            infoPanel.add(new JLabel(""));
            infoPanel.add(new JLabel(""));
            infoPanel.add(new JLabel(""));
            infoPanel.add(new JLabel(TAB+"Emulator is in VNC mode listening on port: " + tcpPort));
            infoPanel.add(new JLabel(TAB+"Connect to this VNC session by pointing a VNC viewer to:"));

            String ip = VNCUtil.getHostIP();
            String name = VNCUtil.getHostName();

            if(ip != null) {
                infoPanel.add(new JLabel(TAB + TAB + "    " + ip + ":" + tcpPort));
            }

            if(name != null) {
                if(ip != null) {
                    infoPanel.add(new JLabel(TAB + TAB + "or"));
                }
                infoPanel.add(new JLabel(TAB + TAB + "    " + name + ":" + tcpPort));
            }

            infoPanel.add(new JLabel(TAB+"using 'password' (without the single quotes) as the password."));
            infoPanel.add(new JLabel(TAB+""));
            infoPanel.add(new JLabel(TAB+""));
            infoPanel.add(new JLabel(TAB+""));
            
            // Start emulation process
            emu = new Emulator(this);
            new Thread(emu).start();
            this.updateGUI(EMU_PROCESS_START);
            
            // Attach the fakeScreenPane with the info to the DioscuriFrame
            JScrollPane fakeScreenPane = new JScrollPane(infoPanel);
            fakeScreenPane.setSize(200,100);
            this.getContentPane().add(fakeScreenPane, BorderLayout.CENTER);
        }
    }

    /**
     * stop the VNC server
     */
    private void stopVncServer ()
    {
        this.getContentPane().add(this.screenPane);
        this.rfbHost.stop();
        this.rfbHost = null;
    }

    /*
     * load the config file
     */

    private void loadConfigFile() {
        if (emuConfig == null) {
            File config = new File(this.configFilePath);
            try {
                emuConfig = ConfigController.loadFromXML(config);
            } catch (Exception ex) {
                logger.log(Level.SEVERE, "[GUI] Config file not readable: " + ex.toString());
            }
        }
    }

    /**
     * Versioning information
     */
    @Override
    public String toString() {
        String bar = "+-------------------------------------------------------------------------------------+";
        return
                "\r\n" + bar +
                        String.format("%n| %1$-" + (bar.length() - 4) + "s |%n", Constants.EMULATOR_NAME) +
                        String.format("| %1$-" + (bar.length() - 4) + "s |%n", "Copyright (C) " + Constants.EMULATOR_DATE) +
                        String.format("| %1$-" + (bar.length() - 4) + "s |%n", Constants.EMULATOR_CREATOR) +
                        bar + "\r\n";
    }

    /**
     * Inner class MouseHandler Takes care of any mouse action (motion,
     * clicking)
     */
    private class MouseHandler implements MouseInputListener {
        // Attributes
        Cursor invisibleCursor;

        public MouseHandler() {
            // Make cursor invisible
            //this.setMouseCursorVisible(false);
        }

        public void mouseClicked(MouseEvent mouseEvent) {
        }

        public void mousePressed(MouseEvent mouseEvent) {
            if (emu != null) {
                emu.notifyMouse(mouseEvent);
            }
        }

        public void mouseReleased(MouseEvent mouseEvent) {
            if (emu != null) {
                emu.notifyMouse(mouseEvent);
            }
        }

        public void mouseEntered(MouseEvent mouseEvent) {
            this.setMouseCursorVisibility(false);
        }

        public void mouseExited(MouseEvent mouseEvent) {
        }

        public void mouseDragged(MouseEvent mouseEvent) {
        }

        public void mouseMoved(MouseEvent mouseEvent) {
            if (emu != null) {
                emu.notifyMouse(mouseEvent);
            }
        }

        public void setMouseCursorVisibility(boolean visible) {
            if (!visible) {
                // Hide cursor
                ImageIcon emptyIcon = new ImageIcon(new byte[0]);
                invisibleCursor = getToolkit().createCustomCursor(
                        emptyIcon.getImage(), new Point(0, 0), "Invisible");
                screen.setCursor(invisibleCursor);
            } else {
                // Show cursor
                screen.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                // setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            }

        }
    }

    //public String getConfigFilePath() {
    //    return configFilePath;
    //}

    public JFrame asJFrame() {
        return this;
    }

    @Override
    public void setCpuTypeLabel(String cpuType) {
        cpuTypeLabel.setText("  " + cpuType);
    }

    /**
     * Main entry point.
     *
     * @param args containing command line arguments
     */
    public static void main(final String[] args) {
        // Load logging.properties
        try {
            // Check for a local system logging.properties file
            File localLogFile = new File(Constants.EMULATOR_LOGGING_PROPERTIES);
            if (localLogFile.exists() && localLogFile.canRead()) {
                LogManager.getLogManager().readConfiguration(
                        new BufferedInputStream(new FileInputStream(localLogFile)));
                logger.log(Level.INFO, "Logging.properties loaded from local file " + localLogFile);
            } else {
                logger.log(Level.WARNING, "No logging.properties file found locally: " + localLogFile.getAbsolutePath());
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error initialising the logging system: " + e.toString());
        }

        // Create GUI
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    new DioscuriFrame(args);
                } catch (Exception e) {
                    logger.log(Level.WARNING, "Wrong command line option(s): " + e.toString());
                }
            }
        });

    }
}
