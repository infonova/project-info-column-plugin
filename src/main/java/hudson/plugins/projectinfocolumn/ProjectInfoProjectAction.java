package hudson.plugins.projectinfocolumn;

import hudson.model.Action;
import hudson.model.AbstractProject;
import hudson.model.Run;
import org.kohsuke.stapler.export.Exported;

public class ProjectInfoProjectAction implements Action {
    private final AbstractProject<?, ?> project;

    public ProjectInfoProjectAction(AbstractProject<?, ?> project) {
        this.project = project;
    }

    public String getIconFileName() {
        return null;
    }

    public String getDisplayName() {
        return null;
    }

    public String getUrlName() {
        return "";
    }

    @Exported
    public boolean hasProjectInfo() {
        return getProjectInfoAction() != null;
    }

    @Exported
    public Action getProjectInfoAction() {
        Run<?,?> lastCompletedBuild = project.getLastCompletedBuild();
        if (lastCompletedBuild == null) {
            return null;
        }
        return lastCompletedBuild.getAction(ProjectInfoAction.class);
    }

}
