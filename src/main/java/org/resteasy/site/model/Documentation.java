package org.resteasy.site.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Documentation(
        String examples,
        @JsonProperty("single_page")
        String singlePage,
        String link,
        String pdf,
        @JsonProperty("java_doc")
        String javadoc
) {
}
