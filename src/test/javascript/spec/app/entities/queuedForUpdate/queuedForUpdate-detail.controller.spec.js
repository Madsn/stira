'use strict';

describe('Controller Tests', function() {

    describe('QueuedForUpdate Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockQueuedForUpdate;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockQueuedForUpdate = jasmine.createSpy('MockQueuedForUpdate');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'QueuedForUpdate': MockQueuedForUpdate
            };
            createController = function() {
                $injector.get('$controller')("QueuedForUpdateDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'stiraApp:queuedForUpdateUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
