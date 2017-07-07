var App = {

    init: function(){
        $(".add-to-basket").click(function(){

            var id = $(this).data('id');
            var max = $(this).data('max');
            var qtty = $("#productQuantity" + id).val();
            var errorArea = $("#errorArea" + id);

            // check if not ordering too much
            if(qtty > max){
                errorArea.html("Vous ne pouvez pas commander plus de " + max + " articles.");
                errorArea.css("display", "block");
                return;
            }

            document.location.href = UrlTree.BASKET + "?addToCart=" + qtty + "&id=" + id;

        });
    }

};

$(function(){
   App.init();
});