package org.resteasy.site.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ReleaseDetail(
        String version,
        String date,
        String license,
        String source,
        String size,
        @JsonProperty("release_notes")
        String releaseNotes,
        @JsonProperty("download_link")
        String downloadLink,
        @JsonProperty("download_text")
        String downloadText,
        @JsonProperty("jakarta_rest_spec")
        Spec spec,
        Documentation documentation
) {
}
