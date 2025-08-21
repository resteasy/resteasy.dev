package org.resteasy.site.model;

import io.quarkiverse.roq.data.runtime.annotations.DataMapping;

import java.util.List;

@DataMapping(value = "grpcreleases", parentArray = true)
public record GrpcReleases(List<Release> list) {
}
