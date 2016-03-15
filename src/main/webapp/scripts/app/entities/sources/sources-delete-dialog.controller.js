'use strict';

angular.module('stiraApp')
	.controller('SourcesDeleteController', function($scope, $uibModalInstance, entity, Sources) {

        $scope.sources = entity;
        $scope.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
        $scope.confirmDelete = function (id) {
            Sources.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        };

    });
