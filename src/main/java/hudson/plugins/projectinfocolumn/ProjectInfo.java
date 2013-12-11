package hudson.plugins.projectinfocolumn;

import org.kohsuke.stapler.DataBoundConstructor;

import hudson.Extension;
import hudson.model.Describable;
import hudson.model.Descriptor;


public class ProjectInfo implements Describable<ProjectInfo> {

    private String value;
    private String icon;
    private String title;
    private String xpath;

    public enum Icons {
        R6VERSION("/plugin/project-info-column-plugin/images/IR6.png"),
        POMVERSION("/plugin/project-info-column-plugin/images/IR6.png");

        private Icons(String value) {
            this.value = value;
        }

        private String value;

        @Override
        public String toString() {
            return value;
        }

        public String getValue() {
            return value;
        }
    }

    public ProjectInfo(String value, String icon, String title, String xpath) {
        super();
        this.value = value;
        this.icon = icon;
        this.title = title;
        this.xpath = xpath;
    }

    @DataBoundConstructor
    public ProjectInfo(String title, String xpath) {
        this(null, null, title, xpath);
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }


    public String getXpath() {
        return xpath;
    }


    public void setXpath(String xpath) {
        this.xpath = xpath;
    }

    @Override
    public Descriptor<ProjectInfo> getDescriptor() {
        return new DescriptorImpl();
    }

    @Extension
    public static final class DescriptorImpl extends Descriptor<ProjectInfo> {

        @Override
        public String getDisplayName() {
            return "prjInfoDescriptorImpl.displayName";
        }

    }

}
