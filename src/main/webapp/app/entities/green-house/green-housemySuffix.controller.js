(function () {
    'use strict';

    angular
        .module('greenHouseApp')
        .controller('GreenHouseMySuffixController', GreenHouseMySuffixController);

    GreenHouseMySuffixController.$inject = ['$scope', '$resource', '$http', '$state', 'GreenHouse', 'HumAndTempService', 'JhiTrackerService'];

    function GreenHouseMySuffixController($scope, $resource, $http, $state, GreenHouse, HumAndTempService, JhiTrackerService) {
        var vm = this;

        vm.greenHouses = [];
        vm.activities = [];

        vm.humidity;
        vm.temperature;
        vm.humidifier;
        vm.pumps;
        vm.lights;
        vm.soilMoisture;
        vm.plantsPhotos;
        vm.plants;

        loadAll();
        getPlants($http);


        HumAndTempService.connect();
        HumAndTempService.receive().then(null, null, function (humAndTemp) {
            vm.humidity = humAndTemp.humidity;
            vm.temperature = humAndTemp.temperature;
            vm.humidifier = humAndTemp.humidifierOn;
            vm.pumps = humAndTemp.pumpsOn;
            vm.lights = humAndTemp.lightsOn;
            // vm.soilMoisture = humAndTemp.soilMoisture;
        });

        function loadAll() {
            GreenHouse.query(function (result) {
                vm.plantsPhotos = result;
                vm.searchQuey = null;
            });
        }

        JhiTrackerService.receive().then(null, null, function (activity) {
            showActivity(activity);
        });

        function showActivity(activity) {
            var existingActivity = false;
            for (var index = 0; index < vm.activities.length; index++) {
                if (vm.activities[index].sessionId === activity.sessionId) {
                    existingActivity = true;
                    if (activity.page === 'logout') {
                        vm.activities.splice(index, 1);
                    } else {
                        vm.activities[index] = activity;
                    }
                }
            }
            if (!existingActivity && (activity.page !== 'logout')) {
                vm.activities.push(activity);
            }
        }

        function makePhotoUrl(photoName) {

        }

        function getPlantsPhotos($http) {
            $http.get('/api/photos').then(function (response) {
                vm.plantsPhotos = response.data;
            });
        }

        function getPlants($http) {
            $http.get('/api/plants').then(function (response) {
                vm.plants = response.data;
            }).then(function () {
                //testowe ustawienie humidity dla dwóch pierwszych plantów
                vm.plants[0].humidity = 30;
                vm.plants[1].humidity = 35;
                setPlantsSoilHumidity(checkIfMultipleSoilHumidifier(vm.plants), vm.plants);
            });
        }

        function checkIfMultipleSoilHumidifier(plants) {
            var humidifierCount = 0;
            var humidity;
            angular.forEach(plants, function (value, key) {
                if (value.humidity) {
                    humidifierCount++;
                    humidity = value.humidity;
                }
            });
            if (humidifierCount > 1) {
                return true
            } else {
                vm.soilMoisture = humidity;
                return false;
            }
        }

        function setPlantsSoilHumidity(ifMultipleSoilHumidity, plants) {
            if (!ifMultipleSoilHumidity) {
                angular.forEach(plants, function (value, key) {
                    value.humidity = vm.soilMoisture;
                });
            }
        }


    }
})();
