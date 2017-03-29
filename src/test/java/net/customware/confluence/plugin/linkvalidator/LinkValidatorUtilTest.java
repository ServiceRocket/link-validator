package net.customware.confluence.plugin.linkvalidator;

import com.atlassian.confluence.setup.BootstrapManager;
import com.atlassian.spring.container.ContainerContext;
import com.atlassian.spring.container.ContainerManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static net.customware.confluence.plugin.linkvalidator.LinkValidatorUtil.createGoodIcon;
import static net.customware.confluence.plugin.linkvalidator.LinkValidatorUtil.createInfoIcon;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

/**
 * @author yasir
 * @author yclian
 * @since 2.1.0-SNAPSHOT-01042013
 * @version 2.1.0-SNAPSHOT-01042013
 */
@RunWith(MockitoJUnitRunner.class)
public class LinkValidatorUtilTest {

    @Mock BootstrapManager bootstrapManager;
    @Mock ContainerContext containerContext;

    @Before public void setUpContext() {

        ContainerManager.getInstance().setContainerContext(containerContext);

        when(containerContext.getComponent("bootstrapManager")).thenReturn(bootstrapManager);
        when(bootstrapManager.getWebAppContextPath()).thenReturn("");
    }

    @After public void tearDownContext() {
        ContainerManager.getInstance().setContainerContext(null);
    }

    @Test public void createIcons() {

        String goodIcon = createGoodIcon("good", true);
        String infoIcon = createInfoIcon("The information is informative", true);

        assertThat(goodIcon, containsString("<img src='/images/icons/emoticons/check.png' title='good'/> good"));
        assertThat(infoIcon, containsString("<img src='/images/icons/emoticons/information.png' title='The information is informative'/> The information is informative"));
    }
}
