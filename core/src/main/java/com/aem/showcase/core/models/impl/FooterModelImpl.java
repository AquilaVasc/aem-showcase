package com.aem.showcase.core.models.impl;

import java.util.Objects;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Via;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.apache.sling.models.annotations.via.ResourceSuperType;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.adobe.cq.wcm.core.components.models.Text;
import com.aem.showcase.core.models.iFooterModel;

@Model(
    adaptables = SlingHttpServletRequest.class,
    adapters = { FooterModelImpl.class,ComponentExporter.class},
    resourceType = FooterModelImpl.RESOURCE_TYPE,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME, extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class FooterModelImpl implements iFooterModel{

    @ValueMapValue
    String logoUrl;
    
    @Self
    @Via(type = ResourceSuperType.class)
    private Text text;

    static final String RESOURCE_TYPE = "aem-showcase/components/footer";
    
    @Override
    public String getExportedType() {
        return RESOURCE_TYPE;
    }

    public static String getResourceType() {
        return RESOURCE_TYPE;
    }

    public String getText() {
        return Objects.isNull(text) ? null : text.getText();
    }

    /**
     * Checks if the text to be displayed is rich text or not.
     *
     * @return {@code true} if the text is rich (HTML formatting), {@code false otherwise}
     * @since com.adobe.cq.wcm.core.components.models 11.0.0; marked <code>default</code> in 12.1.0
     */
    public boolean isRichText() {
        return Objects.isNull(text) ? null : text.isRichText();
    }

    @Override
    public String getLogoUrl() {
        return logoUrl;
    }   
}
