var App = {

    init: function () {
        $(".add-to-basket").click(function () {

            var id = $(this).data('id');
            var max = $(this).data('max');
            var qtty = $("#productQuantity" + id).val();
            var errorArea = $("#errorArea" + id);

            errorArea.css("opacity", "0");

            // check if not ordering too much
            if (qtty > max) {
                errorArea.html("Vous ne pouvez pas commander plus de " + max + " articles.");
                errorArea.animate({"opacity": 1}, 700);
                return;
            }

            if (qtty < 1) {
                errorArea.html("Vous ne pouvez pas commander moins de 1 article.");
                errorArea.animate({"opacity": 1}, 700);
                return;
            }

            document.location.href = UrlTree.BASKET + "?addToCart=true&id=" + id + "&qtty=" + qtty;

        });

        $(".show-description").click(function () {

            var title = $(this).data('title');
            var description = $(this).data('description');
            var weight = $(this).data('weight');

            console.log(title)
            console.log(description)
            console.log(weight)

            var modal = $('#product-modal');
            modal.find('.modal-title').html(title);
            modal.find('.description').html(description);
            modal.find('.weight').html(weight);

            modal.modal('show');
        });
    }

};

$(function () {
    App.init();
});