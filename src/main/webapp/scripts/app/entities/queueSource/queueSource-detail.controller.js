'use strict';

angular.module('stiraApp')
    .controller('QueueSourceDetailController', function ($scope, $rootScope, $stateParams, entity, QueueSource) {
        $scope.queueSource = entity;
        $scope.load = function (id) {
            QueueSource.get({id: id}, function(result) {
                $scope.queueSource = result;
            });
        };
        var unsubscribe = $rootScope.$on('stiraApp:queueSourceUpdate', function(event, result) {
            $scope.queueSource = result;
        });
        $scope.$on('$destroy', unsubscribe);

    });
