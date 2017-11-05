var App = {

    init: function(){

        // initialize dialog
        $("#changeQuantityDialog").dialog({
            closeOnEscape: false,
            resizable: false,
            height: "auto",
            width: 400,
            modal: true,
            autoOpen: false,
        });

        // on click of cancel button, close dialog
        $("#changeQuantityDialogBtnCancel").click(function(){
            $("#changeQuantityDialog").dialog('close');
        });

         // on approve button click, redirect page
        $("#changeQuantityDialogBtnApprove").click(function(){
            var articleId =  $("#changeQuantityDialogArticleId").val();
            var newQuantity = $("#changeQuantityDialogTextField").val();

            App.editQuantity(articleId, newQuantity);

            $("#changeQuantityDialog").dialog('close');
        });
    },

    showEditQuantityDialog: function(articleId, oldQuantity){

        // get old quantity and fill text field with
        $("#changeQuantityDialogTextField").val(oldQuantity);

        // set article id
        $("#changeQuantityDialogArticleId").val(articleId);

        // finally open dialog
        $("#changeQuantityDialog").dialog("open");

    },

    editQuantity: function(articleId, newQuantity){
        document.location.href = UrlTree.BASKET + "?changeQtty=true&id=" + articleId + "&qtty=" + newQuantity;
    },

}

$(function(){
    App.init();
});