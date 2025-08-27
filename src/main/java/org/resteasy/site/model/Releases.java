package org.resteasy.site.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkiverse.roq.data.runtime.annotations.DataMapping;

import java.util.List;

@DataMapping(value = "releases", parentArray = true)
public record Releases(List<Release> list) {

}
