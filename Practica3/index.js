$(function(){
    var socket = io.connect();
    var $btnMessage = $("#btnMessage");
    var $message = $("#message");
    var $chatCommunal = $("#chatCommunal");
    var $chatPrivate = $("#chatPrivate");
    var $userForm = $("#userForm");
    var $userFormArea = $("#userFormArea");
    var $messageArea = $("#messageArea");
    var $users = $("#users");
    var $username = $("#username");
    var $actualUsername = $("#actualUsername");
    var userLogged = "";
    var $imageFile = $("#imageFile");
    var $binaryFile = $("#binaryFile");
    var $optionsMessage = $("#optionsMessage");
    var uploader = new SocketIOFileClient(socket);

    //If an emoji is clicked append to text area
    $(".emoji").click(function(){
        var text = $($message).val();
        text += this.id;
        $message.val(text);
    });

    //Sending message
    $btnMessage.click(function(){
        socket.emit("send-message", $message.val(), $optionsMessage.val());
        $message.val("");
    });

    //New message coming
    socket.on("new-message", function(data){
        if(data.to === "all"){
            if(data.user !== userLogged){
                $chatCommunal.append('<div><span class="text-success">' + data.user + ': </span>' + data.msg + '</div>')
            }else{
                $chatCommunal.append('<div><span class="text-primary">You: </span>' + data.msg + '</div>')
            }
        }else{
            if(data.user !== userLogged){
                if(data.to === userLogged){
                    $chatPrivate.append('<div><span class="text-danger"> Private from, ' + data.user + ': </span>' + data.msg + '</div>')
                }
            }else{
                $chatPrivate.append('<div><span class="text-danger">Private to '+ data.to +': </span>' + data.msg + '</div>')
            } 
        }
    });

    //User login form
    $userForm.submit(function(e){
        e.preventDefault();
        userLogged = $username.val();
        socket.emit("new-user", $username.val(), function(data){
            if(data){
                $userFormArea.hide();
                $messageArea.show();
                $actualUsername.append('<h3>You are: '+ userLogged +'</h3><br>');
            }
        });
        $username.val("");
    });

    //Refresh users connected
    socket.on("get-users", function(data){
        var html = "";
        var options = '<option value="all">All</option>';
        for(var i = 0; i < data.length; i++){
            html += '<li class="list-group-item">' + data[i] + '</li>';
            if(data[i] !== userLogged){
                options += '<option value = "' + data[i] +'">' + data[i] +'</option>'
            }
        }
        $users.html(html);
        $optionsMessage.html(options);
    });

    //Add image to chat
    socket.on("addImage", function(userName, base64image, sendto){  
        if(sendto === "all"){
            if(userName !== userLogged){
                $chatCommunal.append('<div><span class="text-success">' + userName + ': </span>' + '<a target="_blank" href= "' + base64image + '"><img src="' + base64image + '"/></a>' + '</div>')
            }else{
                $chatCommunal.append('<div><span class="text-primary">You: </span>' + '<a target="_blank" href= "' + base64image + '"><img src="' + base64image + '"/></a>' + '</div>')
            }
        }else{
        
            if(userName !== userLogged){
                if(sendto === userLogged){
                    $chatPrivate.append('<div><span class="text-danger">Private from, ' + userName + ': </span>' + '<a target="_blank" href= "' + base64image + '"><img src="' + base64image + '"/></a>' + '</div>')
                }
            }else{
                $chatPrivate.append('<div><span class="text-danger">Private to '+ sendto + ': </span>' + '<a target="_blank" href= "' + base64image + '"><img src="' + base64image + '"/></a>' + '</div>')
            } 
        }
    });

    //Ading a file
    $imageFile.on('change', function(e){
        var file = e.originalEvent.target.files[0];
        var reader = new FileReader();
        reader.readAsDataURL(file);
        reader.onload = function(evt){
            socket.emit("user-image", evt.target.result, $optionsMessage.val());
        };
    })

    //File sent
    uploader.on('start', function(fileInfo) {
        console.log('Start uploading', fileInfo);
    });

    uploader.on('complete', function(fileInfo) {
        console.log('Upload Complete', fileInfo);
    });

    $binaryFile.on('change', function(e){
        e.preventDefault();
        var fileEl = document.getElementById('binaryFile');
        var uploadIds = uploader.upload(fileEl);
        var nameFile = e.originalEvent.target.files[0].name;
        socket.emit("new-file-input", nameFile, $optionsMessage.val());
    });

    //Add file to chat
    socket.on("addFileInput", function(userName, nameFile, sendto){
        if(sendto === "all"){
            if(userName !== userLogged){
                $chatCommunal.append('<div><span class="text-success">' + userName + ': </span><a href = "/data/' + nameFile + '" download>' + nameFile + '</a></div>')
            }else{
                $chatCommunal.append('<div><span class="text-primary">You: </span><a href = "/data/' + nameFile + '" download>' + nameFile + '</a></div>')
            }
        }
        else{
            if(userName !== userLogged){
                if(sendto === userLogged){
                    $chatPrivate.append('<div><span class="text-danger">Private from, ' + userName + ': </span><a href = "/data/' + nameFile + '" download>' + nameFile + '</a></div>')
                }
            }else{
                $chatPrivate.append('<div><span class="text-danger">Private to '+ sendto + ': </span><a href = "/data/' + nameFile + '" download>' + nameFile + '</a></div>')
            } 
        }
    });            
});