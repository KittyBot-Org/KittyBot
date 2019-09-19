
var userId = document.getElementById("userIdField").value;
//var host = "http://anteiku.de/";
var host = "http://localhost/";
var displayMobileNav = false;

loadGuilds();

function request(url, func){
	var xmlhttp = new XMLHttpRequest();
	xmlhttp.onreadystatechange = func;
	xmlhttp.open("GET", url, true);
	xmlhttp.send();
}

function loadGuilds(){
    var desktop = document.getElementById("navigation");
    var mobile = document.getElementById("mobile-navigation-list");
    var list = document.getElementById("server-list");
    var url = host + "user/" + userId + "/guilds/get";
    request(url, function(){
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
