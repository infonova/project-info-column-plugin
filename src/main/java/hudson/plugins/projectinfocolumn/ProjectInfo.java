package hudson.plugins.projectinfocolumn;

import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;

import hudson.Extension;
import hudson.model.Describable;
import hudson.model.Descriptor;
import hudson.util.ListBoxModel;
import hudson.util.ListBoxModel.Option;


public class ProjectInfo implements Describable<ProjectInfo> {

    private String value;
    private String icon;
    private String title;
    private String xpath;

    public enum Icons {
        R6VERSION("/plugin/project-info-column-plugin/images/IR6.png", "IR6"),
        POMVERSION("notepad.png", "jenkins notepad"),
        DEFAULT("notepad.png", "default");

        private Icons(String value, String displayName) {
            this.value = value;
            this.displayName = displayName;
        }

        private String value;
        private String displayName;

        @Override
        public String toString() {
            return value;
        }

        public String getValue() {
            return value;
        }

        public String getDisplayName() {
            return displayName;
        }
    }


    public ProjectInfo(String value, String icon, String title, String xpath) {
        super();
        this.value = value;
        this.icon = StringUtils.isBlank(icon) ? Icons.DEFAULT.getValue() : icon;
        this.title = title;
        this.xpath = xpath;
    }

    @DataBoundConstructor
    public ProjectInfo(String title, String xpath, String icon) {
        this(null, icon, title, xpath);
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

        public ListBoxModel doFillIconItems() {
            ListBoxModel iconListBoxModel = new ListBoxModel();
            for (Icons icon : Icons.values()) {
                iconListBoxModel.add(new Option(icon.getDisplayName(), icon.getValue()));
            }
            return iconListBoxModel;
        }

        @Override
        public String getDisplayName() {
            return "prjInfoDescriptorImpl.displayName";
        }

    }

}
