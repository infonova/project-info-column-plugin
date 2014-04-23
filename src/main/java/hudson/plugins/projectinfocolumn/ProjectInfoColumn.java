package hudson.plugins.projectinfocolumn;

import hudson.Extension;
import hudson.model.Job;
import hudson.util.FormValidation;
import hudson.views.ListViewColumnDescriptor;
import hudson.views.ListViewColumn;

import java.io.IOException;

import javax.servlet.ServletException;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;


public class ProjectInfoColumn extends ListViewColumn {

    private String title;

    @DataBoundConstructor
    public ProjectInfoColumn(String title) {
        this.title = title;
    }

    public String getText(@SuppressWarnings("rawtypes") Job job) {

        if (job == null || job.getLastCompletedBuild() == null) {
            return StringUtils.EMPTY;
        }

        ProjectInfoAction projectInfoAction = job.getLastCompletedBuild().getAction(ProjectInfoAction.class);
        if (projectInfoAction != null) {
            for (ProjectInfo info : projectInfoAction.getProjectInfos()) {
                if (this.title.equalsIgnoreCase(info.getTitle())) {
                    return info.getValue() == null ? StringUtils.EMPTY : info.getValue();
                }
            }
        }
        return StringUtils.EMPTY;
    }

    @Extension
    public static class DescriptorImpl extends ListViewColumnDescriptor {

        /**
         * Validates the columnHeader property.
         *
         * @param value
         * @return FormValidation
         * @throws IOException
         * @throws ServletException
         */
        public FormValidation doCheckTitle(@QueryParameter String value) throws IOException, ServletException {

            if (StringUtils.isBlank(value)) {
                return FormValidation.error(Messages.ProjectInfoParser_TitleError());
            }
            return FormValidation.ok();
        }

        @Override
        public boolean shownByDefault() {
            return false;
        }

        @Override
        public String getDisplayName() {
            return Messages.ProjectInfoColumn_DisplayName();
        }

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
