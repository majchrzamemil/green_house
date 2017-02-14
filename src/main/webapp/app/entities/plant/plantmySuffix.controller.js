(function() {
    'use strict';

    angular
        .module('greenHouseApp')
        .controller('PlantMySuffixController', PlantMySuffixController);

    PlantMySuffixController.$inject = ['$scope', '$state', 'Plant'];

    function PlantMySuffixController ($scope, $state, Plant) {
        var vm = this;

        vm.plants = [];

        loadAll();

        function loadAll() {
            Plant.query(function(result) {
                vm.plants = result;
                vm.searchQuery = null;
            });
        }
    }
})();
