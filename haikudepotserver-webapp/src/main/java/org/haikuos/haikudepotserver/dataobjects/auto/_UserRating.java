package org.haikuos.haikudepotserver.dataobjects.auto;

import java.util.Date;

import org.haikuos.haikudepotserver.dataobjects.NaturalLanguage;
import org.haikuos.haikudepotserver.dataobjects.PkgVersion;
import org.haikuos.haikudepotserver.dataobjects.User;
import org.haikuos.haikudepotserver.dataobjects.UserRatingStability;
import org.haikuos.haikudepotserver.dataobjects.support.AbstractDataObject;

/**
 * Class _UserRating was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _UserRating extends AbstractDataObject {

    public static final String ACTIVE_PROPERTY = "active";
    public static final String COMMENT_PROPERTY = "comment";
    public static final String CREATE_TIMESTAMP_PROPERTY = "createTimestamp";
    public static final String MODIFY_TIMESTAMP_PROPERTY = "modifyTimestamp";
    public static final String RATING_PROPERTY = "rating";
    public static final String NATURAL_LANGUAGE_PROPERTY = "naturalLanguage";
    public static final String PKG_VERSION_PROPERTY = "pkgVersion";
    public static final String USER_PROPERTY = "user";
    public static final String USER_RATING_STABILITY_PROPERTY = "userRatingStability";

    public static final String ID_PK_COLUMN = "id";

    public void setActive(Boolean active) {
        writeProperty(ACTIVE_PROPERTY, active);
    }
    public Boolean getActive() {
        return (Boolean)readProperty(ACTIVE_PROPERTY);
    }

    public void setComment(String comment) {
        writeProperty(COMMENT_PROPERTY, comment);
    }
    public String getComment() {
        return (String)readProperty(COMMENT_PROPERTY);
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

    public void setRating(Short rating) {
        writeProperty(RATING_PROPERTY, rating);
    }
    public Short getRating() {
        return (Short)readProperty(RATING_PROPERTY);
    }

    public void setNaturalLanguage(NaturalLanguage naturalLanguage) {
        setToOneTarget(NATURAL_LANGUAGE_PROPERTY, naturalLanguage, true);
    }

    public NaturalLanguage getNaturalLanguage() {
        return (NaturalLanguage)readProperty(NATURAL_LANGUAGE_PROPERTY);
    }


    public void setPkgVersion(PkgVersion pkgVersion) {
        setToOneTarget(PKG_VERSION_PROPERTY, pkgVersion, true);
    }

    public PkgVersion getPkgVersion() {
        return (PkgVersion)readProperty(PKG_VERSION_PROPERTY);
    }


    public void setUser(User user) {
        setToOneTarget(USER_PROPERTY, user, true);
    }

    public User getUser() {
        return (User)readProperty(USER_PROPERTY);
    }


    public void setUserRatingStability(UserRatingStability userRatingStability) {
        setToOneTarget(USER_RATING_STABILITY_PROPERTY, userRatingStability, true);
    }

    public UserRatingStability getUserRatingStability() {
        return (UserRatingStability)readProperty(USER_RATING_STABILITY_PROPERTY);
    }


}