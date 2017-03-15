(function() {
    'use strict';

    angular
    .module('greenHouseApp')
    .controller('GreenHouseMySuffixController', GreenHouseMySuffixController);

    GreenHouseMySuffixController.$inject = ['$scope', '$state', 'GreenHouse', 'HumAndTempService'];

    function GreenHouseMySuffixController ($scope, $state, GreenHouse, HumAndTempService) {
        var vm = this;

        vm.greenHouses = [];

        vm.humidity;
        vm.temperature;
        vm.humidifier;
        vm.pumps;
        vm.lights;
        vm.soilMoisture;

        // loadAll();


        HumAndTempService.connect();
        HumAndTempService.receive().then(null, null, function(humAndTemp) {
            vm.humidity = humAndTemp.humidity;
            vm.temperature = humAndTemp.temperature;
            vm.humidifier = humAndTemp.humidifierOn;
            vm.pumps = humAndTemp.pumpsOn;
            vm.lights = humAndTemp.lightsOn;
            vm.soilMoisture = humAndTemp.soilMoisture;
        });

        function loadAll() {
            GreenHouse.query(function(result) {
                vm.greenHouses = result;
                vm.searchQuey = null;
            });
        }
    }
})();
