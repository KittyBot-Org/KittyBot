var addedRoles = [];
var displayMobileNav = false;

if(window.location.pathname === "/guild"){
	loadGuildsOverview();
}
else{
	loadGuilds();
	loadSelfAssignableRoles();
	loadChannels();
	getWelcomeMessageEnabled();
	getWelcomeMessage();
	getWelcomeChannel();
	getCommandPrefix();
	getNSFWEnabled();
}

var tx = document.getElementsByTagName('textarea');
for (var i = 0; i < tx.length; i++) {
  tx[i].setAttribute('style', 'height:' + (tx[i].scrollHeight + 2) + 'px;overflow-y:auto;');
  tx[i].addEventListener("input", OnInput, false);
}


function requestUrl(url, func){
	var xmlhttp = new XMLHttpRequest();
	xmlhttp.onreadystatechange = func;
	xmlhttp.open("GET",  window.location.origin + "/" + url, true);
	xmlhttp.send();
}

function request(url, func){
	var xmlhttp = new XMLHttpRequest();
	xmlhttp.onreadystatechange = func;
	xmlhttp.open("GET",  window.location.href + "/" + url, true);
	xmlhttp.send();
}

function loadGuildsOverview(){
    var desktop = document.getElementById("navigation");
    var mobile = document.getElementById("mobile-navigation-list");
    var list = document.getElementById("server-list");
    requestUrl("user/me/guilds/get", function(){
		if(this.readyState == 4 && this.status == 200){
			var json = JSON.parse(this.responseText);
			var textDesktop = "";
			var textMobile = "";
			var textList = "";
			for (var i = 0; i < json.guilds.length; i++) {
				var e = json.guilds[i];
				textDesktop += "<a href='/guild/" + e.id + "'><button><img src='" + e.iconurl + "' alt='" + e.name + " Icon'><h2>" + e.name + "</h2></button></a>";
				textMobile += "<li><a href='/guild/" + e.id + "'><button><img src='" + e.iconurl + "' alt='" + e.name + " Icon'>" + e.name + "</button></a></li>";
				textList += "<li><a href='/guild/" + e.id + "'><img src='" + e.iconurl + "' alt='" + e.name + " Icon'><p>" + e.name + "</p></a></li>";
			}
			desktop.innerHTML += textDesktop;
			mobile.innerHTML += textMobile;
			list.innerHTML += textList;
		}
		else if(this.readyState == 4 && this.status == 401){
			sendToast("Error: '" + this.responseText + "'");
		}
	});
}

function loadGuilds(){
    var desktop = document.getElementById("navigation");
    var mobile = document.getElementById("mobile-navigation-list");
    var url = "user/me/guilds/get";
    requestUrl(url, function(){
		if(this.readyState == 4 && this.status == 200){
			var json = JSON.parse(this.responseText);
			var textDesktop = "";
			var textMobile = "";
			for (var i = 0; i < json.guilds.length; i++) {
				var e = json.guilds[i];
				textDesktop += "<a href='/guild/" + e.id + "'><button><img src='" + e.iconurl + "' alt='" + e.name + " Icon'><h2>" + e.name + "</h2></button></a>";
				textMobile += "<li><a href='/guild/" + e.id + "'><button><img src='" + e.iconurl + "' alt='" + e.name + " Icon'>" + e.name + "</button></a></li>";
			}
			desktop.innerHTML += textDesktop;
			mobile.innerHTML += textMobile;
		}
		else if(this.readyState == 4 && this.status == 401){
			sendToast("Error: '" + this.responseText + "'");
		}
	});
}

function loadRoles() {
    var list = document.getElementById("roleSelect");
    var url = "/roles/get";
    request(url, function(){
    	if(this.readyState == 4 && this.status == 200){
    		var json = JSON.parse(this.responseText);
			var text = "<option value='-1' disabled selected hidden style='display: none;'>Choose role...</option>";
			for (var i = 0; i < json.roles.length; i++) {
				var e = json.roles[i];
				if(!addedRoles.includes(e.id) && e.name != "@everyone"){
					text += "<option value='" + e.id + "'>" + e.name + "</option>";
				}
			}
			list.innerHTML = text;
    	}
    	else if(this.readyState == 4 && this.status == 401){
			sendToast("Error: '" + this.responseText + "'");
		}
    });
}


function loadSelfAssignableRoles() {
    var list = document.getElementById("roleList");
    var url = "/selfassignableroles/get";
    request(url, function(){
		if(this.readyState == 4 && this.status == 200){
			var json = JSON.parse(this.responseText);
			var text = "";
			addedRoles = [];
			if(json.selfassignableroles.length == 0){
				text = "<li><span>No roles added</span></li>";
			}
			else{
				var roles = json.selfassignableroles;
				for(var i = 0; i < roles.length; i++){
					var e = roles[i];
					addedRoles.push(e.id);
					text += "<li><span>" + e.name + "</span><button class='delete' value='" + e.id + "' onclick='removeSelfAssignableRole(this)'>Remove</button></li>";
				}
			}
			list.innerHTML = text;
            loadRoles();
		}
		else if(this.readyState == 4 && this.status == 401){
			sendToast("Error: '" + this.responseText + "'");
		}
	});
}
function removeSelfAssignableRole(element){
    var url = "/selfassignableroles/remove/" + element.value;
	var xmlhttp = new XMLHttpRequest();
	request(url, function(){
		if(this.readyState == 4 && this.status == 200){
			var json = JSON.parse(this.responseText);
			loadSelfAssignableRoles();
			if(json.status == "ok"){
				sendToast("Role removed");
			}
		}
		else if(this.readyState == 4 && this.status == 401){
			sendToast("Error: '" + this.responseText + "'");
		}
	});
}
function addSelfAssignableRole(){
	var e = document.getElementById("roleSelect");
	var roleId = e.options[e.selectedIndex].value;
	if(roleId != "-1"){
		var url = "/selfassignableroles/add/" + roleId;
		request(url, function(){
			if(this.readyState == 4 && this.status == 200){
				var json = JSON.parse(this.responseText);
				loadSelfAssignableRoles();
				if(json.status == "ok"){
					sendToast("Role added");
				}
			}
			else if(this.readyState == 4 && this.status == 401){
				sendToast("Error: '" + this.responseText + "'");
			}
		});
	}
}

function loadChannels() {
    var list = document.getElementById("channelSelect");
    var url = "/channels/get";
    request(url, function(){
		if(this.readyState == 4 && this.status == 200){
			var json = JSON.parse(this.responseText);
			var text = "<option value='-1' disabled selected hidden style='display: none;'>Choose channel...</option>";
			for (var i = 0; i < json.channels.length; i++) {
				var e = json.channels[i];
				text += "<option value='" + e.id + "'>#" + e.name + "</option>";
			}
			list.innerHTML = text;
		}
		else if(this.readyState == 4 && this.status == 401){
			sendToast("Error: '" + this.responseText + "'");
		}
	});
}

function setNSFWEnabled(){
    var box = document.getElementById("nsfwEnabled");
    var url = "/nsfw/set/" + box.checked;
    request(url, function(){
		if(this.readyState == 4 && this.status == 200){
			var json = JSON.parse(this.responseText);
			if(json.status == "ok"){
				if(box.checked){
					sendToast("NSFW enabled");
				}
				else{
					sendToast("NSFW disabled");
				}
			}
			else{
				sendToast("Error: '" + json.status + "'");
			}
		}
		else if(this.readyState == 4 && this.status == 401){
        	sendToast("Error: '" + this.responseText + "'");
        }
	});
}
function getNSFWEnabled(){
    var box = document.getElementById("nsfwEnabled");
    var url = "/nsfw/get";
    request(url, function(){
		if(this.readyState == 4 && this.status == 200){
			var json = JSON.parse(this.responseText);
			if(json.nsfwenabled == true){
				box.checked = true;
			}
			else{
				box.checked = false;
			}
		}
		else if(this.readyState == 4 && this.status == 401){
        	sendToast("Error: '" + this.responseText + "'");
        }
	});
}

function setWelcomeMessageEnabled(){
    var box = document.getElementById("welcomeMessageEnabled");
    var url = "/welcomemessage/enabled/set/" + box.checked;
    request(url, function(){
		if(this.readyState == 4 && this.status == 200){
			var json = JSON.parse(this.responseText);
			if(json.status == "ok"){
				if(box.checked){
					sendToast("Welcome message enabled");
				}
				else{
					sendToast("Welcome message disabled");
				}
			}
			else{
				sendToast("Error: '" + json.status + "'");
			}
		}
		else if(this.readyState == 4 && this.status == 401){
        	sendToast("Error: '" + this.responseText + "'");
        }
	});
}
function getWelcomeMessageEnabled(){
    var box = document.getElementById("welcomeMessageEnabled");
    var url = "/welcomemessage/enabled/get";
    request(url, function(){
		if(this.readyState == 4 && this.status == 200){
			var json = JSON.parse(this.responseText);
			if(json.welcomemessageenabled == true){
				box.checked = true;
			}
			else{
				box.checked = false;
			}
		}
		else if(this.readyState == 4 && this.status == 401){
        	sendToast("Error: '" + this.responseText + "'");
        }
	});
}

function setWelcomeMessage(){
    var field = document.getElementById("welcomeMessage");
    var url = "/welcomemessage/set/" + encodeURIComponent(field.value.trim());
    request(url, function(){
		if(this.readyState == 4 && this.status == 200){
			var json = JSON.parse(this.responseText);
			if(json.status == "ok"){

			}
			else{
				sendToast("Error: '" + json.status + "'");
			}
		}
		else if(this.readyState == 4 && this.status == 401){
        	sendToast("Error: '" + this.responseText + "'");
        }
	});
}
function getWelcomeMessage(){
    var field = document.getElementById("welcomeMessage");
    var url = "/welcomemessage/get";
    request(url, function(){
		if(this.readyState == 4 && this.status == 200){
			if(this.responseText != "-1"){
				field.value = this.responseText;
				field.setAttribute('style', 'height:auto;overflow-y:auto;');
				field.setAttribute('style', 'height:' + (field.scrollHeight + 8) + 'px;overflow-y:auto;');
			}
		}
		else if(this.readyState == 4 && this.status == 401){
        	sendToast("Error: '" + this.responseText + "'");
        }
	});
}

function getWelcomeChannel(){
	var element = document.getElementById("channelSelect");
    var url = "/welcomechannel/get";
    request(url, function(){
		if(this.readyState == 4 && this.status == 200){
			var json = JSON.parse(this.responseText);
			element.value = json.welcomechannel;
		}
		else if(this.readyState == 4 && this.status == 401){
        	sendToast("Error: '" + this.responseText + "'");
        }
	});
}
function setWelcomeChannel(){
	var element = document.getElementById("channelSelect");
    var url = "/welcomechannel/set/" + element.value;
    request(url, function(){
		if(this.readyState == 4 && this.status == 200){
			var json = JSON.parse(this.responseText);
			getWelcomeChannel();
			if(json.status == "ok"){
				sendToast("Welcome channel set");
			}
			else{
				sendToast("Error: '" + json.status + "'");
			}
		}
		else if(this.readyState == 4 && this.status == 401){
        	sendToast("Error: '" + this.responseText + "'");
        }
	});
}


function setCommandPrefix(){
    var field = document.getElementById("commandPrefix");
    var url = "/commandprefix/set/" + encodeURIComponent(field.value);
    if(field.value.length == 1){
		request(url, function(){
			if(this.readyState == 4 && this.status == 200){
				var json = JSON.parse(this.responseText);
				getCommandPrefix();
				if(json.status == "ok"){
					sendToast("Command prefix set");
				}
				else{
					sendToast("Error: '" + json.status + "'");
				}
			}
			else if(this.readyState == 4 && this.status == 401){
            	sendToast("Error: '" + this.responseText + "'");
            }
		});
	}
}
function getCommandPrefix(){
    var field = document.getElementById("commandPrefix");
    var url = "/commandprefix/get";
    request(url, function(){
		if(this.readyState == 4 && this.status == 200){
			var json = JSON.parse(this.responseText);
			field.value = json.commandprefix;
		}
		else if(this.readyState == 4 && this.status == 401){
        	sendToast("Error: '" + this.responseText + "'");
        }
	});
}

function sendToast(text){
	Toastify({
		text: text,
		duration: 1000,
		close: true,
		gravity: "bottom", // `top` or `bottom`
		position: "center", // `left`, `center` or `right`
		backgroundColor: "#212121",
	}).showToast();
}

function toggleMobileNav(){
	if(displayMobileNav){
		displayMobileNav = false;
		document.getElementById("mobile-navigation").style.display = "none";
	}
	else{
		displayMobileNav = true;
		document.getElementById("mobile-navigation").style.display = "block";
	}

}

function OnInput() {
	if(this.style.height.slice(0, -2) <= 400){
		this.style.height = 'auto';
      	this.style.height = (this.scrollHeight + 8) + 'px';
	}
}


