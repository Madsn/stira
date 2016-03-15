'use strict';

angular.module('stiraApp')
    .factory('Sources', function ($resource, DateUtils) {
        return $resource('api/sourcess/:id', {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    data.syncedTo = DateUtils.convertLocaleDateFromServer(data.syncedTo);
                    return data;
                }
            },
            'update': {
                method: 'PUT',
                transformRequest: function (data) {
                    data.syncedTo = DateUtils.convertLocaleDateToServer(data.syncedTo);
                    return angular.toJson(data);
                }
            },
            'save': {
                method: 'POST',
                transformRequest: function (data) {
                    data.syncedTo = DateUtils.convertLocaleDateToServer(data.syncedTo);
                    return angular.toJson(data);
                }
            }
        });
    });
