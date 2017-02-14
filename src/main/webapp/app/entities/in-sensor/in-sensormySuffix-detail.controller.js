(function() {
    'use strict';

    angular
        .module('greenHouseApp')
        .controller('InSensorMySuffixDetailController', InSensorMySuffixDetailController);

    InSensorMySuffixDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'InSensor'];

    function InSensorMySuffixDetailController($scope, $rootScope, $stateParams, previousState, entity, InSensor) {
        var vm = this;

        vm.inSensor = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('greenHouseApp:inSensorUpdate', function(event, result) {
            vm.inSensor = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
