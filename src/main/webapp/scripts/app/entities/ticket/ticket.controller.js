'use strict';

angular.module('stiraApp')
    .controller('TicketController', function ($scope, $state, Ticket) {

        $scope.tickets = [];
        $scope.loadAll = function() {
            Ticket.query(function(result) {
               $scope.tickets = result;
            });
        };
        $scope.loadAll();


        $scope.refresh = function () {
            $scope.loadAll();
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.ticket = {
                stormKey: null,
                jiraKey: null,
                jiraTitle: null,
                stormTitle: null,
                jiraLastUpdated: null,
                stormLastUpdated: null,
                mutedUntil: null,
                id: null
            };
        };
    });
