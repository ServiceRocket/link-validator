package net.customware.confluence.plugin.linkvalidator;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.macro.xhtml.MacroMigration;
import com.atlassian.confluence.xhtml.api.MacroDefinition;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class LinkValidatorMacroMigration implements MacroMigration {

    public MacroDefinition migrate(MacroDefinition macroDefinition, ConversionContext conversionContext) {
        Map<String, String> params = macroDefinition.getParameters();
        String url = params.get("url");
        if (StringUtils.isNotEmpty(url)) {
            return macroDefinition;
        }
        url = macroDefinition.getDefaultParameterValue();
        if (StringUtils.isEmpty(url)) {
            url = macroDefinition.getBodyText();
        }

        Map<String, String> newParams = new HashMap<String, String>();
        newParams.putAll(params);
        if (url != null) {
            newParams.put("url", url.trim());
        }
        return new MacroDefinition(macroDefinition.getName(), null, null, newParams);
    }

    /**
     {link-validator:verbose=true|timeout=10}
     http://www.google.com
     {link-validator}
     {link-validator:url=http://www.google.com|verbose=true|timeout=10}
     {link-validator:http://www.google.com|verbose=true|timeout=10}
     */
}
