package org.haiku.haikudepotserver.dataobjects.auto;

import java.util.Date;

import org.haiku.haikudepotserver.dataobjects.NaturalLanguage;
import org.haiku.haikudepotserver.dataobjects.Pkg;
import org.haiku.haikudepotserver.dataobjects.support.AbstractDataObject;

/**
 * Class _PkgLocalization was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _PkgLocalization extends AbstractDataObject {

    public static final String CREATE_TIMESTAMP_PROPERTY = "createTimestamp";
    public static final String DESCRIPTION_PROPERTY = "description";
    public static final String MODIFY_TIMESTAMP_PROPERTY = "modifyTimestamp";
    public static final String SUMMARY_PROPERTY = "summary";
    public static final String TITLE_PROPERTY = "title";
    public static final String NATURAL_LANGUAGE_PROPERTY = "naturalLanguage";
    public static final String PKG_PROPERTY = "pkg";

    public static final String ID_PK_COLUMN = "id";

    public void setCreateTimestamp(Date createTimestamp) {
        writeProperty(CREATE_TIMESTAMP_PROPERTY, createTimestamp);
    }
    public Date getCreateTimestamp() {
        return (Date)readProperty(CREATE_TIMESTAMP_PROPERTY);
    }

    public void setDescription(String description) {
        writeProperty(DESCRIPTION_PROPERTY, description);
    }
    public String getDescription() {
        return (String)readProperty(DESCRIPTION_PROPERTY);
    }

    public void setModifyTimestamp(Date modifyTimestamp) {
        writeProperty(MODIFY_TIMESTAMP_PROPERTY, modifyTimestamp);
    }
    public Date getModifyTimestamp() {
        return (Date)readProperty(MODIFY_TIMESTAMP_PROPERTY);
    }

    public void setSummary(String summary) {
        writeProperty(SUMMARY_PROPERTY, summary);
    }
    public String getSummary() {
        return (String)readProperty(SUMMARY_PROPERTY);
    }

    public void setTitle(String title) {
        writeProperty(TITLE_PROPERTY, title);
    }
    public String getTitle() {
        return (String)readProperty(TITLE_PROPERTY);
    }

    public void setNaturalLanguage(NaturalLanguage naturalLanguage) {
        setToOneTarget(NATURAL_LANGUAGE_PROPERTY, naturalLanguage, true);
    }

    public NaturalLanguage getNaturalLanguage() {
        return (NaturalLanguage)readProperty(NATURAL_LANGUAGE_PROPERTY);
    }


    public void setPkg(Pkg pkg) {
        setToOneTarget(PKG_PROPERTY, pkg, true);
    }

    public Pkg getPkg() {
        return (Pkg)readProperty(PKG_PROPERTY);
    }


}
