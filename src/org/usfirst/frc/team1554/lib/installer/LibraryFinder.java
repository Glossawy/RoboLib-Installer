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

import org.usfirst.frc.team1554.lib.collect.Array;
import org.usfirst.frc.team1554.lib.collect.Maps;
import org.usfirst.frc.team1554.lib.meta.LibVersion;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.function.BiFunction;

/**
 * Created by Glossawy on 7/12/2015.
 */
public class LibraryFinder {

    private static final String DEF_COMMENTS = "# This file enumerates dependencies that RoboLib will inject into the Eclipse Workspace as well\n" +
            "# as the WPI Classpath (so that the libraries move over to the RoboRIO on build).\n" +
            "#\n" +
            "# The format is as follows:\n" +
            "# someVarName=fileNameWithoutExtension:isRequired?\n" +
            "#\n" +
            "# Since this is PRIMARILY used for RoboLib-related projects the Source jars are automatically searched for\n" +
            "# by assuming the filename for Source jars is fileNameWithoutExtension-sources. Whatever is used for 'someVarName'\n" +
            "# can be accessed later using ${SEEK:previouslyUsedVarName} as seen with RoboLib-MemAccess.\n" +
            "#\n" +
            "# Some variables are pre-provided as 'built-ins' such as LIBNAME and VERSION which are substituted at runtime.\n" +
            "#\n" +
            "# Built-Ins Supported: LIBNAME, VERSION, SEEK:<varname>";

    private static final Path DEP_FILE = Paths.get("dependencies.properties");
    private static final Map<String, String> GLOBAL_VARS = Maps.newHashMap();
    private static final Map<String, BiFunction<LibraryFinder, String, String>> GLOBAL_FUNC = Maps.newHashMap();
    private static final String[][] REQUIRED_DEPS = {
            {LibVersion.NAME + "-Core", "${LIBNAME}-${VERSION}:true"},
            {LibVersion.NAME + "-MemAccess", "${SEEK:RoboLib-Core}-MemAccess:false"}
    };

    static {
        GLOBAL_VARS.put("LIBNAME", LibVersion.NAME);
        GLOBAL_VARS.put("VERSION", LibVersion.VERSION);
        GLOBAL_FUNC.put("SEEK", (finder, str) -> finder.variables.get(str));
    }

    private final Properties properties;
    private final Array<Library> dependencies = Array.of(true, 2, Library.class);
    private final Map<String, String> variables = Maps.newHashMap(GLOBAL_VARS);
    private final Map<String, BiFunction<LibraryFinder, String, String>> functions = Maps.newHashMap(GLOBAL_FUNC);

    private final Path rootSearchDir;

    public LibraryFinder(Path projectDir) {
        properties = new Properties();
        rootSearchDir = projectDir;

        try {
            for (String[] required : REQUIRED_DEPS)
                if (!properties.containsKey(required[0]))
                    properties.setProperty(required[0], required[1]);

            if (!Files.exists(DEP_FILE)) {
                Files.createFile(DEP_FILE);
                writeDefaultDependencyFile();
            } else {
                properties.load(Files.newInputStream(DEP_FILE));
            }
        } catch (IOException e) {
            throw new IORuntimeException("Failed to Load Dependency File!", e);
        }

        processDependencies();
    }

    public boolean addDependency(String displayName, String filename, boolean required) {
        Library lib = new Library(filename, rootSearchDir, required);

        if (dependencies.contains(lib, false))
            return false;

        dependencies.add(lib);
        variables.put(displayName, filename);
        return true;
    }

    public Array<Library> getDependencies() {
        return new Array<>(dependencies);
    }

    public void setVariable(String varname, String value, boolean global) {
        variables.put(varname, value);

        if (global)
            GLOBAL_VARS.put(varname, value);
    }

    public void setFunction(String funcname, BiFunction<LibraryFinder, String, String> function, boolean global) {
        functions.put(funcname, function);

        if (global)
            GLOBAL_FUNC.put(funcname, function);
    }

    public String getVariable(String varname) {
        return variables.get(varname);
    }

    public BiFunction<LibraryFinder, String, String> getFunction(String funcname) {
        return functions.get(funcname);
    }

    public boolean isVariableSet(String varname) {
        return variables.containsKey(varname) && variables.get(varname) != null;
    }

    public boolean isFunctionAvailable(String funcname) {
        return functions.containsKey(funcname) && functions.get(funcname) != null;
    }

    public void processDependencies() {
        Set<String> keys = properties.stringPropertyNames();

        for (String key : keys) {
            String entry = properties.getProperty(key);
            String nameTemplate = entry.substring(0, entry.lastIndexOf(':'));
            String required = entry.substring(entry.lastIndexOf(':') + 1);

            String libFilename = processTemplate(nameTemplate) + ".jar";
            boolean libRequired = ("true".equalsIgnoreCase(required) || "false".equalsIgnoreCase(required)) && "true".equalsIgnoreCase(required);

            if (!addDependency(key, libFilename, libRequired))
                System.err.println("Failed to add '" + libFilename + "' dependency! Already exists.");
        }
    }

    private String processTemplate(String templateStr) {
        StringBuilder template = new StringBuilder(templateStr);

        int startIdx, endIdx;
        while ((startIdx = template.indexOf("${")) != -1) {
            if ((endIdx = template.indexOf("}", startIdx)) == -1)
                throw new RuntimeParsingException("Template Variable is Missing Closing '}'!");

            String varname = template.substring(startIdx + 2, endIdx);

            if (variables.containsKey(varname)) {
                template.replace(startIdx, endIdx + 1, variables.get(varname));
            } else if (variables.containsKey(varname)) {
                int pIdx;
                if ((pIdx = varname.indexOf(':')) == -1)
                    throw new RuntimeParsingException("Function Call missing Argument after ':' at \"" + varname + "\" in dependencies file!");

                template.replace(startIdx, endIdx + 1, functions.get(varname).apply(this, varname.substring(pIdx + 1)));
            } else
                throw new RuntimeParsingException("Variable Name/Function Name Not tied to any existing Function or Variable!: '" + varname + "' was requested.");
        }

        return template.toString();
    }

    private void writeDefaultDependencyFile() throws IOException {
        StringBuilder sb = new StringBuilder(DEF_COMMENTS);
        sb.append('\n');

        for (String[] pair : REQUIRED_DEPS)
            sb.append(pair[0]).append('=').append(pair[1]).append('\n');

        Files.write(DEP_FILE, sb.toString().getBytes(StandardCharsets.UTF_8));
    }

}
