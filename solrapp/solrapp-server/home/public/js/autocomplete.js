$(document).ready(function() {

$("#q").autocomplete({
    source: function(req, rsp) {
        $.getJSON("http://suggestqueries.google.com/complete/search?callback=?",
           { 
               "hl":"ja",                
               "jsonp":"suggestCallBack",
               "q":req.term,
               "client":"youtube"
           }
        );
        suggestCallBack = function (data) {
            var suggestions = [];
            $.each(data[1], function(key, val) {
                suggestions.push({"value":val[0]});
            });
            rsp(suggestions);
        };       
    },
    delay: 200,
    focus: function(event, ui) {
        $("#q").val(ui.item.label);
        return false;
    },
    select: function(event, ui) {
        if ($("#q").val() != "") {
            $("#search").submit();
        }
        return false;    
    }      
});

});
