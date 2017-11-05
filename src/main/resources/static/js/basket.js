var App = {

    init: function(){

        $("#changeQuantityDialog").dialog({
          resizable: false,
          height: "auto",
          width: 400,
          modal: true,
          autoOpen: false,
          buttons: {
            "Changer la quantité": function() {
              App.editQuantity()
              $(this).dialog("close");
            },
            "Annuler": function() {
              $(this).dialog("close");
            }
          }
        });

    },

    showEditQuantityDialog: function(articleId, oldQuantity){

        console.log($("#newQuantityTextField"))

        $("#newQuantityTextField").val(oldQuantity);
        $("#changeQuantityDialog").open();

    },

    editQuantity: function(articleId, newQuantity){


    },

}

$(function(){
    App.init();
});