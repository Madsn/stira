'use strict';

angular.module('stiraApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('ticket', {
                parent: 'entity',
                url: '/tickets',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'Tickets'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/ticket/tickets.html',
                        controller: 'TicketController'
                    }
                },
                resolve: {
                }
            })
            .state('ticket.detail', {
                parent: 'entity',
                url: '/ticket/{id}',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'Ticket'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/ticket/ticket-detail.html',
                        controller: 'TicketDetailController'
                    }
                },
                resolve: {
                    entity: ['$stateParams', 'Ticket', function($stateParams, Ticket) {
                        return Ticket.get({id : $stateParams.id});
                    }]
                }
            })
            .state('ticket.new', {
                parent: 'ticket',
                url: '/new',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/entities/ticket/ticket-dialog.html',
                        controller: 'TicketDialogController',
                        size: 'lg',
                        resolve: {
                            entity: function () {
                                return {
                                    stormKey: null,
                                    jiraKey: null,
                                    jiraTitle: null,
                                    stormTitle: null,
                                    jiraLastUpdated: null,
                                    stormLastUpdated: null,
                                    mutedUntil: null,
                                    id: null
                                };
                            }
                        }
                    }).result.then(function(result) {
                        $state.go('ticket', null, { reload: true });
                    }, function() {
                        $state.go('ticket');
                    })
                }]
            })
            .state('ticket.edit', {
                parent: 'ticket',
                url: '/{id}/edit',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/entities/ticket/ticket-dialog.html',
                        controller: 'TicketDialogController',
                        size: 'lg',
                        resolve: {
                            entity: ['Ticket', function(Ticket) {
                                return Ticket.get({id : $stateParams.id});
                            }]
                        }
                    }).result.then(function(result) {
                        $state.go('ticket', null, { reload: true });
                    }, function() {
                        $state.go('^');
                    })
                }]
            })
            .state('ticket.delete', {
                parent: 'ticket',
                url: '/{id}/delete',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/entities/ticket/ticket-delete-dialog.html',
                        controller: 'TicketDeleteController',
                        size: 'md',
                        resolve: {
                            entity: ['Ticket', function(Ticket) {
                                return Ticket.get({id : $stateParams.id});
                            }]
                        }
                    }).result.then(function(result) {
                        $state.go('ticket', null, { reload: true });
                    }, function() {
                        $state.go('^');
                    })
                }]
            });
    });
