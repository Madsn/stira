'use strict';

angular.module('stiraApp').controller('QueuedForUpdateDialogController',
    ['$scope', '$stateParams', '$uibModalInstance', 'entity', 'QueuedForUpdate',
        function($scope, $stateParams, $uibModalInstance, entity, QueuedForUpdate) {

        $scope.queuedForUpdate = entity;
        $scope.load = function(id) {
            QueuedForUpdate.get({id : id}, function(result) {
                $scope.queuedForUpdate = result;
            });
        };

        var onSaveSuccess = function (result) {
            $scope.$emit('stiraApp:queuedForUpdateUpdate', result);
            $uibModalInstance.close(result);
            $scope.isSaving = false;
        };

        var onSaveError = function (result) {
            $scope.isSaving = false;
        };

        $scope.save = function () {
            $scope.isSaving = true;
            if ($scope.queuedForUpdate.id != null) {
                QueuedForUpdate.update($scope.queuedForUpdate, onSaveSuccess, onSaveError);
            } else {
                QueuedForUpdate.save($scope.queuedForUpdate, onSaveSuccess, onSaveError);
            }
        };

        $scope.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
        $scope.datePickerForAddedToQueue = {};

        $scope.datePickerForAddedToQueue.status = {
            opened: false
        };

        $scope.datePickerForAddedToQueueOpen = function($event) {
            $scope.datePickerForAddedToQueue.status.opened = true;
        };
}]);
