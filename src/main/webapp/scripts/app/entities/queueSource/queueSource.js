'use strict';

angular.module('stiraApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('queueSource', {
                parent: 'entity',
                url: '/queueSources',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'QueueSources'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/queueSource/queueSources.html',
                        controller: 'QueueSourceController'
                    }
                },
                resolve: {
                }
            })
            .state('queueSource.detail', {
                parent: 'entity',
                url: '/queueSource/{id}',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'QueueSource'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/queueSource/queueSource-detail.html',
                        controller: 'QueueSourceDetailController'
                    }
                },
                resolve: {
                    entity: ['$stateParams', 'QueueSource', function($stateParams, QueueSource) {
                        return QueueSource.get({id : $stateParams.id});
                    }]
                }
            })
            .state('queueSource.new', {
                parent: 'queueSource',
                url: '/new',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/entities/queueSource/queueSource-dialog.html',
                        controller: 'QueueSourceDialogController',
                        size: 'lg',
                        resolve: {
                            entity: function () {
                                return {
                                    ticketSource: null,
                                    lastAddedTicket: null,
                                    id: null
                                };
                            }
                        }
                    }).result.then(function(result) {
                        $state.go('queueSource', null, { reload: true });
                    }, function() {
                        $state.go('queueSource');
                    })
                }]
            })
            .state('queueSource.edit', {
                parent: 'queueSource',
                url: '/{id}/edit',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/entities/queueSource/queueSource-dialog.html',
                        controller: 'QueueSourceDialogController',
                        size: 'lg',
                        resolve: {
                            entity: ['QueueSource', function(QueueSource) {
                                return QueueSource.get({id : $stateParams.id});
                            }]
                        }
                    }).result.then(function(result) {
                        $state.go('queueSource', null, { reload: true });
                    }, function() {
                        $state.go('^');
                    })
                }]
            })
            .state('queueSource.delete', {
                parent: 'queueSource',
                url: '/{id}/delete',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/entities/queueSource/queueSource-delete-dialog.html',
                        controller: 'QueueSourceDeleteController',
                        size: 'md',
                        resolve: {
                            entity: ['QueueSource', function(QueueSource) {
                                return QueueSource.get({id : $stateParams.id});
                            }]
                        }
                    }).result.then(function(result) {
                        $state.go('queueSource', null, { reload: true });
                    }, function() {
                        $state.go('^');
                    })
                }]
            });
    });
