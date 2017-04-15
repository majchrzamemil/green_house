(function () {
    'use strict';

    angular
        .module('greenHouseApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider.state('home', {
            parent: 'app',
            url: '/',
            data: {
                authorities: []
            },
            views: {
                'content@': {
                    templateUrl: 'app/home/home.html',
                    controller: 'HomeController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('home');
                    return $translate.refresh();
                }]
            }, onEnter: ['HumAndTempService', function (HumAndTempService) {
                console.log("dadsadas");
                HumAndTempService.subscribe();
            }], onExit: ['HumAndTempService', function (HumAndTempService) {
                HumAndTempService.unsubscribe();
            }]

        });
    }
})();
