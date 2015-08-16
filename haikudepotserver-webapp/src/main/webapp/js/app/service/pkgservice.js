/*
 * Copyright 2014-2015, Andrew Lindesay
 * Distributed under the terms of the MIT License.
 */

/**
 * <p>This service provides functionality for accessing and updating the screenshots for a package.</p>
 */

angular.module('haikudepotserver').factory('pkg',
    [
        '$log','$q','jsonRpc','userState','errorHandling',
        'constants',
        function(
            $log,$q,jsonRpc,userState,errorHandling,
            constants) {

            /**
             * <p>This function will say if the package is a development package.</p>
             * @param {string} name
             */

            function isDevel(name) {
                return name && name.indexOf('_devel') == name.length - 6;
            }

            /**
             * <p>This function will fetch the package from the standard package version request params.</p>
             * @param {string} pkgName
             * @param {string} repositoryCode
             * @param {Object} versionCoordinates
             * @param {string} architectureCode
             * @param {boolean} incrementCounter
             */

            function getPkgWithSpecificVersion(pkgName, repositoryCode, versionCoordinates, architectureCode, incrementCounter) {

                if(!pkgName||!pkgName.length) {
                    throw Error('pkg name must be supplied');
                }

                if(!repositoryCode||!repositoryCode.length) {
                    throw Error('the repository code must be supplied');
                }

                if(!versionCoordinates||!versionCoordinates.major) {
                    throw Error('version coordinates must be supplied');
                }

                if(!architectureCode||!architectureCode.length) {
                    throw Error('architecture code must be supplied');
                }

                var deferred = $q.defer();

                jsonRpc.call(
                    constants.ENDPOINT_API_V1_PKG,
                    'getPkg',
                    [{
                        name : pkgName,
                        repositoryCode: repositoryCode,
                        versionType : 'SPECIFIC',
                        incrementViewCounter : !!incrementCounter,
                        architectureCode : architectureCode,
                        naturalLanguageCode: userState.naturalLanguageCode(),
                        major: versionCoordinates.major,
                        minor : versionCoordinates.minor,
                        micro : versionCoordinates.micro,
                        preRelease : versionCoordinates.preRelease,
                        revision : versionCoordinates.revision
                    }]
                ).then(
                    function(result) {
                        $log.info('fetched '+result.name+' pkg');
                        deferred.resolve(result);
                    },
                    function(err) {
                        errorHandling.logJsonRpcError(err ? err.error : null);
                        deferred.reject();
                    }
                );

                return deferred.promise;
            }

            /**
             * <p>This assumes some standard names in the route params and pull out the details required to
             * get the package and its version.</p>
             * @param {Object} routeParams
             * @param {boolean} incrementCounter
             */

            function getPkgWithSpecificVersionFromRouteParams(routeParams, incrementCounter) {

                if(!routeParams) {
                    throw Error('route params expected');
                }

                function hyphenToNull(val) {
                    if('-'==val) {
                        return null;
                    }

                    return val;
                }

                return getPkgWithSpecificVersion(
                    routeParams.name,
                    routeParams.repositoryCode,
                    {
                        major : hyphenToNull(routeParams.major),
                        minor : hyphenToNull(routeParams.minor),
                        micro : hyphenToNull(routeParams.micro),
                        preRelease : hyphenToNull(routeParams.preRelease),
                        revision : hyphenToNull(routeParams.revision)
                    },
                    routeParams.architectureCode,
                    incrementCounter);
            }

            return {
                getPkgWithSpecificVersionFromRouteParams : function(routeParams, incrementCounter) {
                    return getPkgWithSpecificVersionFromRouteParams(routeParams, incrementCounter);
                },

                isDevel : function(name) {
                    return isDevel(name);
                }
            };

        }
    ]
);