package org.resteasy.site.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Spec(
        String version,
        String link,
        @JsonProperty("java_doc")
        String javadoc
) {
}
