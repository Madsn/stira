'use strict';

angular.module('stiraApp')
    .controller('DashboardController', function ($scope, $state, DashboardErr, DashboardWarn) {

        $scope.warn = true;
        $scope.tickets = [];
        $scope.loadWarn = function () {
            $scope.warn = true;
            DashboardWarn.query(function (result) {
                $scope.tickets = result;
            });
        };
        $scope.loadWarn();

        $scope.loadErr = function () {
            $scope.warn = false;
            DashboardErr.query(function (result) {
                $scope.tickets = result;
            });
        };


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
                jiraAssignee: null,
                id: null
            };
        };

        $scope.getRowAlertClasses = function (ticket) {
            if (ticket.jiraStatus === ticket.stormStatus) {
                return '';
            } else if (ticket.jiraStatus === null && ticket.stormStatus !== null) {
                return 'alert alert-danger';
            } else if (ticket.stormStatus === 'WAITING_SSE') {
                return 'alert alert-danger';
            } else if (ticket.stormStatus === 'WAITING_SKAT') {
                return 'alert alert-info';
            } else {
                return 'alert alert-warning';
            }
        };
    });
