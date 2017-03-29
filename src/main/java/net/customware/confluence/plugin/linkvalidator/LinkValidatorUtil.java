package net.customware.confluence.plugin.linkvalidator;

import com.atlassian.confluence.setup.BootstrapManager;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.spring.container.ContainerManager;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

public class LinkValidatorUtil
{
    private static final Logger LOG                         = LoggerFactory.getLogger(LinkValidatorUtil.class);

    private static final String HTTPS_PROTOCOL              = "https";

    private static final String HTTP_PROTOCOL               = "http";

    private static final String INFO_ICON                   = "emoticons/information.gif";

    private static final String GOOD_ICON                   = "emoticons/check.gif";

    private static final String ERROR_ICON                  = "emoticons/warning.gif";

    private static final String REDIRECT_ICON               = "ref_16.gif";

    private static final String UNKNOWN_ICON                = "emoticons/help_16.gif";

    private static final String FORBIDDEN_ICON              = "emoticons/forbidden.gif";

    private static final String LINK_VALIDATOR_HEADER_VALUE = "1";

    private static final String LINK_VALIDATOR_HEADER       = "x-randombits-link-validator";

    static BootstrapManager     bootstrapManager;

    private LinkValidatorUtil()
    {
    }

    public static boolean isBeingValidated(HttpServletRequest request)
    {
        return LINK_VALIDATOR_HEADER_VALUE.equals(request.getHeader(LINK_VALIDATOR_HEADER));
    }

    public static String validate(URL url, boolean verbose, int timeout)
    {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        RequestConfig requestConfig  = RequestConfig.custom()
                .setSocketTimeout(timeout * 1000)
                .build();
        CloseableHttpResponse response = null;

        try {
            // Check that we're only validating HTTP(S) locations.
            if (!HTTP_PROTOCOL.equals(url.getProtocol()) && !HTTPS_PROTOCOL.equals(url.getProtocol())) {
                return createErrorIcon("Unsupported protocol: " + url.getProtocol(), verbose);
            }

            LOG.debug("connecting to url: " + url);

            HttpGet httpGet = new HttpGet(url.toURI());
            httpGet.setConfig(requestConfig);
            response = httpClient.execute(httpGet);

            return createHttpStatusIcon(response.getStatusLine().getStatusCode(), response.getStatusLine().getReasonPhrase(), verbose);
        } catch (URISyntaxException e) {
            LOG.error("Invalid URL: " + url.toString(), e);
            return createErrorIcon("Invalid URL: " + e, verbose);
        } catch (IOException e) {
            LOG.error("Failed to execute HTTP GET to URL: " + url.toString(), e);
            return createErrorIcon(e.getLocalizedMessage(), verbose);
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    static String createHttpStatusIcon(int responseCode, String responseMessage, boolean verbose)
    {
        String icon = UNKNOWN_ICON;
        if (responseCode >= 100 && responseCode < 200)
            icon = INFO_ICON;
        else if (responseCode < 300)
            icon = GOOD_ICON;
        else if (responseCode < 400)
            icon = REDIRECT_ICON;
        else if (responseCode < 500)
            icon = FORBIDDEN_ICON;
        else if (responseCode < 600) icon = ERROR_ICON;

        return createIcon(icon, responseMessage + " (" + responseCode + ")", verbose);
    }

    private static String createIcon(String iconPath, String status, boolean verbose)
    {
        StringBuffer out = new StringBuffer();

        // FIXME [20130402 YKT] Please remove the following method, if you have found a better way to resolve icon path
        // Could not find any confluence API that able the resolve icon path that backward compatible.
        iconPath = resolvedIconExtensions(iconPath);

        out.append("<img");
        out.append(" src='").append(getContextPath()).append("/images/icons/").append(iconPath).append("'");
        if (status != null) out.append(" title='").append(GeneralUtil.escapeXml(status)).append("'");
        out.append("/>");

        if (verbose && status != null) out.append(" ").append(GeneralUtil.escapeXml(status));

        return out.toString();
    }

    private static String getContextPath()
    {
        return getBootstrapManager().getWebAppContextPath();
    }

    public static BootstrapManager getBootstrapManager()
    {
        if (bootstrapManager == null)
            bootstrapManager = (BootstrapManager) ContainerManager.getComponent("bootstrapManager");
        return bootstrapManager;
    }

    public static String createInfoIcon(String status, boolean verbose)
    {
        return createIcon(INFO_ICON, status, verbose);
    }

    public static String createGoodIcon(String status, boolean verbose)
    {
        return createIcon(GOOD_ICON, status, verbose);
    }

    public static String createUnknownIcon(String status, boolean verbose)
    {
        return createIcon(UNKNOWN_ICON, status, verbose);
    }

    public static String createErrorIcon(String status, boolean verbose)
    {
        return createIcon(ERROR_ICON, status, verbose);
    }

    public static String createRedirectIcon(String status, boolean verbose)
    {
        return createIcon(REDIRECT_ICON, status, verbose);
    }

    public static String createForbiddenIcon(String status, boolean verbose)
    {
        return createIcon(FORBIDDEN_ICON, status, verbose);
    }

    /**
     * Return a given icon path to a converted one (to {@code .png}) from any other formats.
     * It is a behavior introduced since Confluence 5 that all icons are created as PNG.
     */
    private static String resolvedIconExtensions(String iconPath){

        if(GeneralUtil.getVersionNumber().compareToIgnoreCase("5.0") >= 0){
            int lastDotPos = iconPath.lastIndexOf('.');
            if( lastDotPos != -1){
                return iconPath.substring(0, lastDotPos) + ".png";
            }
        }
        return iconPath;
    }

}
