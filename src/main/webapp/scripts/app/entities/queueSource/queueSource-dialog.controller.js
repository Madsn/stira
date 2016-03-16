'use strict';

angular.module('stiraApp').controller('QueueSourceDialogController',
    ['$scope', '$stateParams', '$uibModalInstance', 'entity', 'QueueSource',
        function($scope, $stateParams, $uibModalInstance, entity, QueueSource) {

        $scope.queueSource = entity;
        $scope.load = function(id) {
            QueueSource.get({id : id}, function(result) {
                $scope.queueSource = result;
            });
        };

        var onSaveSuccess = function (result) {
            $scope.$emit('stiraApp:queueSourceUpdate', result);
            $uibModalInstance.close(result);
            $scope.isSaving = false;
        };

        var onSaveError = function (result) {
            $scope.isSaving = false;
        };

        $scope.save = function () {
            $scope.isSaving = true;
            if ($scope.queueSource.id != null) {
                QueueSource.update($scope.queueSource, onSaveSuccess, onSaveError);
            } else {
                QueueSource.save($scope.queueSource, onSaveSuccess, onSaveError);
            }
        };

        $scope.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
        $scope.datePickerForLastAddedTicket = {};

        $scope.datePickerForLastAddedTicket.status = {
            opened: false
        };

        $scope.datePickerForLastAddedTicketOpen = function($event) {
            $scope.datePickerForLastAddedTicket.status.opened = true;
        };
}]);
