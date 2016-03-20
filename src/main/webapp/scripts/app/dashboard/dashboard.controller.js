'use strict';

angular.module('stiraApp')
    .controller('DashboardController', function ($scope, $state, Ticket) {

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
                jiraStatus: null,
                stormStatus: null,
                jiraLastUpdated: null,
                stormLastUpdated: null,
                mutedUntil: null,
                id: null
            };
        };

        $scope.getRowAlertClasses = function(ticket) {
            if (ticket.jiraStatus === ticket.stormStatus) {
                return '';
            } else if (ticket.stormStatus === 'WAITING_SSE') {
                return 'alert alert-danger';
            } else if (ticket.stormStatus === 'WAITING_SKAT') {
                return 'alert alert-info';
            } else {
                return 'alert alert-warning';
            }
        };
    });
