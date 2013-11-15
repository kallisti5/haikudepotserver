/*
 * Copyright 2013, Andrew Lindesay
 * Distributed under the terms of the MIT License.
 */

package org.haikuos.haikudepotserver.model;

import org.apache.cayenne.validation.SimpleValidationFailure;
import org.apache.cayenne.validation.ValidationResult;
import org.haikuos.haikudepotserver.model.auto._PkgVersionUrl;

import java.net.MalformedURLException;
import java.net.URL;

public class PkgVersionUrl extends _PkgVersionUrl {

    @Override
    protected void validateForSave(ValidationResult validationResult) {
        super.validateForSave(validationResult);

        if(null != getUrl()) {
            try {
                    new URL(getUrl());
            }
            catch(MalformedURLException mue) {
                validationResult.addFailure(new SimpleValidationFailure(this,URL_PROPERTY + ".malformed"));
            }
        }

    }

}
