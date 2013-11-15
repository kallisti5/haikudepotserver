package org.haikuos.haikudepotserver.model.auto;

import java.util.Date;
import java.util.List;

import org.haikuos.haikudepotserver.model.Pkg;
import org.haikuos.haikudepotserver.model.support.AbstractDataObject;

/**
 * Class _Publisher was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _Publisher extends AbstractDataObject {

    public static final String ACTIVE_PROPERTY = "active";
    public static final String CODE_PROPERTY = "code";
    public static final String CREATE_TIMESTAMP_PROPERTY = "createTimestamp";
    public static final String EMAIL_PROPERTY = "email";
    public static final String MODIFY_TIMESTAMP_PROPERTY = "modifyTimestamp";
    public static final String NAME_PROPERTY = "name";
    public static final String SITE_URL_PROPERTY = "siteUrl";
    public static final String PKGS_PROPERTY = "pkgs";

    public static final String ID_PK_COLUMN = "id";

    public void setActive(Boolean active) {
        writeProperty(ACTIVE_PROPERTY, active);
    }
    public Boolean getActive() {
        return (Boolean)readProperty(ACTIVE_PROPERTY);
    }

    public void setCode(String code) {
        writeProperty(CODE_PROPERTY, code);
    }
    public String getCode() {
        return (String)readProperty(CODE_PROPERTY);
    }

    public void setCreateTimestamp(Date createTimestamp) {
        writeProperty(CREATE_TIMESTAMP_PROPERTY, createTimestamp);
    }
    public Date getCreateTimestamp() {
        return (Date)readProperty(CREATE_TIMESTAMP_PROPERTY);
    }

    public void setEmail(String email) {
        writeProperty(EMAIL_PROPERTY, email);
    }
    public String getEmail() {
        return (String)readProperty(EMAIL_PROPERTY);
    }

    public void setModifyTimestamp(Date modifyTimestamp) {
        writeProperty(MODIFY_TIMESTAMP_PROPERTY, modifyTimestamp);
    }
    public Date getModifyTimestamp() {
        return (Date)readProperty(MODIFY_TIMESTAMP_PROPERTY);
    }

    public void setName(String name) {
        writeProperty(NAME_PROPERTY, name);
    }
    public String getName() {
        return (String)readProperty(NAME_PROPERTY);
    }

    public void setSiteUrl(String siteUrl) {
        writeProperty(SITE_URL_PROPERTY, siteUrl);
    }
    public String getSiteUrl() {
        return (String)readProperty(SITE_URL_PROPERTY);
    }

    public void addToPkgs(Pkg obj) {
        addToManyTarget(PKGS_PROPERTY, obj, true);
    }
    public void removeFromPkgs(Pkg obj) {
        removeToManyTarget(PKGS_PROPERTY, obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<Pkg> getPkgs() {
        return (List<Pkg>)readProperty(PKGS_PROPERTY);
    }


}
