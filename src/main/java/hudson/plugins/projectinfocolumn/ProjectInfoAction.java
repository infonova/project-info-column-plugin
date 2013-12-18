package hudson.plugins.projectinfocolumn;

import hudson.model.Action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProjectInfoAction implements Action {

    private List<ProjectInfo> projectInfos;

    public String getIconFileName() {
        return null;
    }

    public String getDisplayName() {
        return null;
    }

    public String getUrlName() {
        return "";
    }

    public ProjectInfoAction(ProjectInfo... projectInfos) {
        if (this.projectInfos == null) {
            this.projectInfos = new ArrayList<ProjectInfo>();
        }
        Collections.addAll(this.projectInfos, projectInfos);
    }

    public List<ProjectInfo> getProjectInfos() {
        return projectInfos;
    }

}
