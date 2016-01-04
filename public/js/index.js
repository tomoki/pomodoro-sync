var index = (function(){
  var changeTimer = function(text){
    $("#time").text(text);
  };
  var changeStatus = function(text){
    $("#status").text(text);
  };
  var formatFromSecond = function(second){
    var minute = Math.max(0,Math.floor(second / 60));
    var second = Math.max(0,Math.floor(second % 60));
    var add_zero = function(s){
      if(s.length == 1) return "0" + s;
      else              return s;
    };
    return add_zero(minute + "") + ":" + add_zero(second + "");
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
    api.getCurrent(function(current){
      refreshDisplay(current);
    });
  };
  // TODO: use "patch" to reduce complexity.
  var refreshDisplay = function(current, dones){
    if(current === null){
      changeButton("start", start);
      // TODO: fixme. It depends on time constants.
      changeTimer(formatFromSecond(60 * 25));
    }else if("error" in current){
      console.error(current);
    }else{
      changeButton("giveup", giveup);
      changeTimer(formatFromSecond((current.scheduledEndTime - Date.now()) / 1000));
    }
  };
  var start = function(){
    var message = $("#message").val();
    api.updateCurrent(message, function(current){
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
  changeButton("start", start);

  return {
    check: check,
    giveup: giveup,
    start: start
  };
})();

// TODO: We should use websocket.
setInterval(index.check, 333);
