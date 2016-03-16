'use strict';

angular.module('stiraApp')
	.controller('QueueSourceDeleteController', function($scope, $uibModalInstance, entity, QueueSource) {

        $scope.queueSource = entity;
        $scope.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
        $scope.confirmDelete = function (id) {
            QueueSource.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        };

    });
