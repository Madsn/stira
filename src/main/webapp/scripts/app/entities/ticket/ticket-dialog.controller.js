'use strict';

angular.module('stiraApp').controller('TicketDialogController',
    ['$scope', '$stateParams', '$uibModalInstance', 'entity', 'Ticket',
        function($scope, $stateParams, $uibModalInstance, entity, Ticket) {

        $scope.ticket = entity;
        $scope.load = function(id) {
            Ticket.get({id : id}, function(result) {
                $scope.ticket = result;
            });
        };

        var onSaveSuccess = function (result) {
            $scope.$emit('stiraApp:ticketUpdate', result);
            $uibModalInstance.close(result);
            $scope.isSaving = false;
        };

        var onSaveError = function (result) {
            $scope.isSaving = false;
        };

        $scope.save = function () {
            $scope.isSaving = true;
            if ($scope.ticket.id != null) {
                Ticket.update($scope.ticket, onSaveSuccess, onSaveError);
            } else {
                Ticket.save($scope.ticket, onSaveSuccess, onSaveError);
            }
        };

        $scope.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
        $scope.datePickerForJiraLastUpdated = {};

        $scope.datePickerForJiraLastUpdated.status = {
            opened: false
        };

        $scope.datePickerForJiraLastUpdatedOpen = function($event) {
            $scope.datePickerForJiraLastUpdated.status.opened = true;
        };

        $scope.datePickerForStormLastUpdated = {};

        $scope.datePickerForStormLastUpdated.status = {
            opened: false
        };

        $scope.datePickerForStormLastUpdatedOpen = function($event) {
            $scope.datePickerForStormLastUpdated.status.opened = true;
        };

        $scope.datePickerForMutedUntil = {};

        $scope.datePickerForMutedUntil.status = {
            opened: false
        };

        $scope.datePickerForMutedUntilOpen = function($event) {
            $scope.datePickerForMutedUntil.status.opened = true;
        };
}]);
