package de.unibi.cebitec.bibiserv.wizard.tools;

import de.unibi.techfak.bibiserv.cms.Tdependency;
import de.unibi.techfak.bibiserv.cms.Texample;
import de.unibi.techfak.bibiserv.cms.TexecPath;
import de.unibi.techfak.bibiserv.cms.Texecutable;
import de.unibi.techfak.bibiserv.cms.Tfile;
import de.unibi.techfak.bibiserv.cms.Tfunction;
import de.unibi.techfak.bibiserv.cms.Tmanual;
import de.unibi.techfak.bibiserv.cms.TrunnableItem;
import de.unibi.techfak.bibiserv.cms.TrunnableItemView;
import de.unibi.techfak.bibiserv.cms.Twebstart;


/**
 * This class contains functions that copy existing
 * BiBiServ-Abstraction-elements.
 *
 * @author Benjamin Paassen - bpaassen(at)CeBiTec.uni-bielefeld.de
 */
public class CopyFactory {

    /**
     * Copy a runnable to new object with different id. Please note that the
     * executable gets not copied!
     *
     * @param runnable the runnable itself.
     * @param idSuffix suffix for id.
     * @return full copy of the runnable.
     */
    public static TrunnableItem copyRunnable(final TrunnableItem runnable, String idSuffix) {
        TrunnableItem copy = new TrunnableItem();
        copy.setId(runnable.getId() + idSuffix);
        copy.getName().addAll(runnable.getName());
        copy.getShortDescription().addAll(runnable.getShortDescription());
        copy.getToolTipText().addAll(runnable.getToolTipText());
        copy.getKeywords().addAll(runnable.getKeywords());
        return copy;
    }

    /**
     * Copy a executable to new object with different id.
     *
     * @param executable the executable itself.
     * @param idSuffix suffix for id.
     * @return full copy of the executable.
     */
    public static Texecutable copyExecutable(final Texecutable executable, String idSuffix) {
        Texecutable copy = new Texecutable();
        TexecPath newExecPath = new TexecPath();
        newExecPath.setExecutableType(executable.getExecInfo().getExecutableType());
        newExecPath.setCallingInformation(executable.getExecInfo().getCallingInformation());
        if (executable.getExecInfo().isSetPath()) {
            newExecPath.setPath(executable.getExecInfo().getPath());
        }
        copy.setExecInfo(newExecPath);
        copy.setVersion(executable.getVersion());
        return copy;
    }

    /**
     * Copy a view to new object with different id.
     *
     * @param view the view itself.
     * @param idSuffix suffix for id.
     * @return full copy of the view.
     */
    public static TrunnableItemView copyView(final TrunnableItemView view, String idSuffix) {

        TrunnableItemView copy = new TrunnableItemView();
        //disambiguate ID
        copy.setId(view.getId() + idSuffix);
        //copy the rest of the content
        copy.getTitle().addAll(view.getTitle());
        copy.getCustomContent().addAll(view.getCustomContent());
        copy.setType(view.getType());
        return copy;
    }

    /**
     * Copy a function to new object with different id.
     *
     * @param newfunction function to copy
     * @param idSuffix suffix for id.
     * @return copied element
     */
    public static Tfunction copyFunction(final Tfunction function, String idSuffix) {
        Tfunction copy = new Tfunction();

        copy.getDepref().addAll(function.getDepref());
        copy.getDescription().addAll(function.getDescription());
        copy.getExample().addAll(function.getExample());
        copy.setId(function.getId() + idSuffix);
        copy.getInputref().addAll(function.getInputref());
        copy.getName().addAll(function.getName());
        copy.setOutputref(function.getOutputref());
        copy.setParamAndInputOutputOrder(
                function.getParamAndInputOutputOrder());
        copy.setParamGroup(function.getParamGroup());
        copy.getShortDescription().addAll(function.getShortDescription());
        copy.getOutputfileref().addAll(function.getOutputfileref());

        return copy;
    }

    public static Tdependency copyDependency(final Tdependency dependency) {
        Tdependency copy = new Tdependency();

        copy.setDependencyDefinition(dependency.getDependencyDefinition());
        copy.setId(dependency.getId());
        copy.getName().addAll(dependency.getName());
        copy.getDescription().addAll(dependency.getDescription());
        copy.getShortDescription().addAll(dependency.getShortDescription());

        return copy;
    }

    public static Tmanual copyManual(final Tmanual manual){
        Tmanual copy = new Tmanual();

        copy.getCustomContent().addAll(manual.getCustomContent());
        copy.setId(manual.getId());
        copy.getIntroductoryText().addAll(manual.getIntroductoryText());

        return copy;
    }

    public static Tfile copyFile(final Tfile file) {
        Tfile copy = new Tfile();

        copy.setFilename(file.getFilename());
        copy.setId(file.getId());
        copy.getName().addAll(file.getName());
        copy.getDescription().addAll(file.getDescription());
        copy.getShortDescription().addAll(file.getShortDescription());
        copy.setPlatform(file.getPlatform());
        copy.setVersion(file.getVersion());

        return copy;
    }

    public static Texample copyExample(final Texample example) {
        Texample newexample = new Texample();

        newexample.getDescription().addAll(example.getDescription());
        newexample.getName().addAll(example.getName());
        for(Texample.Prop prop:example.getProp()){
            Texample.Prop newprop = new Texample.Prop();
            newprop.setIdref(prop.getIdref());
            newprop.setValue(prop.getValue());
            newexample.getProp().add(newprop);
        }
        return newexample;
    }
    
    public static Twebstart copyWebstart(final Twebstart webstart) {
        
        Twebstart newwebstart = new Twebstart();

        newwebstart.getTitle().addAll(webstart.getTitle());
        newwebstart.setId(webstart.getId());
        newwebstart.setJnlp(webstart.getJnlp());
        newwebstart.getCustomContent().addAll(webstart.getCustomContent());
        newwebstart.getIntroductoryText().addAll(webstart.getIntroductoryText());

        return newwebstart;
    }
}
