/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.unibi.techfak.bibiserv.util.codegen;

import de.unibi.techfak.bibiserv.util.codegen.logfilter.VerboseOutputFilter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.stream.Collectors;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * CodeGen Main class
 *
 * offers cmdline interface for CodeGen.
 *
 *
 * @author Jan Krüger - jkrueger(at)cebitec.uni-bielefeld.de
 *
 */
public class Main {

    public static final Logger log = LoggerFactory.getLogger(Main.class);
    
    public static Properties config = new Properties();

    public enum RESOURCETYPE {
        isFile, isDirectory
    };

    public static void main(String[] args) {
        // check &   validate cmdline options
        OptionGroup opt_g = getCMDLineOptionsGroups();
        Options opt = getCMDLineOptions();
        opt.addOptionGroup(opt_g);

        CommandLineParser cli = new DefaultParser();
        try {
            CommandLine cl = cli.parse(opt, args);

            if (cl.hasOption("v")) {
                VerboseOutputFilter.SHOW_VERBOSE = true;
            }

            switch (opt_g.getSelected()) {
                case "V":
                    try {
                        URL jarUrl = Main.class.getProtectionDomain().getCodeSource().getLocation();
                        String jarPath = URLDecoder.decode(jarUrl.getFile(), "UTF-8");
                        JarFile jarFile = new JarFile(jarPath);
                        Manifest m = jarFile.getManifest();
                        StringBuilder versionInfo = new StringBuilder();
                        for (Object key : m.getMainAttributes().keySet()) {
                            versionInfo.append(key).append(":").append(m.getMainAttributes().getValue(key.toString())).append("\n");
                        }
                        System.out.println(versionInfo.toString());
                    } catch (Exception e) {
                        log.error("Version info could not be read.");
                    }
                    break;
                case "h":
                    HelpFormatter help = new HelpFormatter();
                    String header = ""; //TODO: missing infotext 
                    StringBuilder footer = new StringBuilder("Supported configuration properties :");
                    help.printHelp("bibigrid -h | -V | -g  [...]", header, opt, footer.toString());
                    break;
                case "g":
                    // target dir
                    if (cl.hasOption("t")) {   
                        File target = new File(cl.getOptionValue("t"));
                        if (target.isDirectory() && target.canExecute() && target.canWrite()) {
                            config.setProperty("target.dir",cl.getOptionValue("t"));
                        } else {
                            log.error("Target dir '{}' is inaccessible!",cl.getOptionValue("t"));
                            break;
                        } 
                    } else {
                        config.setProperty("target.dir",System.getProperty("java.io.tmpdir"));
                    }
                    
                    // project dir
                    if (cl.hasOption("p")) {
                        File project = new File(cl.getOptionValue("p"));
                         if (project.isDirectory() && project.canExecute() && project.canWrite()) {
                            config.setProperty("project.dir",cl.getOptionValue("p"));
                        } else {
                            log.error("Project dir '{}' is inaccessible!",cl.getOptionValue("p"));
                            break;
                        }                     
                    } 
                    
                    generateAppfromXML(cl.getOptionValue("g"));
                    break;
            }
        } catch (ParseException e) {
            log.error("ParseException occurred while parsing cmdline arguments!\n{}", e.getLocalizedMessage());
        }

    }

    private static boolean generateAppfromXML(String fn) {

        long starttime = System.currentTimeMillis();

        // fn must not be null or empty
        if (fn == null || fn.isEmpty()) {
            log.error("Empty filename!");
            return false;
        }
        // fn must be a valid file and readable
        File runnableitem = new File(fn);
        if (!runnableitem.exists() || !runnableitem.canRead()) {
            log.error("{} doesn't exists or can't be read! ", fn);
            return false;
        }
        // load as xml file, validate it, and ...
        Document doc;
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            //dbf.setValidating(true);
            DocumentBuilder db = dbf.newDocumentBuilder();
            doc = db.parse(runnableitem);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            log.error("{} occured: {}", e.getClass().getSimpleName(), e.getLocalizedMessage());
            return false;
        }
        // extract project id, name  and version from it.
        String projectid = doc.getDocumentElement().getAttribute("id");
        if ((projectid == null) || projectid.isEmpty()) {
            log.error("Missing project id in description file!");
            return false;
        }
        String projectname;
        try {
            projectname = doc.getElementsByTagNameNS("bibiserv:de.unibi.techfak.bibiserv.cms", "name").item(0).getTextContent();
        } catch (NullPointerException e) {
            log.error("Missing project name in description file!");
            return false;
        }

        String projectversion = "unknown";
        try {
            projectversion = doc.getElementsByTagNameNS("bibiserv:de.unibi.techfak.bibiserv.cms", "version").item(0).getTextContent();
        } catch (NullPointerException e) {
            log.warn("Missing project version in description file!");

        }

        File projectdir = new File( config.getProperty("project.dir",config.getProperty("target.dir")+"/"+projectid));

        mkdirs(projectdir + "/src/main/java");
        mkdirs(projectdir + "/src/main/config");
        mkdirs(projectdir + "/src/main/libs");
        mkdirs(projectdir + "/src/main/pages");
        mkdirs(projectdir + "/src/main/resources");
        mkdirs(projectdir + "/src/main/downloads");

        // place runnableitem in config dir
        try {
            Files.copy(runnableitem.toPath(), new File(projectdir + "/src/main/config/runnableitem.xml").toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            log.error("{} occurred : {}", e.getClass().getSimpleName(), e.getLocalizedMessage());
            return false;
        }

        // copy files from SKELETON to projectdir and replace wildcard expression
        String[] SKELETON_INPUT_ARRAY = {"/pom.xml", "/src/main/config/log4j-tool.properties"};
        String SKELETON_INPUT = null;
        try {
            for (int c = 0; c < SKELETON_INPUT_ARRAY.length; c++) {
                SKELETON_INPUT = SKELETON_INPUT_ARRAY[c];

                InputStream in = Main.class.getResourceAsStream("/SKELETON" + SKELETON_INPUT);
                if (in == null) {
                    throw new IOException();
                }

                CopyAndReplace(in, new FileOutputStream(new File(projectdir, SKELETON_INPUT)), projectid, projectname, projectversion);
            }

        } catch (IOException e) {
            log.error("Exception occurred while calling 'copyAndReplace(/SKELETON{},{}/{},{},{},{})'", SKELETON_INPUT, projectdir, SKELETON_INPUT, projectid, projectname, projectversion);
            return false;
        }

        log.info("Empty project created! ");

        try {
            // _base 

            generate(CodeGen_Implementation.class, runnableitem, projectdir);
            log.info("Implementation generated!");

            generate(CodeGen_Implementation_Threadworker.class, runnableitem, projectdir);
            log.info("Implementation_Threadworker generated!");

            generate(CodeGen_Utilities.class, runnableitem, projectdir);
            log.info("Utilities generated!");

            generate(CodeGen_Common.class, runnableitem, projectdir, "/templates/common", RESOURCETYPE.isDirectory);

            log.info("Common generated!");

            // _REST
            generate(CodeGen_REST.class, runnableitem, projectdir);
            generate(CodeGen_REST_general.class, runnableitem, projectdir);

            log.info("REST generated!");

            //_HTML
            generate(CodeGen_WebSubmissionPage.class, runnableitem, projectdir);
            generate(CodeGen_WebSubmissionPage_Input.class, runnableitem, projectdir);
            generate(CodeGen_WebSubmissionPage_Param.class, runnableitem, projectdir);
            generate(CodeGen_WebSubmissionPage_Result.class, runnableitem, projectdir);
            generate(CodeGen_WebSubmissionPage_Visualization.class, runnableitem, projectdir);
            generate(CodeGen_WebSubmissionPage_Formatchooser.class, runnableitem, projectdir);
            generate(CodeGen_WebSubmissionPage_Resulthandler.class, runnableitem, projectdir);
            generate(CodeGen_WebSubmissionBean_Function.class, runnableitem, projectdir);
            generate(CodeGen_Session_Reset.class, runnableitem, projectdir);
            generate(CodeGen_WebSubmissionBean_Controller.class, runnableitem, projectdir);
            generate(CodeGen_WebSubmissionBean_Input.class, runnableitem, projectdir);
            generate(CodeGen_WebSubmissionBean_Param.class, runnableitem, projectdir);
            generate(CodeGen_WebSubmissionBean_Result.class, runnableitem, projectdir);
            generate(CodeGen_WebSubmissionBean_Resulthandler.class, runnableitem, projectdir);
            generate(CodeGen_Webstart.class, runnableitem, projectdir); // can be removed ???
            generate(CodeGen_WebToolBeanContextConfig.class, runnableitem, projectdir);
            generate(CodeGen_WebManual.class, runnableitem, projectdir);
            generate(CodeGen_WebPage.class, runnableitem, projectdir, "/templates/pages", RESOURCETYPE.isDirectory);

            log.info("XHTML pages generated!");

            long time = (System.currentTimeMillis() - starttime) / 1000;

            log.info("Project \"{}\" (id:{}, version:{}) created at '{}' in {} seconds.", projectname, projectid, projectversion, projectdir, time);

        } catch (CodeGenParserException e) {
            log.error("CodeGenParserException occurred :", e);
            return false;
        }
        return true;
    }

    /**
     * Returns "Choice" optiongroup for CodeGeneration.
     *
     * @return
     */
    private static OptionGroup getCMDLineOptionsGroups() {
        OptionGroup optionsgroup = new OptionGroup();
        optionsgroup.setRequired(true);
        Option generate = new Option("g", "generate", true, "Generate app from xml description.");
        generate.setArgName("runnableitem.xml");
        optionsgroup
                .addOption(new Option("V", "version", false, "version"))
                .addOption(new Option("h", "help", false, "help"))
                .addOption(generate);

        return optionsgroup;
    }

    /**
     * Return Option for CodeGeneration
     *
     * @return
     */
    private static Options getCMDLineOptions() {
        Options cmdLineOptions = new Options();
        cmdLineOptions.addOption("t", "target", true, "Path to target dir. If not set java temporary directory will be used!");
        cmdLineOptions.addOption("p", "project",true, "Path to project dir. If unset ${target}+${toolid} will be used!");             
        cmdLineOptions.addOption("v", "verbose", false, "more verbose output");
        return cmdLineOptions;
    }

    /**
     * Encapsulate File.mkdirs method in a static method
     *
     * @param fn
     * @return
     */
    private static boolean mkdirs(String fn) {
        File tmp = new File(fn);
        return tmp.mkdirs();
    }

    public static void generate(Class clazz, File runnableitem, File projectdir) throws CodeGenParserException {
        generate(clazz, runnableitem, projectdir, null, null, null);
    }

    public static void generate(Class clazz, File runnableitem, File projectdir, File file) throws CodeGenParserException {
        if (file.isFile()) {
            generate(clazz, runnableitem, projectdir, file, null, RESOURCETYPE.isFile);
        } else if (file.isDirectory()) {
            generate(clazz, runnableitem, projectdir, file, null, RESOURCETYPE.isDirectory);

        } else {
            throw new CodeGenParserException("File '" + file + "' is not either a file or directory!");
        }
    }

    public static void generate(Class clazz, File runnableitem, File projectdir, String path, RESOURCETYPE type) throws CodeGenParserException {
        generate(clazz, runnableitem, projectdir, null, path, type);
    }

    public static void generate(Class clazz, File runnableitem, File projectdir, File file, String path, RESOURCETYPE type) throws CodeGenParserException {
        try {
            Constructor constructor = clazz.getConstructor();
            Object object = constructor.newInstance();

            if (!(object instanceof CodeGen)) {
                throw new CodeGenParserException("'" + clazz.getSimpleName() + "' does not implement the CodeGen Interface!");
            }

            CodeGen codegen = (CodeGen) object;
            codegen.setRunnableFile(runnableitem);
            codegen.setResultDir(projectdir);

            // distinguish if path describes a file or resource
            if (file != null) { // template resource is a file
                switch (type) {
                    case isFile:
                        codegen.setTemplateFile(file);
                        codegen.generate();
                        break;
                    case isDirectory:
                        for (File f : file.listFiles()) {
                            codegen.setTemplateFile(f);
                            codegen.generate();
                        }
                        break;
                }
            } else if (path != null) { // template resource is a path
                switch (type) {
                    case isFile:
                        codegen.setTemplateResource(path);
                        codegen.generate();
                        break;
                    case isDirectory:
                        for (String entry : getClasspathEntriesByPath(path)) {
                            codegen.setTemplateResource(entry);
                            codegen.generate();
                        }
                }
            } else { // otherwise use default template defined by CodeGen Implementation
                codegen.generate();
            }

        } catch (NoSuchMethodException e) {
            throw new CodeGenParserException("Unknown implementation '" + clazz.getSimpleName() + "' of CodeGen interface.");
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new CodeGenParserException("Can not instantiate class '" + clazz.getSimpleName() + "'");
        } catch (IOException e) {
            throw new CodeGenParserException("tbd");
        }

    }

    /**
     * Get a list of resources from class path directory. Attention: path must
     * be a path !
     *
     * @param path
     * @return
     * @throws IOException
     * @throws de.unibi.techfak.bibiserv.util.codegen.CodeGenParserException
     */
    public static List<String> getClasspathEntriesByPath(String path) throws IOException, CodeGenParserException {

        try {
            List<String> tmp = new ArrayList<>();

            URL jarUrl = Main.class.getProtectionDomain().getCodeSource().getLocation();
            String jarPath = URLDecoder.decode(jarUrl.getFile(), "UTF-8");
            JarFile jarFile = new JarFile(jarPath);

            String prefix = path.startsWith("/") ? path.substring(1) : path;

            Enumeration<JarEntry> enu = jarFile.entries();
            while (enu.hasMoreElements()) {
                JarEntry je = enu.nextElement();
                if (!je.isDirectory()) {
                    String name = je.getName();
                    if (name.startsWith(prefix)) {
                        tmp.add("/"+name);
                    }
                }
            }
            return tmp;
        } catch (Exception e) {
            // maybe we start Main.class not from Jar.
        }

        InputStream is = Main.class.getResourceAsStream(path);

        if (is == null) {
            throw new CodeGenParserException("Path '" + path + "' not found in Classpath!");
        }

        StringBuilder sb = new StringBuilder();

        byte[] buffer = new byte[1024];
        while (is.read(buffer) != -1) {
            sb.append(new String(buffer, Charset.defaultCharset()));
        }
        is.close();
        return Arrays
                .asList(sb.toString().split("\n")) // Convert StringBuilder to individual lines
                .stream() // Stream the list
                .filter(line -> line.trim().length() > 0) // Filter out empty lines
                .map(line -> path+"/"+line)               // add path for each entry
                .collect(Collectors.toList());            // Collect remaining lines into a List again
        
    }

    /**
     * Copy a text based file from in to out and replace TEMPLATE_ID with 'id'
     * and TEMPLATE_NAME with 'name'
     *
     *
     * @param in
     * @param out
     * @param id
     * @param name
     * @throws IOException
     */
    public static void CopyAndReplace(InputStream in, OutputStream out, String id, String name, String version) throws IOException {

        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(out));

        String line;

        while ((line = br.readLine()) != null) {
            bw.append(line.replace("TEMPLATE_ID", id).replace("TEMPLATE_NAME", name).replace("TEMPLATE_VERSION", version));
            bw.newLine();
        }
        br.close();
        bw.close();

    }
}
