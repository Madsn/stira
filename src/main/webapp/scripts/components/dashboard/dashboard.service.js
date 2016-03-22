'use strict';

angular.module('stiraApp')
    .factory('DashboardWarn', function ($resource) {
        return $resource('api/dashboard/ticketswarn', {}, {
            'query': { method: 'GET', isArray: true}
        });
    });

angular.module('stiraApp')
    .factory('DashboardErr', function ($resource) {
        return $resource('api/dashboard/ticketserr', {}, {
            'query': { method: 'GET', isArray: true}
        });
    });
