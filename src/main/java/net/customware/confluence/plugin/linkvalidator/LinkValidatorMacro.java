package net.customware.confluence.plugin.linkvalidator;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.core.ContextPathHolder;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.renderer.radeox.macros.MacroUtils;
import com.atlassian.confluence.util.velocity.VelocityUtils;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.macro.BaseMacro;
import com.atlassian.renderer.v2.macro.MacroException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

import static com.atlassian.confluence.util.GeneralUtil.escapeXml;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class LinkValidatorMacro extends BaseMacro implements Macro {

    private ContextPathHolder contextPathHolder;

    @Override
    public String execute(Map map, String s, RenderContext renderContext) throws MacroException {
        try {
            return execute(map, s, new DefaultConversionContext(renderContext));
        } catch (MacroExecutionException e) {
            throw new MacroException(e);
        }
    }

    @Override
    public String execute(Map<String, String> params, String body, ConversionContext conversionContext) throws MacroExecutionException {
        String link = isNotBlank(params.get("url")) ? params.get("url") : null;
        if (isBlank(link)) {
            throw new MacroExecutionException("Please supply the URL to validate.");
        }

        // If true, output the message in full.
        boolean verbose = isNotBlank(params.get("verbose")) ? Boolean.valueOf(params.get("verbose")) : false;
        // The timeout in seconds.
        int timeout = isNotBlank(params.get("timeout")) ? Integer.valueOf(params.get("timeout")) : 5;

        Map<String,Object> contextMap = MacroUtils.defaultVelocityContext();
        contextMap.put("contextPath", contextPathHolder.getContextPath());
        contextMap.put("url", escapeXml(link));
        contextMap.put("timeout", timeout);
        contextMap.put("verbose", verbose);
        contextMap.put("icon", LinkValidatorUtil.createUnknownIcon("Unvalidated", verbose));

        return VelocityUtils.getRenderedTemplate("net/customware/confluence/plugin/linkvalidator/macro/link-validator.vm", contextMap);
    }

    public BodyType getBodyType() {
        return BodyType.NONE;
    }

    public OutputType getOutputType() {
        return OutputType.INLINE;
    }

    @Autowired
    public void setContextPathHolder(ContextPathHolder contextPathHolder) {
        this.contextPathHolder = contextPathHolder;
    }

    @Override
    public boolean hasBody() {
        return true;
    }

    @Override
    public RenderMode getBodyRenderMode() {
        return RenderMode.suppress(RenderMode.F_FIRST_PARA + RenderMode.F_LINKS);
    }
}
