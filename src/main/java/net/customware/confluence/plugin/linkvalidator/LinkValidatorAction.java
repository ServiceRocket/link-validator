package net.customware.confluence.plugin.linkvalidator;

import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.velocity.htmlsafe.HtmlSafe;
import com.opensymphony.webwork.ServletActionContext;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import java.net.MalformedURLException;
import java.net.URL;

public class LinkValidatorAction extends ConfluenceActionSupport {
	private static final long serialVersionUID = 1L;

	private static final Logger LOG = Logger.getLogger(LinkValidatorAction.class);

    // The URL to validate.
    private String url;

    // The timeout period
    private int timeout = 10;

    private boolean verbose = false;

    private String status;

    public String execute() {
        try {
            HttpServletRequest request = ServletActionContext.getRequest();
            URL currentUrl = new URL(request.getRequestURL().toString());
            URL url = new URL(currentUrl, this.url);

            // Check we aren't validating ourselves.
            if (currentUrl.equals(url))
                status = LinkValidatorUtil.createHttpStatusIcon(200, "OK", verbose);
            else
                status = LinkValidatorUtil.validate(url, verbose, timeout);

        } catch (MalformedURLException e) {
            String errorMessage = "Malformed URL: " + e.getLocalizedMessage();
            LOG.error(errorMessage, e);
            status = LinkValidatorUtil.createErrorIcon(errorMessage, verbose);
        }
        return SUCCESS;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isVerbose() {
        return verbose;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    @HtmlSafe
    public String getStatus() {
        return status;
    }

}
