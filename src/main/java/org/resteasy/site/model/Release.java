package org.resteasy.site.model;

import java.util.List;

public record Release(
        String group,
        boolean supported,
        List<ReleaseDetail> detail
) {
}
