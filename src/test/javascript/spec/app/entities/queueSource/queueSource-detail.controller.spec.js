'use strict';

describe('Controller Tests', function() {

    describe('QueueSource Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockQueueSource;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockQueueSource = jasmine.createSpy('MockQueueSource');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'QueueSource': MockQueueSource
            };
            createController = function() {
                $injector.get('$controller')("QueueSourceDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'stiraApp:queueSourceUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
