'use strict';

angular.module('stiraApp')
    .factory('QueuedForUpdate', function ($resource, DateUtils) {
        return $resource('api/queuedForUpdates/:id', {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    data.addedToQueue = DateUtils.convertLocaleDateFromServer(data.addedToQueue);
                    return data;
                }
            },
            'update': {
                method: 'PUT',
                transformRequest: function (data) {
                    data.addedToQueue = DateUtils.convertLocaleDateToServer(data.addedToQueue);
                    return angular.toJson(data);
                }
            },
            'save': {
                method: 'POST',
                transformRequest: function (data) {
                    data.addedToQueue = DateUtils.convertLocaleDateToServer(data.addedToQueue);
                    return angular.toJson(data);
                }
            }
        });
    });
