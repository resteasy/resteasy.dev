package org.resteasy.site;

import io.quarkus.qute.TemplateExtension;

import java.text.SimpleDateFormat;
import java.util.Date;

@TemplateExtension
public class Extensions {
    public static String format(Date date, String format) {
        return new SimpleDateFormat(format).format(date);
    }
}
