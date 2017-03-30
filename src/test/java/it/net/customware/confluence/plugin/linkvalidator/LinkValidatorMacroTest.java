package it.net.customware.confluence.plugin.linkvalidator;

import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.content.ContentRepresentation;
import com.atlassian.confluence.webdriver.pageobjects.page.content.ViewPage;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;

public class LinkValidatorMacroTest extends DefaultStatelessTestRunner {
    @Test public void shouldSuccessIfValidLink() {
        Content content = createPage("shouldSuccessIfValidLink", "{link-validator:url=" + product.getProductInstance().getBaseUrl() + "|verbose=true}{link-validator}", ContentRepresentation.WIKI);
        ViewPage viewPage = product.viewPage(content);

        webDriver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        assertThat(viewPage.getMainContent().getText(), containsString("OK (200)"));
    }

    @Test public void shouldFailIfInvalidLink() {
        Content content = createPage("shouldFailIfInvalidLink", "{link-validator:url=" + product.getProductInstance().getBaseUrl() + "/fail|verbose=true}{link-validator}", ContentRepresentation.WIKI);
        ViewPage viewPage = product.viewPage(content);

        webDriver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        assertThat(viewPage.getMainContent().getText(), containsString("Not Found (404)"));
    }
}
