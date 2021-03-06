package org.haiku.haikudepotserver.dataobjects.auto;

import org.haiku.haikudepotserver.dataobjects.Repository;
import org.haiku.haikudepotserver.dataobjects.support.AbstractDataObject;

/**
 * Class _RepositorySource was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _RepositorySource extends AbstractDataObject {

    public static final String ACTIVE_PROPERTY = "active";
    public static final String CODE_PROPERTY = "code";
    public static final String URL_PROPERTY = "url";
    public static final String REPOSITORY_PROPERTY = "repository";

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

    public void setUrl(String url) {
        writeProperty(URL_PROPERTY, url);
    }
    public String getUrl() {
        return (String)readProperty(URL_PROPERTY);
    }

    public void setRepository(Repository repository) {
        setToOneTarget(REPOSITORY_PROPERTY, repository, true);
    }

    public Repository getRepository() {
        return (Repository)readProperty(REPOSITORY_PROPERTY);
    }


}
