package hudson.plugins.projectinfocolumn;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.Action;
import hudson.model.BuildListener;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Publisher;
import hudson.tasks.Recorder;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.ws.commons.util.NamespaceContextImpl;
import org.kohsuke.stapler.DataBoundConstructor;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class ProjectInfoParser extends Recorder {

    private List<ProjectInfo> projectInfos;

    @DataBoundConstructor
    public ProjectInfoParser(List<ProjectInfo> projectInfos) {
        super();
        if (this.projectInfos == null) {
            this.projectInfos = new ArrayList<ProjectInfo>();
        }
        this.projectInfos.addAll(projectInfos);
    }

    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener)
            throws InterruptedException, IOException {

        final PrintStream console = listener.getLogger();

        for (ProjectInfo info : projectInfos) {
            try {
                InputStream inputStream = build.getWorkspace().child("pom.xml").read();
                info.setValue(findValueByXpathInFile(info.getXpath(), inputStream));
            } catch (Exception e) {
                e.printStackTrace(console);
            }
        }
        build.addAction(new ProjectInfoAction(this.projectInfos.toArray(new ProjectInfo[0])));

        return true;
    }

    private String findValueByXpathInFile(String xpath, InputStream inputStream) throws Exception {

        XPathFactory xPathFactory = XPathFactory.newInstance();
        XPath xpathInstance = xPathFactory.newXPath();

        String fileContent = IOUtils.toString(inputStream);

        String ns = StringUtils.substringBetween(fileContent, "xmlns=\"", "\"");

        if (StringUtils.isNotBlank(ns)) {
            NamespaceContextImpl context = new NamespaceContextImpl();
            context.startPrefixMapping("ns", ns);
            xpathInstance.setNamespaceContext(context);
        }

        String[] expressions = StringUtils.split(xpath, ";");
        for (String expression : expressions) {

            if (StringUtils.isNotBlank(ns)) {
                expression = StringUtils.replace(expression, "//", "__");
                expression = StringUtils.replace(expression, "/", "/ns:");
                expression = StringUtils.replace(expression, "__", "//ns:");
            }

            XPathExpression xpathExpression = xpathInstance.compile(expression);
            if (isPresent(xpathExpression, fileContent)) {
                return getValue(xpathExpression, fileContent);
            }

        }

        return StringUtils.EMPTY;
    }

    private boolean isPresent(XPathExpression expression, String text) throws XPathExpressionException {

        InputSource inputSource = new InputSource(IOUtils.toInputStream(text));
        NodeList list = (NodeList)expression.evaluate(inputSource, XPathConstants.NODESET);

        return list.getLength() > 0;
    }

    private String getValue(XPathExpression expression, String text) throws XPathExpressionException {

        InputSource inputSource = new InputSource(IOUtils.toInputStream(text));
        NodeList list = (NodeList)expression.evaluate(inputSource, XPathConstants.NODESET);
        Node node = list.item(0);
        Node childNode = node.getFirstChild();

        return childNode.getNodeValue();
    }

    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Publisher> {

        @SuppressWarnings("rawtypes")
        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return Messages.ProjectInfoParser_DisplayName();
        }

    }

    @Override
    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.NONE;
    }

    @Override
    public Action getProjectAction(AbstractProject<?, ?> project) {
        return new ProjectInfoProjectAction(project);
    }

    public List<ProjectInfo> getProjectInfos() {
        return projectInfos;
    }


    public void setProjectInfos(List<ProjectInfo> projectInfos) {
        if (this.projectInfos == null) {
            this.projectInfos = new ArrayList<ProjectInfo>();
        }
        this.projectInfos.addAll(projectInfos);
    }

}
