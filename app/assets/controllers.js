angular.module('reactive.controllers', [])

.controller('ReactiveController', function($scope) {

	// Statistics
	$scope.rawCount = 0;
	$scope.scenarios = [
	{name:'Scenario 1', num:1},
	{name:'Scenario 2', num:2},
	{name:'Scenario 3', num:3},
	{name:'Scenario 4', num:4},
	{name:'Scenario 5', num:5},
	{name:'Scenario 6', num:6}
	];
	$scope.scenario = $scope.scenarios[0]; 
	$scope.changeScenario = function(scen) {
		$scope.scenario = scen;    
	};

	// events from server handler
	$scope.handleServerEvent = function(e) {
		$scope.$apply(function() {
			var raw = JSON.parse(e.data);
			var target = raw.target;
			var data = raw.data;
			if (target == "rawCount") {
				$scope.rawCount += data;
			} else if (target == "counters") {
				// {"ts":1427787041808,"data":[{"key":"FL","count":9},{"key":"CO","count":11},{"key":"HI","count":12},{"key":"CA","count":13},{"key":"NY","count":8}]}
				var ts = data.ts;
				var counts = [ 0, 0, 0 ];
				for (var j = 0; j < data.data.length; j++) {
					var pr = data.data[j];
					if (pr.key == "fastSource") {
						counts[0] = pr.count + 1;
					}
					if (pr.key == "fastSink") {
						counts[1] = pr.count;
					}
					if (pr.key == "slowSink") {
						counts[2] = pr.count - 1;
					}

					updateGeoChart(pr.key, pr.count);
				}
				updateAreaChart(ts, counts);
			} else {
				console.log("Unhandled - target = " + target + ", data = " + data);
			}
		});
	};

	// sse socket
	$scope.startSocket = function(uuid) {
		$scope.stopSocket();
		$scope.images = [];
		var url = "/sse/" + uuid;
		console.log(url);
		$scope.socket = new EventSource(url);
		$scope.socket.addEventListener("message", $scope.handleServerEvent, false);
	};

	$scope.stopSocket = function() {
		if (typeof $scope.socket != 'undefined') {
			$scope.socket.close();
		}
	};

});
