/*
 * Copyright 2013, Andrew Lindesay
 * Distributed under the terms of the MIT License.
 */

/**
 * <p>This service is here to maintain the current user's state.  When the user logs in for example, this is stored
 * here.  This service may take other actions such as configuring headers in the jsonRpc service when the user logs-in
 * or logs-out.</p>
 */

angular.module('haikudepotserver').factory('userState',
    [
        '$log','$q','jsonRpc','referenceData','constants',
        function($log, $q, jsonRpc,referenceData,constants) {

            var architecture = undefined;
            var user = undefined;

            var UserState = {

                /**
                 * <p>This function will return a promise to get the current architecture that the user would like to
                 * see.</p>
                 */

                architecture : function(value) {

                    if(undefined !== value) {
                        architecture = value;
                        $log.info('set architecture; '+value.code);
                    }

                    var deferred = $q.defer();

                    if(!architecture) {
                        referenceData.architecture(constants.ARCHITECTURE_CODE_DEFAULT).then(
                            function(data) {
                                if(!data) {
                                    $log.error('architecture \''+constants.ARCHITECTURE_CODE_DEFAULT+'\' unknown');
                                    deferred.reject();
                                }
                                else {
                                    architecture = data;
                                    deferred.resolve(data);
                                }
                            },
                            function() {
                                deferred.reject();
                            }
                        );
                    }
                    else {
                        deferred.resolve(architecture);
                    }

                    return deferred.promise;
                },

                /**
                 * <p>Invoked with no argument, this function will return the user.  If it is supplied with null then
                 * it will set the current user to empty.  If it is supplied with a user value, it will configure the
                 * user.</p>
                 */

                user : function(value) {
                    if(undefined !== value) {
                        if(null==value) {
                            user = undefined;
                            jsonRpc.setHeader('Authorization'); // remove this header.
                        }
                        else {

                            if(!value.nickname) {
                                throw 'the nickname is required when setting a user';
                            }

                            if(!value.passwordClear) {
                                throw 'the password clear is required when setting a user';
                            }

                            jsonRpc.setHeader(
                                'Authorization',
                                'Basic '+window.btoa(''+value.nickname+':'+value.passwordClear));

                            user = value;
                            $log.info('have set user; '+user.nickname);
                        }
                    }

                    return user;
                }

            };

            return UserState;

        }
    ]
);