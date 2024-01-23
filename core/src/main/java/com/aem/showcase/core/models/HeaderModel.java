
package com.aem.showcase.core.models;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.aem.showcase.core.pojos.Link;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.List;

import javax.inject.Inject;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Via;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

@Model(
        adaptables = SlingHttpServletRequest.class,
        adapters = { HeaderModel.class, ComponentExporter.class },
        resourceType = HeaderModel.RESOURCE_TYPE,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME,
        extensions = ExporterConstants.SLING_MODEL_EXTENSION
)
@JsonSerialize(as = HeaderModel.class)
public class HeaderModel implements ComponentExporter{ 

    static final String RESOURCE_TYPE = "aem-showcase/components/header";

    @ValueMapValue
    String logoUrl;

    @Inject
    @Via("resource")
    public List<Link> links;

    @Override
    public String getExportedType() {
        return RESOURCE_TYPE;
    }

    public static String getResourceType() {
        return RESOURCE_TYPE;
    }

    public String getLogoUrl() {
        return logoUrl;
    }
}