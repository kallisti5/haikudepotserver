/*
 * Copyright 2013, Andrew Lindesay
 * Distributed under the terms of the MIT License.
 */

package org.haikuos.pkg.model;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

public class PkgVersion {

    private String major;
    private String minor;
    private String micro;
    private String preRelease;
    private Integer revision;

    public PkgVersion(String major, String minor, String micro, String preRelease, Integer revision) {
        Preconditions.checkState(!Strings.isNullOrEmpty(major));
        this.major = major;
        this.minor = minor;
        this.micro = micro;
        this.preRelease = preRelease;
        this.revision = revision;
    }

    public String getMajor() {
        return major;
    }

    public String getMinor() {
        return minor;
    }

    public String getMicro() {
        return micro;
    }

    public String getPreRelease() {
        return preRelease;
    }

    public Integer getRevision() {
        return revision;
    }

    private void appendDotValue(StringBuilder stringBuilder, String value) {
        if(null!=value) {
            if(0!=stringBuilder.length()) {
                stringBuilder.append('.');
            }

            stringBuilder.append(value);
        }
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        appendDotValue(result,getMajor());
        appendDotValue(result,getMinor());
        appendDotValue(result,getMicro());
        appendDotValue(result, getPreRelease());
        appendDotValue(result, null == getRevision() ? null : getRevision().toString());
        return result.toString();
    }

}
