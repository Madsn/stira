'use strict';

angular.module('stiraApp').controller('SourcesDialogController',
    ['$scope', '$stateParams', '$uibModalInstance', 'entity', 'Sources',
        function($scope, $stateParams, $uibModalInstance, entity, Sources) {

        $scope.sources = entity;
        $scope.load = function(id) {
            Sources.get({id : id}, function(result) {
                $scope.sources = result;
            });
        };

        var onSaveSuccess = function (result) {
            $scope.$emit('stiraApp:sourcesUpdate', result);
            $uibModalInstance.close(result);
            $scope.isSaving = false;
        };

        var onSaveError = function (result) {
            $scope.isSaving = false;
        };

        $scope.save = function () {
            $scope.isSaving = true;
            if ($scope.sources.id != null) {
                Sources.update($scope.sources, onSaveSuccess, onSaveError);
            } else {
                Sources.save($scope.sources, onSaveSuccess, onSaveError);
            }
        };

        $scope.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
        $scope.datePickerForSyncedTo = {};

        $scope.datePickerForSyncedTo.status = {
            opened: false
        };

        $scope.datePickerForSyncedToOpen = function($event) {
            $scope.datePickerForSyncedTo.status.opened = true;
        };
}]);
