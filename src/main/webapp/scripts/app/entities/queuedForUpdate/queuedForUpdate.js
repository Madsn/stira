'use strict';

angular.module('stiraApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('queuedForUpdate', {
                parent: 'entity',
                url: '/queuedForUpdates',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'QueuedForUpdates'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/queuedForUpdate/queuedForUpdates.html',
                        controller: 'QueuedForUpdateController'
                    }
                },
                resolve: {
                }
            })
            .state('queuedForUpdate.detail', {
                parent: 'entity',
                url: '/queuedForUpdate/{id}',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'QueuedForUpdate'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/queuedForUpdate/queuedForUpdate-detail.html',
                        controller: 'QueuedForUpdateDetailController'
                    }
                },
                resolve: {
                    entity: ['$stateParams', 'QueuedForUpdate', function($stateParams, QueuedForUpdate) {
                        return QueuedForUpdate.get({id : $stateParams.id});
                    }]
                }
            })
            .state('queuedForUpdate.new', {
                parent: 'queuedForUpdate',
                url: '/new',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/entities/queuedForUpdate/queuedForUpdate-dialog.html',
                        controller: 'QueuedForUpdateDialogController',
                        size: 'lg',
                        resolve: {
                            entity: function () {
                                return {
                                    ticketSource: null,
                                    addedToQueue: null,
                                    ticketKey: null,
                                    id: null
                                };
                            }
                        }
                    }).result.then(function(result) {
                        $state.go('queuedForUpdate', null, { reload: true });
                    }, function() {
                        $state.go('queuedForUpdate');
                    })
                }]
            })
            .state('queuedForUpdate.edit', {
                parent: 'queuedForUpdate',
                url: '/{id}/edit',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/entities/queuedForUpdate/queuedForUpdate-dialog.html',
                        controller: 'QueuedForUpdateDialogController',
                        size: 'lg',
                        resolve: {
                            entity: ['QueuedForUpdate', function(QueuedForUpdate) {
                                return QueuedForUpdate.get({id : $stateParams.id});
                            }]
                        }
                    }).result.then(function(result) {
                        $state.go('queuedForUpdate', null, { reload: true });
                    }, function() {
                        $state.go('^');
                    })
                }]
            })
            .state('queuedForUpdate.delete', {
                parent: 'queuedForUpdate',
                url: '/{id}/delete',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/entities/queuedForUpdate/queuedForUpdate-delete-dialog.html',
                        controller: 'QueuedForUpdateDeleteController',
                        size: 'md',
                        resolve: {
                            entity: ['QueuedForUpdate', function(QueuedForUpdate) {
                                return QueuedForUpdate.get({id : $stateParams.id});
                            }]
                        }
                    }).result.then(function(result) {
                        $state.go('queuedForUpdate', null, { reload: true });
                    }, function() {
                        $state.go('^');
                    })
                }]
            });
    });
