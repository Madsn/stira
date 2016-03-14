'use strict';

angular.module('stiraApp')
	.controller('TicketDeleteController', function($scope, $uibModalInstance, entity, Ticket) {

        $scope.ticket = entity;
        $scope.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
        $scope.confirmDelete = function (id) {
            Ticket.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        };

    });
