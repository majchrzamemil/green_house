(function() {
    'use strict';
    angular
        .module('greenHouseApp')
        .factory('GreenHouseManager', GreenHouseManager);

    GreenHouseManager.$inject = ['$resource'];

    function GreenHouseManager ($resource) {
        var resourceUrl =  'api/green-house-managers/:id';

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
