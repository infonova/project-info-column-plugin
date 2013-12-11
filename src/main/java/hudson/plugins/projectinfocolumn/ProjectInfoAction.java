package hudson.plugins.projectinfocolumn;

import hudson.model.Action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.kohsuke.stapler.export.Exported;

public class ProjectInfoAction implements Action {

    private transient String text;
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

    @Exported
    public String getText() {
        if (text == null) {
            StringBuilder textBuilder = new StringBuilder();
            for (ProjectInfo info : projectInfos) {
                textBuilder.append("<b>");
                textBuilder.append(info);
                textBuilder.append("</b>");
            }
            text = textBuilder.toString();
        }
        return text;
    }

    public List<ProjectInfo> getProjectInfos() {
        return projectInfos;
    }

}
