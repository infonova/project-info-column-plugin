package hudson.plugins.projectinfocolumn;

import hudson.Extension;
import hudson.model.Job;
import hudson.util.FormValidation;
import hudson.views.ListViewColumnDescriptor;
import hudson.views.ListViewColumn;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;


/**
 * {@link ListViewColumn} implementation which displays version of the POM given
 * file.
 *
 * @author christian.weber
 * @since 1.0
 */
public class ProjectInfoColumn extends ListViewColumn {

    private final String header;

    private final String xPath;

    @DataBoundConstructor
    public ProjectInfoColumn(String header, String xPath) {
        this.header = header;
        this.xPath = xPath;
    }


    /**
     * Returns the variable value with the given variable name.
     *
     * @param job
     * @return String
     */
    public String getText(@SuppressWarnings("rawtypes") Job job) {

        if (job == null || job.getLastCompletedBuild() == null) {
            return StringUtils.EMPTY;
        }

        try {

            File xmlFile = new File(job.getLastCompletedBuild().getArtifactsDir(), "pom.xml");

            if (!xmlFile.exists()) {
                return StringUtils.EMPTY;
            }

            return getText(xmlFile);
        } catch (Exception e) {
            return StringUtils.EMPTY;
        }
    }

    /**
     * Returns the set of parameter names of the JENKINS job with the given
     * name.
     *
     * @param configStream
     * @return Set
     * @throws Exception
     */
    private String getText(File file) throws Exception {

        XPathFactory xPathFactory = XPathFactory.newInstance();
        XPath instance = xPathFactory.newXPath();

        String text = IOUtils.toString(new FileInputStream(file));

        String ns = StringUtils.substringBetween(text, "xmlns=\"", "\"");

        // if (StringUtils.isNotBlank(ns)) {
        // NamespaceContextImpl context = new NamespaceContextImpl();
        // context.startPrefixMapping("ns", ns);
        // instance.setNamespaceContext(context);
        // }

        String[] expressions = StringUtils.split(xPath, ";");
        for (String expression : expressions) {

            if (StringUtils.isNotBlank(ns)) {
                expression = StringUtils.replace(expression, "//", "__");
                expression = StringUtils.replace(expression, "/", "/ns:");
                expression = StringUtils.replace(expression, "__", "//ns:");
            }

            XPathExpression xpathExpression1 = instance.compile(expression);
            if (isPresent(xpathExpression1, text)) {
                return getVersion(xpathExpression1, text);
            }

        }

        return StringUtils.EMPTY;
    }

    /**
     * Indicates if the given XPath expression matches a target node.
     *
     * @param expression
     * @param text
     * @return boolean
     * @throws XPathExpressionException
     */
    private boolean isPresent(XPathExpression expression, String text) throws XPathExpressionException {

        InputSource inputSource = new InputSource(IOUtils.toInputStream(text));
        NodeList list = (NodeList)expression.evaluate(inputSource, XPathConstants.NODESET);

        return list.getLength() > 0;
    }

    /**
     * Returns the text node value from a tag which relates to the given XPath
     * expression.
     *
     * @param expression
     * @param text
     * @return String
     * @throws XPathExpressionException
     */
    private String getVersion(XPathExpression expression, String text) throws XPathExpressionException {

        InputSource inputSource = new InputSource(IOUtils.toInputStream(text));
        NodeList list = (NodeList)expression.evaluate(inputSource, XPathConstants.NODESET);
        Node node = list.item(0);
        Node childNode = node.getFirstChild();

        return childNode.getNodeValue();
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
        public FormValidation doCheckColumnHeader(@QueryParameter String value) throws IOException, ServletException {

            if (StringUtils.isBlank(value)) {
                return FormValidation.error(Messages.ProjectInfoParser_HeaderError());
            }
            return FormValidation.ok();
        }

        /**
         * Validates the xPath property.
         *
         * @param value
         * @return FormValidation
         * @throws IOException
         * @throws ServletException
         */
        public FormValidation doCheckXPath(@QueryParameter String value) throws IOException, ServletException {

            if (StringUtils.isBlank(value)) {
                return FormValidation.error(Messages.ProjectInfoParser_XPathError());
            }
            return FormValidation.ok();
        }

        @Override
        public boolean shownByDefault() {
            return false;
        }

        @Override
        public String getDisplayName() {
            return Messages.ProjectInfoParser_DisplayName();
        }

    }

}
