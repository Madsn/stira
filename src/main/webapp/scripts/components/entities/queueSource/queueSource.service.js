'use strict';

angular.module('stiraApp')
    .factory('QueueSource', function ($resource, DateUtils) {
        return $resource('api/queueSources/:id', {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    data.lastAddedTicket = DateUtils.convertLocaleDateFromServer(data.lastAddedTicket);
                    return data;
                }
            },
            'update': {
                method: 'PUT',
                transformRequest: function (data) {
                    data.lastAddedTicket = DateUtils.convertLocaleDateToServer(data.lastAddedTicket);
                    return angular.toJson(data);
                }
            },
            'save': {
                method: 'POST',
                transformRequest: function (data) {
                    data.lastAddedTicket = DateUtils.convertLocaleDateToServer(data.lastAddedTicket);
                    return angular.toJson(data);
                }
            }
        });
    });
