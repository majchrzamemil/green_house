(function() {
    'use strict';

    angular
        .module('greenHouseApp')
        .controller('HomeController', HomeController);

    HomeController.$inject = ['$scope', 'Principal', 'LoginService', '$state', 'HumAndTempService'];

    function HomeController ($scope, Principal, LoginService, $state, HumAndTempService) {
        var vm = this;
        console.log("controller");
        vm.account = null;
        vm.isAuthenticated = Principal.isAuthenticated;
        console.log(vm.isAuthenticated());
        vm.login = LoginService.open;
        vm.register = register;
        $scope.$on('authenticationSuccess', function() {
            getAccount();
        });

        HumAndTempService.connect();
        HumAndTempService.receive().then(function(humAndTemp) {
            console.log("humAndTemp");
            showHumAndTemp(humAndTemp);
        });
            
        function showHumAndTemp(humAndTemp){
            console.log(humAndTemp);
        }
        
        getAccount();

        if(vm.isAuthenticated()) {
            $state.go('green-housemySuffix');
        }

        function getAccount() {
            Principal.identity().then(function(account) {
                vm.account = account;
                vm.isAuthenticated = Principal.isAuthenticated;
            });
        }
        function register () {
            $state.go('register');
        }
    }
})();
