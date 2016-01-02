var index = (function(){
  var changeTimer = function(text){
    $("#time").text(text);
  };
  var changeStatus = function(text){
    $("#status").text(text);
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
      if(current === null){
        console.log("null");
      } else if("error" in current){
        console.log("error. already activated");
      } else {
      }
    });
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
  };  var notify = function(message){
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
