package de.unibi.cebitec.bibiserv.wizard.bean;

import java.util.List;

/**
 * Store for one example while loaded in function.
 * This is needed to prevent constant forth and back converting
 * in functionbean while changing parameter/input set.
 * @author Thomas Gatter - tgatter(aet)cebitec.uni-bielefeld.de
 */
public class Example {

    private String name;
    private String Description;
    private List<ExampleStore> examples;
    private boolean valid;
    private boolean dependencyValid;
    private String dependencyReason;

    public Example(String name, String description, List<ExampleStore> examples,
            boolean valid, boolean dependencyValid) {
        this.name = name;
        this.Description = description;
        this.examples = examples;
        this.valid = valid;
        this.dependencyValid = dependencyValid;
        this.dependencyReason="";
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String Description) {
        this.Description = Description;
    }

    public List<ExampleStore> getExamples() {
        return examples;
    }

    public void setExamples(List<ExampleStore> examples) {
        this.examples = examples;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public boolean isDependencyValid() {
        return dependencyValid;
    }

    public void setDependencyValid(boolean dependencyValid) {
        this.dependencyValid = dependencyValid;
    }

    public String getDependencyReason() {
        return dependencyReason;
    }

    public void setDependencyReason(String dependencyReason) {
        this.dependencyReason = dependencyReason;
    }
   
}
