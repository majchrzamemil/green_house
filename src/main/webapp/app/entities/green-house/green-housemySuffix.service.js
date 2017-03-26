(function() {
    'use strict';
    angular
        .module('greenHouseApp')
        .factory('GreenHouse', GreenHouse);

    GreenHouse.$inject = ['$resource'];

    function GreenHouse ($resource) {
        var resourceUrl =  'api/pictures/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                    }
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();
