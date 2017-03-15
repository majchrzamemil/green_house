(function() {
    'use strict';

    angular
        .module('greenHouseApp')
        .controller('NavbarController', NavbarController);

    NavbarController.$inject = ['$scope', '$state', 'Auth', 'Principal', 'ProfileService', 'LoginService'];

    function NavbarController ($scope, $state, Auth, Principal, ProfileService, LoginService) {
        var vm = this;

        vm.isNavbarCollapsed = true;
        vm.isAuthenticated = Principal.isAuthenticated;
        vm.account = null;
        vm.isAdmin = null;
        $scope.$on('authenticationSuccess', function() {
            getAccount();
        });

        ProfileService.getProfileInfo().then(function(response) {
            vm.inProduction = response.inProduction;
            vm.swaggerEnabled = response.swaggerEnabled;
        });

        vm.login = login;
        vm.logout = logout;
        vm.toggleNavbar = toggleNavbar;
        vm.collapseNavbar = collapseNavbar;
        vm.$state = $state;

        getAccount();
        checkIfAdminRole();

        function getAccount() {
            Principal.identity().then(function(account) {
                vm.account = account;
                vm.isAuthenticated = Principal.isAuthenticated;
                console.log(account.authorities);
                vm.isAdmin = account.authorities.includes('ROLE_ADMIN');
                console.log(vm.isAdmin);
            });
        }

        function checkIfAdminRole() {
            
        }

        function login() {
            collapseNavbar();
            LoginService.open();
        }

        function logout() {
            collapseNavbar();
            Auth.logout();
            $state.go('home');
        }

        function toggleNavbar() {
            vm.isNavbarCollapsed = !vm.isNavbarCollapsed;
        }

        function collapseNavbar() {
            vm.isNavbarCollapsed = true;
        }
    }
})();
