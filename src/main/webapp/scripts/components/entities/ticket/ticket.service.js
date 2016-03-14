'use strict';

angular.module('stiraApp')
    .factory('Ticket', function ($resource, DateUtils) {
        return $resource('api/tickets/:id', {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    data.jiraLastUpdated = DateUtils.convertLocaleDateToServer(data.jiraLastUpdated);
                    data.stormLastUpdated = DateUtils.convertLocaleDateToServer(data.stormLastUpdated);
                    data.mutedUntil = DateUtils.convertLocaleDateToServer(data.mutedUntil);
                    return data;
                }
            },
            'update': {
                method: 'PUT',
                transformRequest: function (data) {
                    data.jiraLastUpdated = DateUtils.convertLocaleDateToServer(data.jiraLastUpdated);
                    data.stormLastUpdated = DateUtils.convertLocaleDateToServer(data.stormLastUpdated);
                    data.mutedUntil = DateUtils.convertLocaleDateToServer(data.mutedUntil);
                    return angular.toJson(data);
                }
            },
            'save': {
                method: 'POST',
                transformRequest: function (data) {
                    data.jiraLastUpdated = DateUtils.convertLocaleDateToServer(data.jiraLastUpdated);
                    data.stormLastUpdated = DateUtils.convertLocaleDateToServer(data.stormLastUpdated);
                    data.mutedUntil = DateUtils.convertLocaleDateToServer(data.mutedUntil);
                    return angular.toJson(data);
                }
            }
        });
    });
