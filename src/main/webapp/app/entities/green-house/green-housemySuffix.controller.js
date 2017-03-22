(function () {
    'use strict';

    angular
            .module('greenHouseApp')
            .controller('GreenHouseMySuffixController', GreenHouseMySuffixController);

    GreenHouseMySuffixController.$inject = ['$scope', '$resource', '$http', '$state', 'GreenHouse', 'HumAndTempService'];

    function GreenHouseMySuffixController($scope, $resource, $http, $state, GreenHouse, HumAndTempService) {
        var vm = this;

        vm.greenHouses = [];

        vm.humidity;
        vm.temperature;
        vm.humidifier;
        vm.pumps;
        vm.lights;
        vm.soilMoisture;
        vm.plantsPhotos;

        getPlantsPhotos($http, $resource);
        // loadAll();


        HumAndTempService.connect();
        HumAndTempService.receive().then(null, null, function (humAndTemp) {
            vm.humidity = humAndTemp.humidity;
            vm.temperature = humAndTemp.temperature;
            vm.humidifier = humAndTemp.humidifierOn;
            vm.pumps = humAndTemp.pumpsOn;
            vm.lights = humAndTemp.lightsOn;
            vm.soilMoisture = humAndTemp.soilMoisture;
        });

        function loadAll() {
            GreenHouse.query(function (result) {
                vm.plantsPhotos = result;
                vm.searchQuey = null;
            });
      
            vm.getPlantsPhotos();
        }
        
        function makePhotoUrl(photoName) {

        }

        function getPlantsPhotos($http, $resource) {
            $http.get('/api/photos').
                    then(function (response) {
                        vm.plantsPhotos = response.data;
                    });
        }


    }
})();
