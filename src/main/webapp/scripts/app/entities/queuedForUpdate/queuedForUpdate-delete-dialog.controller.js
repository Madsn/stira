'use strict';

angular.module('stiraApp')
	.controller('QueuedForUpdateDeleteController', function($scope, $uibModalInstance, entity, QueuedForUpdate) {

        $scope.queuedForUpdate = entity;
        $scope.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
        $scope.confirmDelete = function (id) {
            QueuedForUpdate.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        };

    });
