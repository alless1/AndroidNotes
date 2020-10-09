document.addEventListener('UniAppJSBridgeReady', function() {
	uni.postMessage({
		data: {
			action: 'message'
		}
	});

	console.log('------------------------------');

	uni.getEnv(function(res) {
		console.log('当前环境：' + JSON.stringify(res));
	});
	
});

window.onload = function() {
	var video = document.getElementById('video');
	var canvas = document.getElementById('canvas');
	var context = canvas.getContext('2d');
	var tracker = new window.tracking.ObjectTracker('face');
	tracker.setInitialScale(4);
	tracker.setStepSize(2);
	tracker.setEdgesDensity(0.1);
	
	window.tracking.track('#video', tracker, {
		camera: true
	});
	
	tracker.on('track', function(event) {
		console.log('-----------track on-----------');

		context.clearRect(0, 0, canvas.width, canvas.height);
		event.data.forEach(function(rect) {
			context.strokeStyle = '#a64ceb';
			context.strokeRect(rect.x, rect.y, rect.width, rect.height);
			context.font = '11px Helvetica';
			context.fillStyle = "#fff";
			context.fillText('x: ' + rect.x + 'px', rect.x + rect.width + 5, rect.y + 11);
			context.fillText('y: ' + rect.y + 'px', rect.x + rect.width + 5, rect.y + 22);
		});
		console.log('-----------track on-----------');
	});
};
