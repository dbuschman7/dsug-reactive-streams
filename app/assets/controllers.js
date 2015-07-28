angular.module('reactive.controllers', [])

.controller('ReactiveController', function($scope) {
	// Initialize data fields
	$scope.serverTime = "Nothing Here Yet";

	// Statistics
	$scope.rawCount = 0;
	$scope.pubGeoCount = 0;
	$scope.mongoCount = 0;

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
				var counts = [ 0, 0, 0, 0, 0 ];
				for (var j = 0; j < data.data.length; j++) {
					var pr = data.data[j];
					if (pr.key == "fastSource") {
						counts[0] = pr.count + 1;
					}
					if (pr.key == "CA") {
						counts[1] = pr.count;
					}
					if (pr.key == "FL") {
						counts[2] = pr.count;
					}
					if (pr.key == "fastSink") {
						counts[3] = pr.count;
					}
					if (pr.key == "slowSink") {
						counts[4] = pr.count - 1;
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
