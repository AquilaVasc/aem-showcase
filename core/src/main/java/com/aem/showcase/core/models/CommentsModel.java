
package com.aem.showcase.core.models;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

@Model(
        adaptables = SlingHttpServletRequest.class,
        adapters = { CommentsModel.class, ComponentExporter.class },
        resourceType = CommentsModel.RESOURCE_TYPE,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME,
        extensions = ExporterConstants.SLING_MODEL_EXTENSION
)
@JsonSerialize(as = CommentsModel.class)
public class CommentsModel implements ComponentExporter{ 

    static final String RESOURCE_TYPE = "aem-showcase/components/comments";

    @ValueMapValue
    private String commentTitle;

    @Override
    public String getExportedType() {
        return RESOURCE_TYPE;
    }

    public static String getResourceType() {
        return RESOURCE_TYPE;
    }

    public String getCommentTitle() {
        return commentTitle;
    }
}