'use strict';

angular.module('stiraApp')
    .controller('SourcesDetailController', function ($scope, $rootScope, $stateParams, entity, Sources) {
        $scope.sources = entity;
        $scope.load = function (id) {
            Sources.get({id: id}, function(result) {
                $scope.sources = result;
            });
        };
        var unsubscribe = $rootScope.$on('stiraApp:sourcesUpdate', function(event, result) {
            $scope.sources = result;
        });
        $scope.$on('$destroy', unsubscribe);

    });
