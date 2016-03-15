'use strict';

angular.module('stiraApp')
    .controller('QueuedForUpdateDetailController', function ($scope, $rootScope, $stateParams, entity, QueuedForUpdate) {
        $scope.queuedForUpdate = entity;
        $scope.load = function (id) {
            QueuedForUpdate.get({id: id}, function(result) {
                $scope.queuedForUpdate = result;
            });
        };
        var unsubscribe = $rootScope.$on('stiraApp:queuedForUpdateUpdate', function(event, result) {
            $scope.queuedForUpdate = result;
        });
        $scope.$on('$destroy', unsubscribe);

    });
