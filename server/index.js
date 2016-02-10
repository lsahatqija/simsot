// Setup basic express server
var express = require('express');
var app = express();
var server = require('http').createServer(app);
var io = require('socket.io').listen(server);
var port =  3000;

server.listen(port, function () {
  console.log('Server listening at port %d', port);
});



io.sockets.on('connection', function (socket) {

  // when the client emits 'test', this listens and executes
  socket.on('test', function (data) {
	  console.log("socket on test recu : "+data);
    // we tell the client to execute 'testresponse'
    socket.emit('testresponse', {
      "test": "bien recu",
	  "data" : data
    });
  });


  
});