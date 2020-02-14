var head = null;
var tail = null;
var next = null;
var count = 0;
var forward = true;
var toggle = true;

function addImage(i) {
	var node = {};
	node.time = i.time;
	node.img = i.img;
	node.next = null;
	node.prev = tail;
	if (tail == null) {
		head = node;
		tail = node;
	}
	else {
		tail.next = node;
		tail = node;
	}
}

var CamWs = {};
CamWs.socket = null;

CamWs.connect = (function(host) {
	if ('WebSocket' in window) {
		CamWs.socket = new WebSocket(host);
	} else if ('MozWebSocket' in window) {
		CamWs.socket = new MozWebSocket(host);
	} else {
	    return;
	}
	
	CamWs.socket.onopen = function () {
	};
	
	CamWs.socket.onclose = function () {
	};
	
	CamWs.socket.onmessage = function (message) {
		var i = JSON.parse(message.data);
		if (message.data.length > 3000) {
            i.img = new Image();
            i.img.src = i.image;
            i.img.onload = function () {
                addImage(i);
            }
        }
    	else {
			if (i.done == true) {
				CamWs.socket.close();
				CamWs.socket = null;
			}
    	}
    };
});

CamWs.initialize = function(path) {
	if (CamWs.socket != null) {
		CamWs.socket.send("close");
		CamWs.socket.close();
		CamWs.socket = null;
	}
	head = null;
	tail = null;
	next = null;
	count = 0;
	forward = true;
	CamWs.connect(window.location.href.replace('http','ws').replace('cam/','camws?') + path);
};

function goNext() {
	if (next != tail) {
		next = next.next;
	}
	else {
		return;
	}
}

function goPrev() {
	if (next != head) {
			next = next.prev;
		}
		else {
			return;
		}
}

