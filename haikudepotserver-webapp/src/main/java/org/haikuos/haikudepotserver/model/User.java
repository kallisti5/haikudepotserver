/*
 * Copyright 2013, Andrew Lindesay
 * Distributed under the terms of the MIT License.
 */

package org.haikuos.haikudepotserver.model;

import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.common.hash.Hashing;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.cayenne.validation.SimpleValidationFailure;
import org.apache.cayenne.validation.ValidationFailure;
import org.apache.cayenne.validation.ValidationResult;
import org.haikuos.haikudepotserver.model.auto._User;

import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

public class User extends _User {

    public final static Pattern NICKNAME_PATTERN = Pattern.compile("^[\\w]{4,16}$");
    public final static Pattern PASSWORDHASH_PATTERN = Pattern.compile("^[a-f0-9]{64}$");
    public final static Pattern PASSWORDSALT_PATTERN = Pattern.compile("^[a-f0-9]{64}$");

    public static Optional<User> getByNickname(ObjectContext context, String nickname) {
        return Optional.fromNullable(Iterables.getOnlyElement(
                (List<User>) context.performQuery(new SelectQuery(
                        User.class,
                        ExpressionFactory.matchExp(User.NICKNAME_PROPERTY, nickname))),
                null));
    }

    // configured as a listener method in the model.

    public void onPostAdd() {

        if(null==getIsRoot()) {
            setIsRoot(Boolean.FALSE);
        }

        if(null==getCanManageUsers()) {
            setCanManageUsers(Boolean.FALSE);
        }

        if(null==getActive()) {
            setActive(Boolean.TRUE);
        }

        if(null==getPasswordSalt()) {
            setPasswordSalt(UUID.randomUUID().toString());
        }

    }

    @Override
    protected void validateForSave(ValidationResult validationResult) {
        super.validateForSave(validationResult);

        if(null==getIsRoot()) {
            setIsRoot(Boolean.FALSE);
        }

        if(null != getNickname()) {
            if(!NICKNAME_PATTERN.matcher(getNickname()).matches()) {
                validationResult.addFailure(new SimpleValidationFailure(this,NICKNAME_PROPERTY + ".malformed"));
            }
        }

        if(null != getPasswordHash()) {
            if(!PASSWORDHASH_PATTERN.matcher(getPasswordHash()).matches()) {
                validationResult.addFailure(new SimpleValidationFailure(this,PASSWORD_HASH_PROPERTY + ".malformed"));
            }
        }

        if(null != getPasswordSalt()) {
            if(!PASSWORDSALT_PATTERN.matcher(getPasswordSalt()).matches()) {
                validationResult.addFailure(new SimpleValidationFailure(this,PASSWORD_HASH_PROPERTY + ".malformed"));
            }
        }

    }

    /**
     * <p>This method will configure a random salt value.</p>
     */

    public void setPasswordSalt() {
        setPasswordSalt(Hashing.sha256().hashString(UUID.randomUUID().toString()).toString());
    }

}
