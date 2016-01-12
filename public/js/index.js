var main = (function(){
  var time     = $("#time");
  var status   = $("#status");
  var sbutton  = $("#sbutton");
  var message  = $("#message");
  var timeline = $("#timeline");

  var formatFromSecond = function(second){
    var minute = Math.max(0,Math.floor(second / 60));
    var second = Math.max(0,Math.floor(second % 60));
    var add_zero = function(s){
      if(s.length == 1) return "0" + s;
      else              return s;
    };
    return add_zero(minute + "") + ":" + add_zero(second + "");
  };
  var showInProgress = function(rest, topic){
    sbutton.prop("disabled", false);
    message.attr("readonly", true);
    message.val(topic);
    message.addClass("readonly");
    status.text("Let's start new work!");
    time.text(rest);
    changeButton("giveup", giveup);
  };

  var showInRest = function(rest){
    sbutton.prop("disabled", false);
    message.attr("readonly", false);
    message.removeClass("readonly");
    status.text("Have a rest");
    time.text(rest);
    changeButton("start", start);
  };

  var button_callback = null;
  var changeButton = function(text, callback){
    var sbutton = $("#sbutton");
    if(button_callback !== null){
      sbutton.off("click", button_callback);
    }
    button_callback = callback;
    sbutton.val(text);
    sbutton.on("click", callback);
  };

  var check = function(){
    api.getCurrent(refreshTime);
    api.getPastWorks(refreshPastWorks);
  };

  var refreshTime = function(current){
    if(current === null){
      showInRest(formatFromSecond(60 * 25));
    }else{
      showInProgress(formatFromSecond((current.scheduledEndTime - Date.now()) / 1000),
                     current.topic);
    }
  };

  // TODO: should use patch.
  var refreshPastWorks = function(works){
    var tbody = timeline.find("tbody");
    tbody.find("tr").remove();
    works.forEach(function(work){
      var row       = tbody[0].insertRow(0);
      var ac_or_not = row.insertCell(0);
      var date      = new Date(work.startTime);
      ac_or_not.style.color           = "white";
      ac_or_not.style.backgroundColor = work.succeeded ? "#458543" : "#F89406";
      ac_or_not.textContent           = work.succeeded ? "YES" : "NO";
      row.insertCell(1).textContent   = (date.getMonth()+1) + "/" + date.getDate() + " " + date.getHours() + ":" + date.getMinutes();
      row.insertCell(2).textContent   = work.topic;
    });
  };


  var start = function(){
    api.updateCurrent(message.val(), function(current){
      if(current.error !== undefined){
        console.log("error. already activated");
      }
      console.log(current);
    });
  };
  var giveup = function(){
    api.giveupCurrent(function(done){
    });
  };

  var notify = function(message){
    Notification.requestPermission();
    var notification = new Notification(message);
  };

  return {
    check: check
  };
})();
setInterval(main.check, 333);
