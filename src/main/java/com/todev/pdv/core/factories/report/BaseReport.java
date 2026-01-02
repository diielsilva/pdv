package com.todev.pdv.core.factories.report;

import java.util.List;

public interface BaseReport {
    String getTitle();

    List<String> getSubtitle();

    List<String> getContent();

    String getFooter();
}
