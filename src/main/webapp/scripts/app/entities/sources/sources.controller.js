'use strict';

angular.module('stiraApp')
    .controller('SourcesController', function ($scope, $state, Sources) {

        $scope.sourcess = [];
        $scope.loadAll = function() {
            Sources.query(function(result) {
               $scope.sourcess = result;
            });
        };
        $scope.loadAll();


        $scope.refresh = function () {
            $scope.loadAll();
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.sources = {
                name: null,
                syncedTo: null,
                id: null
            };
        };
    });
