var express = require("express");
var app = express();
var server = require("http").createServer(app);
var io = require("socket.io").listen(server);
const SocketIOFile = require('socket.io-file');

var users = [];
var connections = [];
var usersAndConnections = new Map();

server.listen(process.env.PORT || 1234);
console.log("Server running in port 1234");

app.get("/", function(req, res){
    res.sendFile(__dirname + "/index.html");
})

app.get('/index.js', (req, res, next) => {
	return res.sendFile(__dirname + '/index.js');
});

app.get('/socket.io.js', (req, res, next) => {
	return res.sendFile(__dirname + '/node_modules/socket.io-client/dist/socket.io.js');
});

app.get('/socket.io-file-client.js', (req, res, next) => {
	return res.sendFile(__dirname + '/node_modules/socket.io-file-client/socket.io-file-client.js');
});


//Whenever a client connects we enter to this function
io.sockets.on("connection", function(socket){
    connections.push(socket);
    console.log("A client has connected, with the id: " + socket.id);
    console.log("Actually there are " + connections.length + " clients connected");

    //If a client disconnect
    socket.on("disconnect", function(){
        users.splice(users.indexOf(socket.username), 1);
        updateUsernames();
        usersAndConnections.delete(socket.username);
        connections.splice(connections.indexOf(socket), 1);
        console.log("A client disconnected, still remain: " + connections.length + " clients connected");
        console.log(usersAndConnections);
    });

    //Send Messages
    socket.on("send-message", function(data, option){
        io.sockets.emit("new-message", {msg : data, user : socket.username, to : option})
    });

    //New User
    socket.on("new-user", function(data, callback){
        callback(true);
        socket.username = data;
        users.push(socket.username);
        usersAndConnections.set(data, socket.id);
        console.log(usersAndConnections);
        updateUsernames();
    });

    function updateUsernames(){
        io.sockets.emit("get-users", users);
    }

    //User wants to upload image
    socket.on("user-image", function(image, optionSend){
        io.sockets.emit("addImage", socket.username, image, optionSend);
    });

    //User wants to upload file
    socket.on("new-file-input", function(name_file, optionSend){
        io.sockets.emit("addFileInput", socket.username, name_file, optionSend);
    });

    //Files
	var uploader = new SocketIOFile(socket, {
		uploadDir: 'data',							// simple directory
		chunkSize: 10240,							// default is 10240(1KB)
		transmissionDelay: 0,						// delay of each transmission, higher value saves more cpu resources, lower upload speed. default is 0(no delay)
		overwrite: false, 							// overwrite file if exists, default is true.
		rename: function(filename) {
			var split = filename.split('.');	// split filename by .(extension)
			var fname = split[0];	// filename without extension
			var ext = split[1];

			return `${fname}.${ext}`;
		}
	});
	uploader.on('start', (fileInfo) => {
		console.log('Start uploading');
		console.log(fileInfo);
	});

	uploader.on('complete', (fileInfo) => {
		console.log('Upload Complete.');
		console.log(fileInfo);
    });    
});