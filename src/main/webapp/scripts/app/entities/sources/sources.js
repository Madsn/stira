'use strict';

angular.module('stiraApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('sources', {
                parent: 'entity',
                url: '/sourcess',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'Sourcess'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/sources/sourcess.html',
                        controller: 'SourcesController'
                    }
                },
                resolve: {
                }
            })
            .state('sources.detail', {
                parent: 'entity',
                url: '/sources/{id}',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'Sources'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/sources/sources-detail.html',
                        controller: 'SourcesDetailController'
                    }
                },
                resolve: {
                    entity: ['$stateParams', 'Sources', function($stateParams, Sources) {
                        return Sources.get({id : $stateParams.id});
                    }]
                }
            })
            .state('sources.new', {
                parent: 'sources',
                url: '/new',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/entities/sources/sources-dialog.html',
                        controller: 'SourcesDialogController',
                        size: 'lg',
                        resolve: {
                            entity: function () {
                                return {
                                    name: null,
                                    syncedTo: null,
                                    id: null
                                };
                            }
                        }
                    }).result.then(function(result) {
                        $state.go('sources', null, { reload: true });
                    }, function() {
                        $state.go('sources');
                    })
                }]
            })
            .state('sources.edit', {
                parent: 'sources',
                url: '/{id}/edit',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/entities/sources/sources-dialog.html',
                        controller: 'SourcesDialogController',
                        size: 'lg',
                        resolve: {
                            entity: ['Sources', function(Sources) {
                                return Sources.get({id : $stateParams.id});
                            }]
                        }
                    }).result.then(function(result) {
                        $state.go('sources', null, { reload: true });
                    }, function() {
                        $state.go('^');
                    })
                }]
            })
            .state('sources.delete', {
                parent: 'sources',
                url: '/{id}/delete',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/entities/sources/sources-delete-dialog.html',
                        controller: 'SourcesDeleteController',
                        size: 'md',
                        resolve: {
                            entity: ['Sources', function(Sources) {
                                return Sources.get({id : $stateParams.id});
                            }]
                        }
                    }).result.then(function(result) {
                        $state.go('sources', null, { reload: true });
                    }, function() {
                        $state.go('^');
                    })
                }]
            });
    });
