/*==================================================================================================
 RoboLib - An Expansion and Improvement Library for WPILibJ
 Copyright (C) 2015  Glossawy

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.

 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 =================================================================================================*/


package org.usfirst.frc.team1554.lib.installer;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.usfirst.frc.team1554.lib.collect.Array;
import org.usfirst.frc.team1554.lib.installer.FXOptionPane.IconType;
import org.usfirst.frc.team1554.lib.meta.LibVersion;
import org.usfirst.frc.team1554.lib.util.OS;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * Primary Class for Library Installation (handles .classpath and build.properties editing) <br />
 * <br />
 * Currently Supports installing The Main Library and the MemAccess Module
 *
 * @author Glossawy
 */
public final class LibInstaller extends Application {

    private static final String LIB_FILENAME = LibVersion.NAME.toLowerCase() + "-" + LibVersion.VERSION + ".jar";
    private static final String WPI_VERSION_REGEX = "\\$\\{version\\}";

    private static final FileAttribute<Set<PosixFilePermission>> STANDARD_PERMISSIONS;

    static {
        Set<PosixFilePermission> permits = PosixFilePermissions.fromString("rw-rw-rw-");
        STANDARD_PERMISSIONS = PosixFilePermissions.asFileAttribute(permits);
    }

    private ProgressDisplay progDisplay;

    @Override
    public void start(Stage primaryStage) throws IOException {
        // Confirm Installation
        displayAwarenessMessage();
        if (!FXOptionPane.showConfirmation(primaryStage, "Proceed with Installation?", "Install " + LibVersion.NAME + " v" + LibVersion.VERSION + "?", FXOptionPane.OptionType.YES_NO, IconType.CONFIRM)) {
            primaryStage.close();
            return;
        }

        // Set up Progress Display
        progDisplay = new ProgressDisplay(500, 50);
        primaryStage.setScene(progDisplay);
        primaryStage.setTitle(LibVersion.NAME + " v" + LibVersion.VERSION + " Installation");
        primaryStage.centerOnScreen();
        primaryStage.show();

        try {
            // Locate Project Directory and Library Jar File
            // Through the power of Java NIO!
            progDisplay.setInfoText("Locating Project Directory and Library File...");
            Path projectDir = getProjectDirectory(primaryStage);

            // Get Dependencies
            Array<Library> libDependencies = new LibraryFinder(projectDir).getDependencies();

            progDisplay.setProgress(10);
            progDisplay.setInfoText("Retrieving Necessary .classpath and .properties files...");
            Path wpilibProperties = getWPIProperties(projectDir);

            if (!Files.exists(wpilibProperties, LinkOption.NOFOLLOW_LINKS))
                throw new MissingRequirementException("No WPILib build.properties Found! Tried: " + wpilibProperties.toString());

            progDisplay.setProgress(20);
            progDisplay.setInfoText("Adding " + LibVersion.NAME + " to WPILib classpath variable...");

            // Set Classpath Property in wpilib build.properties
            for (Library lib : libDependencies)
                if (lib.isAvailable())
                    modifyClasspathWPI(wpilibProperties, projectDir.relativize(lib.getLibraryFile().toAbsolutePath()));

            try {
                // Inject Library as a dependency in .classpath
                // <classpathentry kind="lib" path="<libpath>" sourcepath="<libsrcpath>" />
                progDisplay.setProgress(50);
                progDisplay.setInfoText("Generating new .classpath XML Data...");

                Path classpathFile = projectDir.resolve(".classpath");
                if (Files.exists(classpathFile, LinkOption.NOFOLLOW_LINKS)) {
                    for (Library lib : libDependencies)
                        if (lib.isAvailable())
                            injectDependencyEclipseClasspathXML(lib, classpathFile);
                } else
                    FXOptionPane.showBlockingMessage("No .classpath Found!", "Could not automatically modify Eclipse's .classpath file.\nThis can be ignored if not using Eclipse.\n\nOtherwise, link the library manually.", IconType.WARNING);
            } catch (ParserConfigurationException | SAXException e) {
                throw new RuntimeParsingException("Failed to Parse .classpath XML!", e);
            } catch (TransformerException e) {
                throw new IORuntimeException("Failed to transform and write out new XML Graph to .classpath!", e);
            }

            progDisplay.setProgress(100);
            progDisplay.setInfoText("Finished!");
            FXOptionPane.showMessage("Finished!");
        } catch (Exception e) {
            progDisplay.setInfoText("ERROR -- " + e.getMessage());
            FXOptionPane.showBlockingMessage(primaryStage, "Application has Errored!", assembleErrorMessage(e), IconType.ERROR);
            createErrorFile(e);
        }

        primaryStage.close();
    }

    Path getWPIProperties(Path project) throws IOException {
        Path buildFile = project.resolve("build.xml");
        String wpiVersion = "current";
        String wpiPath = "/wpilib/java/${version}/ant/build.properties";

        if (!Files.exists(buildFile, LinkOption.NOFOLLOW_LINKS))
            throw new MissingRequirementException("No build.xml found in project!");

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setIgnoringComments(true);
            Document xml = factory.newDocumentBuilder().parse(buildFile.toFile());
            NodeList nodes = xml.getElementsByTagName("property");

            boolean versionRetrieved = false;
            boolean pathRetrieved = false;
            for (int i = 0; i < nodes.getLength() && (!versionRetrieved || !pathRetrieved); i++) {
                Node cur = nodes.item(i);
                NamedNodeMap map = cur.getAttributes();

                Node fileNode = map.getNamedItem("file");

                if (fileNode.getNodeValue().endsWith("wpilib.properties")) {
                    int index = fileNode.getNodeValue().indexOf('/');
                    Path toVersionProps = Paths.get(System.getProperty("user.home"), fileNode.getNodeValue().substring(index));

                    Properties props = new Properties();
                    props.load(Files.newInputStream(toVersionProps));
                    wpiVersion = props.getProperty("version");
                    versionRetrieved = true;
                } else if (fileNode.getNodeValue().endsWith("build.properties") && fileNode.getNodeValue().contains("/wpilib/")) {
                    int index = fileNode.getNodeValue().indexOf('/');
                    wpiPath = fileNode.getNodeValue().substring(index);
                    pathRetrieved = true;
                }
            }

            return Paths.get(System.getProperty("user.home"), wpiPath.replaceAll(WPI_VERSION_REGEX, wpiVersion));
        } catch (SAXException | ParserConfigurationException e) {
            throw new IORuntimeException("Failed to Parse WPILib build.xml! Is it still ANT?", e);
        }
    }

    void modifyClasspathWPI(Path wpilibProperties, Path libRelative) throws IOException {
        List<String> lines = Files.readAllLines(wpilibProperties, StandardCharsets.UTF_8);
        boolean hasChange = false;

        for (int i = 0; i < lines.size(); i++) {
            String entry = lines.get(i);
            if (entry.startsWith("classpath")) {
                if (entry.contains(LibVersion.NAME.toLowerCase())) {
                    String eVal = entry.substring(entry.indexOf('=') + 1);
                    lines.set(i, "classpath=" + eVal + ":" + libRelative);
                    hasChange = true;
                } else if (hasChange) {
                    hasChange = false;
                }
            }
        }

        if (hasChange)
            Files.write(wpilibProperties, lines, StandardCharsets.UTF_8, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    void injectDependencyEclipseClasspathXML(Library lib, Path classpathFile) throws IOException, ParserConfigurationException, SAXException, TransformerException {
        Document classpathXml = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(classpathFile.toFile());
        Element libElement = createLibraryElement(classpathXml, lib.getLibraryFile(), lib.hasSourceFile() ? lib.getSourceFile() : null);

        progDisplay.setProgress(70);
        NodeList libNodes = classpathXml.getElementsByTagName("classpathentry");
        DOMSearchResult<Node> result = searchForLastElementOfKind(libNodes, "var");

        progDisplay.setProgress(90);
        progDisplay.setInfoText("Adding " + LibVersion.NAME + " as dependency and writing XML...");
        if (!result.roboLibFound || !lib.getLibraryFile().endsWith(LIB_FILENAME)) {
            libNodes.item(0).getParentNode().insertBefore(libElement, result.value.getNextSibling());

            Transformer transform = TransformerFactory.newInstance().newTransformer();
            transform.setOutputProperty(OutputKeys.INDENT, "yes");

            DOMSource source = new DOMSource(classpathXml);
            StreamResult res = new StreamResult(Files.newOutputStream(classpathFile));
            transform.transform(source, res);
        }
    }

    Element createLibraryElement(Document classpathXml, Path libFile, Path srcFile) {
        Element libElement = classpathXml.createElement("classpathentry");
        libElement.setAttribute("kind", "lib");
        libElement.setAttribute("path", libFile.toAbsolutePath().toString());
        if (srcFile != null && Files.exists(srcFile, LinkOption.NOFOLLOW_LINKS))
            libElement.setAttribute("sourcepath", srcFile.toAbsolutePath().toString());

        return libElement;
    }

    DOMSearchResult<Node> searchForLastElementOfKind(NodeList list, String kind) {
        Node last = null;
        boolean found = false;

        for (int i = 0; i < list.getLength(); i++) {
            Node cur = list.item(i);
            if (cur.getAttributes().getNamedItem("kind").getNodeValue().equals(kind))
                last = cur;
            if (cur.getAttributes().getNamedItem("path").getNodeValue().contains(LibVersion.NAME.toLowerCase()))
                found = true;
        }

        return new DOMSearchResult<>(last, found);
    }

    Path getProjectDirectory(Stage primaryStage) {
        DirectoryChooser chooser = new DirectoryChooser();

        chooser.setTitle(GUIRef.TITLE + " - Please Select Eclipse Project Directory");
        chooser.setInitialDirectory(new File("."));

        File f = chooser.showDialog(primaryStage);

        if (f == null) {
            boolean cancel = FXOptionPane.showConfirmation(primaryStage, "Confirmation", "Cancel Installation?", FXOptionPane.OptionType.YES_NO, IconType.CONFIRM);

            if (cancel) {
                primaryStage.close();
                System.exit(0);
            } else
                return getProjectDirectory(primaryStage);
        }
        Path dir = Paths.get(f.toURI());
        if (!isEclipseProject(dir))
            throw new MissingRequirementException(String.valueOf(dir) + " is not an eclipse project! No .classpath found!");

        return dir;
    }

    boolean isEclipseProject(Path dir) {
        return Files.exists(dir.resolve(".classpath"), LinkOption.NOFOLLOW_LINKS);
    }

    public static void startInstallation(String[] args) throws IllegalAccessException {
        try {
            // Access Check
            StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
            boolean found = false;
            for (int i = 0; i < stackTrace.length && !found; i++) {
                if (stackTrace[i].getClassName().equals(LibInstallerLauncher.class.getName()))
                    found = true;
            }

            if (!found)
                throw null;
        } catch (Exception e) {
            throw new IllegalAccessException("LibInstaller must be checked through LibInstallerLauncher!");
        }

        Application.launch(args);
        Platform.runLater(() -> Application.setUserAgentStylesheet(Application.STYLESHEET_CASPIAN));
    }

    private static String assembleErrorMessage(Exception t) {
        final StackTraceElement[] stackTrace = t.getStackTrace();
        final StringBuilder message = new StringBuilder();
        final String separator = "===\n";
        final Throwable cause = t.getCause();

        message.append("Exception of type ").append(t.getClass().getName()).append('\n');
        message.append("Message: ").append(t.getMessage()).append('\n');
        message.append(separator);
        message.append("   ").append(stackTrace[0]).append('\n');

        for (int i = 1; i < stackTrace.length; i++) {
            message.append(" \t").append(stackTrace[i]).append('\n');
        }

        if (cause != null) {
            final StackTraceElement[] causeTrace = cause.getStackTrace();
            message.append(" \t\t").append("Caused by ").append(cause.getClass().getName()).append('\n');
            message.append(" \t\t").append("Because: ").append(cause.getMessage()).append('\n');
            message.append(" \t\t   ").append(causeTrace[0]).append('\n');
            message.append(" \t\t \t").append(causeTrace[2]).append('\n');
            message.append(" \t\t \t").append(causeTrace[3]);
        }

        return message.toString();
    }

    private static void createErrorFile(Exception e) {
        Path path = Paths.get("robolib-install-error.log");

        try {
            if (!Files.exists(path)) {
                if (OS.get() == OS.UNIX)
                    Files.createFile(path, STANDARD_PERMISSIONS);
                else
                    Files.createFile(path);
            }

            e.printStackTrace(new PrintWriter(Files.newBufferedWriter(path, StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING)));
        } catch (Exception ignore) {
        }

        e.printStackTrace();
    }

    static void displayAwarenessMessage() {
        String msg = "Be aware that the following requirements must be met:\n" +
                "\t 1. The Library file is either in the project directory or in some sub-directory\t\n" +
                "\t 2. There is a build.xml in the project directory that was auto-generated\t\n" +
                "\t 3. There is a wpilib folder in your User Home Directory (" + System.getProperty("user.home") + ")\t\n";

        FXOptionPane.builder(msg)
                .toWarning()
                .makeBlocking()
                .makeOkButton(FXOptionPane.DEFAULT_CLOSE_ACTION)
                .setModality(Modality.APPLICATION_MODAL)
                .setWidth(600)
                .setHeight(200)
                .build();
    }

    static class DOMSearchResult<T extends Node> {
        public final T value;
        public final boolean roboLibFound;

        public DOMSearchResult(T val, boolean roboLibFound) {
            this.value = val;
            this.roboLibFound = roboLibFound;
        }
    }
}
