(function() {
    'use strict';

    angular
        .module('greenHouseApp')
        .controller('GreenHouseManagerMySuffixDetailController', GreenHouseManagerMySuffixDetailController);

    GreenHouseManagerMySuffixDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'GreenHouseManager', 'ProfileSettings', 'GreenHouse'];

    function GreenHouseManagerMySuffixDetailController($scope, $rootScope, $stateParams, previousState, entity, GreenHouseManager, ProfileSettings, GreenHouse) {
        var vm = this;

        vm.greenHouseManager = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('greenHouseApp:greenHouseManagerUpdate', function(event, result) {
            vm.greenHouseManager = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
