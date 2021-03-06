package org.haiku.haikudepotserver.dataobjects.auto;

import java.util.Date;

import org.haiku.haikudepotserver.dataobjects.Pkg;
import org.haiku.haikudepotserver.dataobjects.support.AbstractDataObject;

/**
 * Class _PkgChangelog was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _PkgChangelog extends AbstractDataObject {

    public static final String CONTENT_PROPERTY = "content";
    public static final String CREATE_TIMESTAMP_PROPERTY = "createTimestamp";
    public static final String MODIFY_TIMESTAMP_PROPERTY = "modifyTimestamp";
    public static final String PKG_PROPERTY = "pkg";

    public static final String ID_PK_COLUMN = "id";

    public void setContent(String content) {
        writeProperty(CONTENT_PROPERTY, content);
    }
    public String getContent() {
        return (String)readProperty(CONTENT_PROPERTY);
    }

    public void setCreateTimestamp(Date createTimestamp) {
        writeProperty(CREATE_TIMESTAMP_PROPERTY, createTimestamp);
    }
    public Date getCreateTimestamp() {
        return (Date)readProperty(CREATE_TIMESTAMP_PROPERTY);
    }

    public void setModifyTimestamp(Date modifyTimestamp) {
        writeProperty(MODIFY_TIMESTAMP_PROPERTY, modifyTimestamp);
    }
    public Date getModifyTimestamp() {
        return (Date)readProperty(MODIFY_TIMESTAMP_PROPERTY);
    }

    public void setPkg(Pkg pkg) {
        setToOneTarget(PKG_PROPERTY, pkg, true);
    }

    public Pkg getPkg() {
        return (Pkg)readProperty(PKG_PROPERTY);
    }


}
