var api = (function(){
  var getCurrent = function(callback){
    return $.getJSON("api_current", {}, callback);
  };

  var updateCurrent = function(topic, callback){
    return $.post("api_updateCurrent", {
      topic: topic
    }, callback);
  };

  var giveupCurrent = function(callback){
    return $.post("api_giveupCurrent", {
    }, callback);
  };
  return {
    getCurrent: getCurrent,
    updateCurrent: updateCurrent,
    giveupCurrent: giveupCurrent
  };
})();
