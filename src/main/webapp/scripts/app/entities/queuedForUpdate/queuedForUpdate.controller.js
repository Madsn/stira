'use strict';

angular.module('stiraApp')
    .controller('QueuedForUpdateController', function ($scope, $state, QueuedForUpdate) {

        $scope.queuedForUpdates = [];
        $scope.loadAll = function() {
            QueuedForUpdate.query(function(result) {
               $scope.queuedForUpdates = result;
            });
        };
        $scope.loadAll();


        $scope.refresh = function () {
            $scope.loadAll();
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.queuedForUpdate = {
                ticketSource: null,
                addedToQueue: null,
                ticketKey: null,
                id: null
            };
        };
    });
