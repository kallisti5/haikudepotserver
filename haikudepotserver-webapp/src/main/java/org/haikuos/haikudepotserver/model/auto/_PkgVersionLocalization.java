package org.haikuos.haikudepotserver.model.auto;

import org.haikuos.haikudepotserver.model.PkgVersion;
import org.haikuos.haikudepotserver.model.support.AbstractDataObject;

/**
 * Class _PkgVersionLocalization was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _PkgVersionLocalization extends AbstractDataObject {

    public static final String DESCRIPTION_PROPERTY = "description";
    public static final String SUMMARY_PROPERTY = "summary";
    public static final String PKG_VERSION_PROPERTY = "pkgVersion";

    public static final String ID_PK_COLUMN = "id";

    public void setDescription(String description) {
        writeProperty(DESCRIPTION_PROPERTY, description);
    }
    public String getDescription() {
        return (String)readProperty(DESCRIPTION_PROPERTY);
    }

    public void setSummary(String summary) {
        writeProperty(SUMMARY_PROPERTY, summary);
    }
    public String getSummary() {
        return (String)readProperty(SUMMARY_PROPERTY);
    }

    public void setPkgVersion(PkgVersion pkgVersion) {
        setToOneTarget(PKG_VERSION_PROPERTY, pkgVersion, true);
    }

    public PkgVersion getPkgVersion() {
        return (PkgVersion)readProperty(PKG_VERSION_PROPERTY);
    }


}
