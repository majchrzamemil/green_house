(function () {
    'use strict';

    angular
        .module('greenHouseApp')
        .controller('GreenHouseMySuffixController', GreenHouseMySuffixController);

    GreenHouseMySuffixController.$inject = ['$scope', '$resource', '$http', '$state', 'GreenHouse', 'HumAndTempService', 'JhiTrackerService', 'Plant'];

    function GreenHouseMySuffixController($scope, $resource, $http, $state, GreenHouse, HumAndTempService, JhiTrackerService, Plant) {
        var vm = this;

        vm.humAndTempLoaded = false;
        vm.greenHouses = [];
        vm.activities = [];
        vm.plants = [];

        vm.humidity;
        vm.temperature;
        vm.humidifier;
        vm.pumps;
        vm.lights;
        vm.soilMoisture;
        vm.plantsPhotos;
        vm.plants;
        vm.photosLoaded = false;
        vm.currentPhotoId = 0;
        vm.currentPhoto;

        HumAndTempService.connect();
        HumAndTempService.receive().then(null, null, function (humAndTemp) {
            vm.humidity = humAndTemp.humidity;
            vm.temperature = humAndTemp.temperature;
            vm.humidifier = humAndTemp.humidifierOn;
            vm.pumps = humAndTemp.pumpsOn;
            vm.lights = humAndTemp.lightsOn;
            vm.soilMoisture = humAndTemp.soilMoisture;
            if(!vm.humAndTempLoaded) {
                loadAll();
                setPlantsSoilMoisture();
            }
            vm.humAndTempLoaded = true;
        });

        function loadAll() {
            GreenHouse.query(function (result) {
                // console.log(angular.fromJson(result));
                vm.plantsPhotos = result;
                vm.searchQuery = null;
                vm.photosLoaded = true;
                console.log('PLANTS PHOTOS: ' + vm.plantsPhotos);
                $scope.getNextPhoto();
            });
            Plant.query(function (result) {
                vm.plants = result;
                vm.searchQuery = null;
                setPlantsSoilMoisture();
                // console.log(angular.toJson(vm.plants, true));
            });
        }

        JhiTrackerService.receive().then(null, null, function (activity) {
            showActivity(activity);
        });

        $scope.getPreviousPhoto = function() {
            vm.currentPhotoId = (vm.currentPhotoId - 1);
            if(vm.currentPhotoId < 0) {
                vm.currentPhotoId = vm.plantsPhotos.length - 1;
            }
            vm.currentPhoto = vm.plantsPhotos[vm.currentPhotoId];
        };

        $scope.getNextPhoto = function() {
            vm.currentPhotoId = (vm.currentPhotoId + 1);
            if(vm.currentPhotoId > vm.plantsPhotos.length - 1) {
                vm.currentPhotoId = 0;
            }
            vm.currentPhoto = vm.plantsPhotos[vm.currentPhotoId];
        };




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
                vm.photosLoaded = true;
                //testowe ustawienie nazw zdjęć
                // setTimeout(function () {
                //     vm.plantsPhotos = ['cam1.jpg', 'cam2.jpg', 'cam3.jpg'];
                //     vm.photosLoaded = true;
                // }, 2000);
            });
        }

        function getPlants($http) {
            $http.get('/api/plants').then(function (response) {
                vm.plants = response.data;
            }).then(function () {
                //testowe ustawienie humidity dla dwóch pierwszych plantów
                // vm.plants[0].humidity = 30;
                // vm.plants[1].humidity = 35;
                // console.log(vm.plants);
                setPlantsSoilHumidity(checkIfMultipleSoilHumidifier(vm.plants), vm.plants);
            });
        }

        function setPlantsSoilMoisture() {
            var soilMoistureCount = vm.soilMoisture.length;
            if(vm.soilMoistureCount == 1) {
                angular.forEach(vm.plants, function (value, key) {
                    value.soilMoisture = vm.soilMoisture[0];
                });
            } else {
                var counter = 0;
                angular.forEach(vm.plants, function(value, key) {
                    value.soilMoisture = vm.soilMoisture[counter];
                    counter++;
                });
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
