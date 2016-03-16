'use strict';

angular.module('stiraApp')
    .controller('QueueSourceController', function ($scope, $state, QueueSource) {

        $scope.queueSources = [];
        $scope.loadAll = function() {
            QueueSource.query(function(result) {
               $scope.queueSources = result;
            });
        };
        $scope.loadAll();


        $scope.refresh = function () {
            $scope.loadAll();
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.queueSource = {
                ticketSource: null,
                lastAddedTicket: null,
                id: null
            };
        };
    });
