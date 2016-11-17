/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 2010 BiBiServ Curator Team, http://bibiserv.cebitec.uni-bielefeld.de, 
 * All rights reserved.
 * 
 * The contents of this file are subject to the terms of the Common
 * Development and Distribution License("CDDL") (the "License"). You 
 * may not use this file except in compliance with the License. You can 
 * obtain a copy of the License at http://www.sun.com/cddl/cddl.html
 * 
 * See the License for the specific language governing permissions and 
 * limitations under the License.  When distributing the software, include 
 * this License Header Notice in each file.  If applicable, add the following 
 * below the License Header, with the fields enclosed by brackets [] replaced
 *  by your own identifying information:
 * 
 * "Portions Copyrighted [year] [name of copyright owner]"
 * 
 * Contributor(s):
 * 
 */
package de.unibi.techfak.bibiserv.util.codegen;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.apache.tools.ant.AntClassLoader;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Reference;

/**
 * This class 'CodeGenTask' represents an ANT task for the CodeGen Interface. 
 * Example usage :
 *
 * <pre>
 *  &lt;target name="use" description="Use the CodeGenTask ..." &gt;
 *   &lt; taskdef name="codegen" classname="de.unibi.techfak.bibiserv.util.codegen.CodeGenTask" classpath="${path_to_the_codegen.jar}"/&gt;
 *
 *   &lt; codegen destdir="dest" /&gt;
 *
 *   &lt; codegen template="~/bibiserv2/main/base/codegen/templates/template_impl.java"
 *                runnableitem="~/bibiserv2/main/base/sample/rnashapes_sample.xml"
 *                destdir="dest"
 *                class="de.unibi.techfak.bibiserv.util.codegen.CodeGenImplementation" /&gt;
 *
 *
 *
 *  &lt;/target&gt;
 * </pre>
 *
 *
 * @author Jan Krueger - jkrueger(at)techfak.uni-bielefeld.de
 */
public class CodeGenTask  extends Task{

    private String template_fn = null;
    private File template_file = null;
    private Path classpath = null;

    public void setTemplate(String template_fn){
        this.template_fn = template_fn;
    }

    private String dest_dn = null;
    private File dest_dir = null;

    public void setDestdir(String dest_dn){
        this.dest_dn = dest_dn;
    }

    private String runnableitem_fn = null;
    private File runnableitem_file = null;

    public void setRunnableitem(String runnableitem_fn) {
        this.runnableitem_fn = runnableitem_fn;
    }

    private String class_name = null;

    public void setClass(String class_name){
        this.class_name  = class_name;
    }



    @Override
    public void execute() throws BuildException{
        /* check if template is set and exists ... */
        if (template_fn != null) {
            template_file = new File(template_fn);
            if (!template_file.canRead() && CodeGenTask.class.getResourceAsStream("/"+template_fn) == null) {
                throw new BuildException("Template found at location \""+template_fn+"\" isn't readable.");
            }
        } 
        /* check if runnable item is set and exists ... */
        if (runnableitem_fn == null) {
            throw new BuildException("No runnableitem set.");
        }
        runnableitem_file = new File(runnableitem_fn);
        if (!runnableitem_file.isFile()) {
            throw new BuildException("No runnableitem found at location \""+runnableitem_fn+"\".");
        }
        /* check if destination dir is set and exists ... */
        if (dest_dn == null) {
            throw new BuildException("No destination directory set.");
        }
        dest_dir = new File(dest_dn);
        if (!dest_dir.isDirectory()) {
            if (!dest_dir.exists()) {
                log("Destination directory \""+dest_dn+"\" does not exist, create it (including all not existing path to directories).");
                dest_dir.mkdirs();
            } else {
                throw new BuildException("Destination directory \""+dest_dn+"\" does not exists and can't created (maybe it's a file, link, ...).");
            }
        }
        /* Check if class does exist and create a new instance from it (using java reflection) */
        if (class_name == null) {
            throw new BuildException("No (implementing CodeGen) class set.");
        }

        try {
            Class codegen_class = null;
            /* if no classpath is provided do use the default classloader ... */
            if (classpath == null) {
                codegen_class = Class.forName(class_name);
            } else {
                /* otherwise use AntClassLoader ...*/
                AntClassLoader loader = getProject().createClassLoader(classpath);
                loader.setParent(getProject().getCoreLoader());
                loader.setParentFirst(false);
                codegen_class = Class.forName(class_name,true,loader);
            }

            Object codegen = codegen_class.newInstance();


            
            Method setTemplateFile = null;
            try {
                setTemplateFile = codegen_class.getMethod("setTemplateFile", new Class [] { File.class});
            } catch (NoSuchMethodException e) {
                throw new BuildException("No Such Method \"setTemplateFile\" ... ",e);
            }
            try {
                setTemplateFile.invoke(codegen, template_file);
            } catch (InvocationTargetException e) {
                throw new BuildException("InvocationTargetException while invoke \"setTemplateFile\"");
            }

            Method setResultDir = null;
            try {
                setResultDir = codegen_class.getMethod("setResultDir", new Class [] { File.class});
            } catch (NoSuchMethodException e) {
                throw new BuildException("No Such Method \"setResultDir\" ... ",e);
            }
            try {
                setResultDir.invoke(codegen, dest_dir);
            } catch (InvocationTargetException e) {
                throw new BuildException("InvocationTargetException while invoke \"setResultDir\"",e);
            }

            Method setRunnableFile = null;
            try {
                setRunnableFile = codegen_class.getMethod("setRunnableFile", new Class [] { File.class});
            } catch (NoSuchMethodException e) {
                throw new BuildException("No Such Method \"setRunnableFile\" ... ",e);
            }
            try {
                setRunnableFile.invoke(codegen, runnableitem_file);
            } catch (InvocationTargetException e) {
                throw new BuildException("InvocationTargetException while invoke \"setRunnableFile\"",e);
            }

            /* ... start the generation process ... */
            Method generate = null;
             try {
                generate = codegen_class.getMethod("generate");
            } catch (NoSuchMethodException e) {
                throw new BuildException("No Such Method \"generate\" ... ",e);
            }
            try {
                generate.invoke(codegen);
            } catch (InvocationTargetException e) {
                throw new BuildException("InvocationTargetException while invoke \"generate\""+e,e);
            }

        } catch (ClassNotFoundException e){
            throw new BuildException("Class \""+class_name+"\" can't be loaded. Correct classname, classpath, ... ?",e);
        } catch (ClassCastException e) {
            throw new BuildException ("Class \""+class_name+"\" hasn't type \"de.unibi.techfak.bibiserv.util.codegen.CodeGen\".",e);
        } catch (InstantiationException e){
            throw new BuildException ("Class \""+class_name+"\" can't be instantiated.",e);
        } catch (IllegalAccessException e){
            throw new BuildException ("IllegalAccessException while instantiated class \""+class_name+"\".",e);

        }

//        /* now we can set the environment for the code generation ... */
//        codegen.setTemplateFileName(template_file);
//        codegen.setResultDir(dest_dir);
//        codegen.setRunnableFileName(runnableitem_file);
//
//        /* ... and start the generation process ... */
//        try {
//            codegen.generate();
//        } catch (CodeGenParserException e){
//            // in the case of any exception throw an ant build exception ...
//            throw new BuildException(e);
//        }
          //throw new BuildException("YES!!!");
    }

       /**
        * Set the classpath to be used when running the task
        * 
        * @param s an ant Path object containing the classpath
        */
    public void setClasspath(Path s){
        createClasspath().append(s);
    }

    /**
     * Set the classptah to be use by reference
     *
     * @param r a reference to an existing classpath
     */
    public void setClasspathRef(Reference r){
        createClasspath().setRefid(r);
    }

    /**
     * createClasspath from current ant project
     *
     * @return a initalized classpath
     */
    public Path createClasspath(){
        if (classpath == null){
            classpath = new Path(getProject());
        }
        return classpath;
    }


}
