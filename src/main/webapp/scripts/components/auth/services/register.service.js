'use strict';

angular.module('stiraApp')
    .factory('Register', function ($resource) {
        return $resource('api/register', {}, {
        });
    });


