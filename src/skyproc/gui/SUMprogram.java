/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import lev.Ln;
import lev.debug.LDebug;
import lev.gui.*;
import lev.gui.resources.LFonts;
import skyproc.*;
import skyproc.exceptions.BadRecord;

/**
 * SUM - SkyProc Unified Manager<br> This is the main program that hooks
 * together various SkyProc patchers and streamlines their patching processing.
 *
 * @author Justin Swanson
 */
public class SUMprogram implements SUM {

    ArrayList<String> exclude = new ArrayList<>(2);
    ArrayList<PatcherLink> links = new ArrayList<>();
    // GUI
    SPMainMenuPanel mmenu;
    HookMenu hookMenu;
    OptionsMenu optionsMenu;
    LScrollPane hookMenuScroll;
    LSaveFile saveFile = new SUMsave();
    Color teal = new Color(75, 164, 134);
    Color green = new Color(54, 154, 31);
    Color grey = new Color(230, 230, 230);
    Font settingFont = new Font("Serif", Font.BOLD, 14);
    int scrollOffset = 100;
    int patcherNumber = 0;
    BufferedImage collapsedSetting;
    BufferedImage openSetting;
    LCheckBox forceAllPatches;

    /**
     * Main function that starts the program and GUI.
     *
     * @param args "-test" Opens up the SkyProc tester program instead of SUM
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
	try {
	    if (handleArgs(args)) {
		SUMprogram sum = new SUMprogram();
		sum.runProgram();
	    }
	} catch (Exception e) {
	    // If a major error happens, print it everywhere and display a message box.
	    System.err.println(e.toString());
	    SPGlobal.logException(e);
	    JOptionPane.showMessageDialog(null, "There was an exception thrown during program execution: '" + e + "'  Check the debug logs.");
	    SPGlobal.closeDebug();
	}
    }

    static boolean handleArgs(String[] args) {
	ArrayList<String> argsList = new ArrayList<>();
	for (String s : args) {
	    argsList.add(s.toUpperCase());
	}
	if (argsList.contains("-TESTCOPY")) {
	    SkyProcTester.runTests(3);
	    return false;
	}
	if (argsList.contains("-TESTIMPORT")) {
	    SkyProcTester.runTests(2);
	    return false;
	}
	if (argsList.contains("-TEST")) {
	    SkyProcTester.runTests(1);
	    return false;
	}
	if (argsList.contains("-EMBEDDEDSCRIPTGEN")) {
	    SkyProcTester.parseEmbeddedScripts();
	    return false;
	}
	return true;
    }

    void runProgram() throws InstantiationException, IllegalAccessException {
	compileExcludes();

	openDebug();
	saveFile.init();
	getHooks();
	openGUI();
	initLinkGUIs();
    }

    void compileExcludes() {
	exclude.add("SKYPROC UNIFIED MANAGER.JAR");
	exclude.add("SKYPROC.JAR");

	try {
	    BufferedReader in = new BufferedReader(new FileReader("Files/Block Jars.txt"));
	    String read;
	    while (in.ready()) {
		read = in.readLine();
		if (read.indexOf("//") != -1) {
		    read = read.substring(0, read.indexOf("//"));
		}
		read = read.trim().toUpperCase();
		if (!read.equals("")) {
		    exclude.add(read.toUpperCase());
		}
	    }
	} catch (IOException ex) {
	    SPGlobal.logError("Block Jars", "Failed to locate or read 'Block Jars.txt'");
	}
    }

    void openDebug() {
	SPGlobal.createGlobalLog();
	SPGlobal.debugModMerge = false;
	SPGlobal.debugExportSummary = false;
	SPGlobal.debugBSAimport = false;
	SPGlobal.debugNIFimport = false;
	LDebug.timeElapsed = true;
	LDebug.timeStamp = true;
	SPGlobal.newSpecialLog(SUMlogs.JarHook, "Jar Hooking.txt");
    }

    void openGUI() {
	mmenu = new SPMainMenuPanel();
	mmenu.addLogo(this.getLogo());

	hookMenu = new HookMenu(mmenu);
	mmenu.addMenu(hookMenu, green, false, saveFile, null);

	optionsMenu = new OptionsMenu(mmenu);
	mmenu.addMenu(optionsMenu, green, false, saveFile, null);


	try {
	    collapsedSetting = ImageIO.read(SUM.class.getResource("Open Settings Collapsed.png"));
	    openSetting = ImageIO.read(SUM.class.getResource("Open Settings.png"));
	} catch (IOException ex) {
	    SPGlobal.logException(ex);
	}

	SUMGUI.open(this, new String[0]);
	SwingUtilities.invokeLater(new Runnable() {
	    @Override
	    public void run() {
		SUMGUI.patchNeededLabel.setText("");
		SUMGUI.patchNeededLabel.setLocation(-1000, -1000);
		SUMGUI.forcePatch.setLocation(-1000, -1000);
		forceAllPatches = new LCheckBox("Force All Patches", SUMGUI.SUMSmallFont, Color.GRAY);
		forceAllPatches.setLocation(SUMGUI.rightDimensions.x + 10, SUMGUI.cancelPatch.getY() + SUMGUI.cancelPatch.getHeight() / 2 - forceAllPatches.getHeight() / 2);
		forceAllPatches.setOffset(-4);
		forceAllPatches.addMouseListener(new MouseListener() {
		    @Override
		    public void mouseClicked(MouseEvent e) {
		    }

		    @Override
		    public void mousePressed(MouseEvent e) {
		    }

		    @Override
		    public void mouseReleased(MouseEvent e) {
		    }

		    @Override
		    public void mouseEntered(MouseEvent e) {
			SUMGUI.helpPanel.setContent("This will force each patcher to create a patch, even if "
				+ "it doesn't think it needs to.  Use this if you want to forcibly "
				+ "remake all patches for any reason.");
			SUMGUI.helpPanel.setTitle("Force All Patches");
			SUMGUI.helpPanel.hideArrow();
			SUMGUI.helpPanel.setDefaultPos();
		    }

		    @Override
		    public void mouseExited(MouseEvent e) {
		    }
		});
		SUMGUI.singleton.add(forceAllPatches);
	    }
	});
    }

    void initLinkGUIs() {
	ArrayList<PatcherLink> linkTmp = new ArrayList<>(links);
	PatcherLink last = null;
	int height = 0;
	for (PatcherLink link : linkTmp) {
	    try {
		link.setup();
		if (last != null) {
		    link.setLocation(link.getX(), last.getY() + last.getHeight() + 15);
		}
		last = link;
		hookMenu.hookMenu.add(link);
		height = link.getY() + link.getHeight();
		hookMenu.revalidate();
	    } catch (Exception ex) {
		SPGlobal.logException(ex);
		links.remove(link);
	    }
	}
	hookMenu.hookMenu.setPreferredSize(new Dimension(SUMGUI.middleDimensions.width, height + 25));
    }

    void getHooks() {
	ArrayList<File> jars = findJars(new File("../"));

	for (File jar : jars) {
	    try {
		SPGlobal.logMain("Jar Load", "Loading jar " + jar);
		ArrayList<Class> classes = Ln.loadClasses(jar, true);
		for (Class c : classes) {
		    //Skip other skyproc SUM classes
		    if (c.equals(getClass())) {
			continue;
		    }
		    try {
			Object tester = c.newInstance();
			if (tester instanceof SUM) {
			    SPGlobal.logMain("Jar Load", "   Added jar " + jar);
			    PatcherLink newLink = new PatcherLink((SUM) c.newInstance(), jar);
			    if (!links.contains(newLink)) {
				links.add(newLink);
			    }
			    break;
			}
		    } catch (Throwable ex) {
			SPGlobal.logSpecial(SUMlogs.JarHook, "Loading class", "Skipped " + c + ": " + ex.getMessage());
		    }
		}
	    } catch (Throwable ex) {
		SPGlobal.logSpecial(SUMlogs.JarHook, "Loading jar", "Skipped jar " + jar + ": " + ex.getMessage());
	    }
	}
    }

    ArrayList<File> findJars(File dir) {
	ArrayList<File> files = Ln.generateFileList(dir, false);
	ArrayList<File> out = new ArrayList<>();
	for (File f : files) {
	    if (f.getName().toUpperCase().endsWith(".JAR")
		    && !exclude.contains(f.getName().toUpperCase())) {
		out.add(f);
	    }
	}
	return out;
    }

    /**
     * Returns the modlisting used for the exported patch.
     *
     * @return
     */
    @Override
    public ModListing getListing() {
	return new ModListing("SUM", false);
    }

    /**
     *
     * @return True if any hook needs patching.
     */
    @Override
    public boolean needsPatching() {
	return true;
    }

    /**
     *
     * @return
     */
    @Override
    public ArrayList<ModListing> requiredMods() {
	return new ArrayList<>(0);
    }

    @Override
    public String description() {
	throw new UnsupportedOperationException("Not supported yet.");
    }

    // Internal Classes
    class HookMenu extends SPSettingPanel {

	JPanel hookMenu;

	HookMenu(SPMainMenuPanel parent_) {
	    super(parent_, "Patcher List", grey);
	    initialize();
	}

	@Override
	protected final void initialize() {
	    super.initialize();

	    hookMenu = new JPanel();
	    hookMenu.setOpaque(false);
	    hookMenu.setLayout(null);

	    hookMenuScroll = new LScrollPane(hookMenu);
	    hookMenuScroll.setSize(SUMGUI.middleDimensions.width,
		    SUMGUI.middleDimensions.height - scrollOffset);
	    hookMenuScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
	    hookMenuScroll.setLocation(0, scrollOffset);
	    hookMenuScroll.getVerticalScrollBar().setUnitIncrement(20);
	    Add(hookMenuScroll);
	}
    }

    class OptionsMenu extends SPSettingPanel {

	LCheckBox runBoss;
	LCheckBox mergePatches;

	OptionsMenu(SPMainMenuPanel parent_) {
	    super(parent_, "SUM Options", grey);
	}

	@Override
	protected final void initialize() {
	    super.initialize();

	    mergePatches = new LCheckBox("Merge Patches", settingFont, SUMGUI.light);
	    mergePatches.addShadow();
	    mergePatches.tie(SUMSettings.MERGE_PATCH, saveFile, SUMGUI.helpPanel, true);
	    setPlacement(mergePatches);
	    AddSetting(mergePatches);

	    runBoss = new LCheckBox("Run BOSS", settingFont, SUMGUI.light);
	    runBoss.addShadow();
	    runBoss.tie(SUMSettings.RUN_BOSS, saveFile, SUMGUI.helpPanel, true);
	    setPlacement(runBoss);
	    AddSetting(runBoss);

	    alignRight();

	}
    }

    class PatcherLink extends LHelpComponent {

	LImagePane logo;
	LLabel title;
	JCheckBox cbox;
	SUM hook;
	File path;
	JPanel menu;
	LImagePane setting;

	PatcherLink(final SUM hook, final File path) {
	    super(hook.getName());
	    this.hook = hook;
	    this.path = path;
	}

	final void setup() {
	    setupGUI();
	}

	final void setupGUI() {

	    Component using = null;

	    cbox = new JCheckBox();
	    cbox.setSize(cbox.getPreferredSize());
	    cbox.setOpaque(false);
	    cbox.setVisible(true);
	    cbox.setSelected(!saveFile.getStrings(SUMSettings.DISABLED).contains(getName().toUpperCase()));

	    int desiredMargin = 15;
	    int desiredWidth = SUMGUI.middleDimensions.width - desiredMargin * 2;
	    int width = cbox.getWidth() + 10;

	    if (hook.hasLogo()) {
		try {
		    logo = new LImagePane(hook.getLogo());
		    if (logo.getWidth() + width > desiredWidth) {
			logo.setMaxSize(desiredWidth - width, 0);
		    }
		    using = logo;
		    add(logo);
		} catch (IOException ex) {
		    SPGlobal.logException(ex);
		    logo = null;
		}
	    }
	    if (logo == null) {
		title = new LLabel(hook.getName(), SUMGUI.SUMmainFont.deriveFont((float) 25), hook.getHeaderColor());
		title.addShadow();
		using = title;
		add(title);
	    }

	    width += using.getWidth();
	    using.addMouseListener(new LinkClick());
	    cbox.setLocation(desiredMargin, using.getHeight() / 2 - cbox.getHeight() / 2);
	    add(cbox);
	    using.setLocation(cbox.getX() + cbox.getWidth() + 10, 0);

	    // Add settings
	    setting = new LImagePane(collapsedSetting);
	    setting.setLocation(SUMGUI.middleDimensions.width - 10 - setting.getWidth(), using.getHeight() / 2 - setting.getHeight() / 2);
	    setting.addMouseListener(new MouseListener() {
		@Override
		public void mouseClicked(MouseEvent e) {
		    ArrayList<String> args = new ArrayList<>();
		    args.add("java");
		    args.add("-jar");
		    args.add("-Xms500m");
		    args.add("-Xmx1000m");
		    args.add(path.getPath());
		    args.add("-NONEW");
		    args.add("-JUSTSETTINGS");
		    NiftyFunc.startProcess(new File(path.getParentFile().getPath() + "\\"), args.toArray(new String[0]));
		}

		@Override
		public void mousePressed(MouseEvent e) {
		}

		@Override
		public void mouseReleased(MouseEvent e) {
		}

		@Override
		public void mouseEntered(MouseEvent e) {
		    setting.setImage(openSetting);
		    setting.setLocation(SUMGUI.middleDimensions.width - 10 - setting.getWidth(), setting.getY());
		}

		@Override
		public void mouseExited(MouseEvent e) {
		    setting.setImage(collapsedSetting);
		    setting.setLocation(SUMGUI.middleDimensions.width - 10 - setting.getWidth(), setting.getY());
		}
	    });
	    add(setting, 0);
	    if (setting.getY() < 0) {
		setting.setLocation(setting.getX(), 0);
		using.setLocation(using.getX(), setting.getHeight() / 2 - using.getHeight() / 2);
		using = setting;
	    }

	    cbox.setLocation(desiredMargin, using.getHeight() / 2 - cbox.getHeight() / 2);
	    setSize(SUMGUI.middleDimensions.width, using.getHeight());

	    // Tie to help
	    SUMSettings tie = SUMSettings.values()[patcherNumber++];
	    saveFile.Add(tie, true, true);
	    saveFile.helpInfo.put(tie, hook.description());
	    this.linkTo(tie, saveFile, SUMGUI.helpPanel, true);
	    helpYoffset = scrollOffset;
	    setFollowPosition(false);
	}

	class LinkClick implements MouseListener {

	    @Override
	    public void mouseClicked(MouseEvent arg0) {
		cbox.setSelected(!cbox.isSelected());
	    }

	    @Override
	    public void mousePressed(MouseEvent arg0) {
	    }

	    @Override
	    public void mouseReleased(MouseEvent arg0) {
	    }

	    @Override
	    public void mouseEntered(MouseEvent arg0) {
	    }

	    @Override
	    public void mouseExited(MouseEvent arg0) {
	    }
	}

	boolean isActive() {
	    return cbox.isSelected();
	}

	@Override
	public String getName() {
	    return hook.getName();
	}

	@Override
	public int hashCode() {
	    int hash = 7;
	    hash = 83 * hash + Objects.hashCode(this.hook);
	    return hash;
	}

	@Override
	public boolean equals(Object obj) {
	    if (obj == null) {
		return false;
	    }
	    if (getClass() != obj.getClass()) {
		return false;
	    }
	    final PatcherLink other = (PatcherLink) obj;
	    return hook.getName().equalsIgnoreCase(other.hook.getName());
	}

	@Override
	protected void addHelpHandler(boolean hoverListener) {
	    cbox.addFocusListener(new HelpFocusHandler());
	    cbox.addMouseListener(new HelpMouseHandler());
	    if (title != null) {
		title.addMouseListener(new HelpMouseHandler());
	    }
	    if (logo != null) {
		logo.addMouseListener(new HelpMouseHandler());
	    }
	    setting.addMouseListener(new HelpMouseHandler());
	}
    }

    class SUMsave extends LSaveFile {

	SUMsave() {
	    super(SPGlobal.pathToInternalFiles);
	}

	@Override
	protected void initSettings() {
	    Add(SUMSettings.MERGE_PATCH, false, true);
	    Add(SUMSettings.DISABLED, new HashSet<String>(0), true);
	    Add(SUMSettings.RUN_BOSS, true, false);
	}

	@Override
	protected void initHelp() {
	    helpInfo.put(SUMSettings.MERGE_PATCH, "This will merge all of your SkyProc patches into one patch.  "
		    + "This helps if you're hitting the max number of mods.\n\n"
		    + "WARNING:  Existing savegames may break when switching this setting on/off.  It is "
		    + "recommended you start fresh savegames when adjusting this setting.");
	    helpInfo.put(SUMSettings.RUN_BOSS, "SUM will run BOSS before running the patchers to confirm that "
		    + "they are all in the correct load order.  It is highly recommended you leave this setting on.\n\n"
		    + "NOTE:  Be aware that BOSS reserves the right to change load ordering as it sees fit.  "
		    + "If it adjusts its load order and shuffles SkyProc patchers around "
		    + "to be in a different order, your savegame may or may not function with the new ordering.  This is "
		    + "most likely to occur if the SkyProc patcher is brand new, and hasn't been processed yet by BOSS.");
	}
    }

    enum SUMSettings {

	PATCHER1,
	PATCHER2,
	PATCHER3,
	PATCHER4,
	PATCHER5,
	PATCHER6,
	PATCHER7,
	PATCHER8,
	PATCHER9,
	PATCHER10,
	PATCHER11,
	PATCHER12,
	PATCHER13,
	PATCHER14,
	PATCHER15,
	PATCHER16,
	PATCHER17,
	PATCHER18,
	PATCHER19,
	PATCHER20,
	PATCHER21,
	PATCHER22,
	PATCHER23,
	PATCHER24,
	PATCHER25,
	PATCHER26,
	PATCHER27,
	PATCHER28,
	PATCHER29,
	PATCHER30,
	PATCHER31,
	PATCHER32,
	PATCHER33,
	PATCHER34,
	PATCHER35,
	PATCHER36,
	PATCHER37,
	PATCHER38,
	PATCHER39,
	PATCHER40,
	PATCHER41,
	PATCHER42,
	PATCHER43,
	PATCHER44,
	PATCHER45,
	PATCHER46,
	PATCHER47,
	PATCHER48,
	PATCHER49,
	PATCHER50,
	MERGE_PATCH,
	DISABLED,
	RUN_BOSS,;
    }

    enum SUMlogs {

	JarHook;
    }

    // SUM methods
    /**
     *
     * @return
     */
    @Override
    public String getName() {
	return "SkyProc Unified Manager";
    }

    /**
     *
     * @return
     */
    @Override
    public GRUP_TYPE[] dangerousRecordReport() {
	throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     *
     * @return
     */
    @Override
    public GRUP_TYPE[] importRequests() {
	return new GRUP_TYPE[0];
    }

    /**
     *
     * @return
     */
    @Override
    public boolean importAtStart() {
	return false;
    }

    /**
     *
     * @return
     */
    @Override
    public boolean hasStandardMenu() {
	return true;
    }

    /**
     *
     * @return
     */
    @Override
    public SPMainMenuPanel getStandardMenu() {
	return mmenu;
    }

    /**
     *
     * @return
     */
    @Override
    public boolean hasCustomMenu() {
	return false;
    }

    /**
     *
     * @return
     */
    @Override
    public JFrame openCustomMenu() {
	return null;
    }

    /**
     *
     * @return
     */
    @Override
    public boolean hasLogo() {
	return true;
    }

    /**
     *
     * @return
     */
    @Override
    public URL getLogo() {
	return SUMprogram.class.getResource("SUM program.png");
    }

    /**
     *
     * @return
     */
    @Override
    public boolean hasSave() {
	return true;
    }

    /**
     *
     * @return
     */
    @Override
    public LSaveFile getSave() {
	return saveFile;
    }

    /**
     *
     * @return
     */
    @Override
    public String getVersion() {
	return "1.0";
    }

    /**
     *
     * @return
     */
    @Override
    public Mod getExportPatch() {
	Mod patch = new Mod(getListing());
	patch.setFlag(Mod.Mod_Flags.STRING_TABLED, false);
	patch.setAuthor("Leviathan1753 and friends");
	return patch;
    }

    /**
     *
     * @return
     */
    @Override
    public Color getHeaderColor() {
	return teal;
    }

    /**
     * Runs all hooks onExit() function
     *
     * @param patchWasGenerated
     */
    @Override
    public void onExit(boolean patchWasGenerated) {
	Set<String> disabledLinks = saveFile.getStrings(SUMSettings.DISABLED);
	disabledLinks.clear();
	for (PatcherLink link : links) {
	    if (!link.isActive()) {
		disabledLinks.add(link.getName().toUpperCase());
	    }
	}
    }

    /**
     * Runs all hooks onStart() function
     */
    @Override
    public void onStart() {
    }

    /**
     *
     * @throws Exception
     */
    @Override
    public void runChangesToPatch() throws Exception {

	ArrayList<PatcherLink> activeLinks = getActiveLinks();
	SUMGUI.progress.setBar(0);
	SUMGUI.progress.setMax(activeLinks.size());

	handleNonExistantLinks(activeLinks);

	runBOSS();

	runEachPatcher(activeLinks);
    }

    ArrayList<PatcherLink> getActiveLinks() {
	ArrayList<PatcherLink> activeLinks = new ArrayList<>();
	for (PatcherLink l : links) {
	    if (l.isActive()) {
		activeLinks.add(l);
	    }
	}
	return activeLinks;
    }

    void handleNonExistantLinks(ArrayList<PatcherLink> activeLinks) throws IOException, BadRecord {
	ArrayList<ModListing> activeMods = SPImporter.getActiveModList();
	ArrayList<Mod> nonExistantPatchers = new ArrayList<>();
	for (PatcherLink link : activeLinks) {
	    Mod patcherMod = link.hook.getExportPatch();
	    if (!activeMods.contains(patcherMod.getInfo())) {
		nonExistantPatchers.add(patcherMod);
	    }
	}

	//Read in plugins.txt
	ArrayList<String> pluginsLines = new ArrayList<>();
	BufferedReader pluginsIn = new BufferedReader(new FileReader(SPGlobal.getPluginsTxt()));
	while (pluginsIn.ready()) {
	    String line = pluginsIn.readLine().trim();
	    if (!"".equals(line)) {
		pluginsLines.add(line);
	    }
	}
	pluginsIn.close();

	// Handle non-existant patchers
	for (Mod newPatcher : nonExistantPatchers) {
	    // Export tmp patch as a placeholder
	    newPatcher.export();
	    // Add listing to plugins.txt
	    pluginsLines.add(newPatcher.getName());
	}

	// Write out new plugins.txt
	BufferedWriter pluginsOut = new BufferedWriter(new FileWriter(SPGlobal.getPluginsTxt()));
	for (String line : pluginsLines) {
	    pluginsOut.write(line + "\n");
	}
	pluginsOut.close();
    }

    void runBOSS() {
	if (saveFile.getBool(SUMSettings.RUN_BOSS)) {
	    SUMGUI.progress.setStatus("Running BOSS");
	    File bossFolder = new File(WinRegistry.WinRegistry.getRegistryEntry("BOSS", "Installed Path"));
	    File bossExe = new File(bossFolder.getPath() + "\\BOSS.exe");
	    int response = JOptionPane.YES_OPTION;
	    if (bossExe.isFile()) {
		if (!NiftyFunc.startProcess(bossFolder, new String[]{bossExe.getPath(), "-s", "-g", "Skyrim"})) {
		    response = JOptionPane.showConfirmDialog(null, "BOSS failed to run. Do you want to continue?", "BOSS failed", JOptionPane.YES_NO_OPTION);
		}
	    } else {
		response = JOptionPane.showConfirmDialog(null, "BOSS could not be located.\n"
			+ "It is highly recommended you download BOSS so that it can be used.\n\n"
			+ "Do you want to continue patching without BOSS?", "Cannot locate BOSS", JOptionPane.YES_NO_OPTION);
	    }
	    if (response == JOptionPane.NO_OPTION) {
		SUMGUI.exitProgram(false, true);
	    }
	}
    }

    void runEachPatcher(ArrayList<PatcherLink> activeLinks) {
	SUMGUI.progress.setStatus("Running Patchers");
	for (int i = 0; i < activeLinks.size(); i++) {
	    PatcherLink link = activeLinks.get(i);
	    SUMGUI.progress.setStatus(i + 1, activeLinks.size(), "Running " + link.getName());
	    SPGlobal.logMain("Run Changes", "Running jar: " + link.path);
	    if (!link.isActive()) {
		SPGlobal.logMain("Run Changes", "Skipped jar because it was not selected: " + link.path);
	    } else if (runJarPatcher(link)) {
		SPGlobal.logMain("Run Changes", "Successfully ran jar: " + link.path);
	    } else {
		SPGlobal.logMain("Run Changes", "UNsuccessfully ran jar: " + link.path);
	    }
	    SUMGUI.progress.incrementBar();
	}
	SUMGUI.progress.done();
	SUMGUI.exitProgram(true, true);
    }

    boolean runJarPatcher(PatcherLink link) {
	ArrayList<String> args = new ArrayList<>();
	args.add("java");
	args.add("-jar");
	args.add("-Xms500m");
	args.add("-Xmx1000m");
	args.add(link.path.getPath());
	args.add("-GENPATCH");
	args.add("-NONEW");
	args.add("-NOMODSAFTER");
	if (forceAllPatches.isSelected()) {
	    args.add("-FORCE");
	}
	args.add("-PROGRESSLOCATION");
	args.add("-" + (SUMGUI.progress.getWidth() + 10));
	args.add("-" + 0);
	return NiftyFunc.startProcess(new File(link.path.getParentFile().getPath() + "\\"), args.toArray(new String[0]));
    }
}
