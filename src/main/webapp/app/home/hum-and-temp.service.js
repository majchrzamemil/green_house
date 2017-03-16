(function () {
    'use strict';
    /* globals SockJS, Stomp */

    angular
            .module('greenHouseApp')
            .factory('HumAndTempService', HumAndTempService);

    HumAndTempService.$inject = ['$rootScope', '$window', '$cookies', '$http', '$q'];

    function HumAndTempService($rootScope, $window, $cookies, $http, $q) {
        var stompClient = null;
        var subscriber = null;
        var listener = $q.defer();
        var connected = $q.defer();
        var alreadyConnectedOnce = false;

        var service = {
            connect: connect,
            disconnect: disconnect,
            receive: receive,
            subscribe: subscribe,
            unsubscribe: unsubscribe
        };

        return service;

        function connect() {
            //building absolute path so that websocket doesnt fail when deploying with a context path
            var loc = $window.location;
            var url = '//' + loc.host + loc.pathname + 'websocket/tempAndHum';
            var socket = new SockJS(url);
            stompClient = Stomp.over(socket);
            setTimeout(function(){}, 2500);
            var stateChangeStart;
            var headers = {};
            headers[$http.defaults.xsrfHeaderName] = $cookies.get($http.defaults.xsrfCookieName);
            console.log("connecting to humAndTemp")
            stompClient.connect(headers, function () {
                connected.resolve('success');
                console.log(connected);
                if (!alreadyConnectedOnce) {
                    stateChangeStart = $rootScope.$on('$stateChangeStart', function () {
                        sendActivity();
                    });
                    alreadyConnectedOnce = true;
                }
            });
            
            $rootScope.$on('$destroy', function () {
                if (angular.isDefined(stateChangeStart) && stateChangeStart !== null) {
                    stateChangeStart();
                }
            });
        }
        
        function disconnect() {
            if (stompClient !== null) {
                stompClient.disconnect();
                stompClient = null;
            }
        }

        function receive() {
            console.log('receive');
            return listener.promise;
        }

        function subscribe() {
            connected.promise.then(function () {
                subscriber = stompClient.subscribe('/topic/tempAndHum', function (data) {
                    // console.log("angular.fromJson(data.body)SUBSCRIBE");
                    listener.notify(angular.fromJson(data.body));
                });
            }, null, null);
        }

        function unsubscribe() {
            if (subscriber !== null) {
                subscriber.unsubscribe();
            }
            listener = $q.defer();
        }

        function getHumidity() {
            return this.humidity;
        }
    }
})();
