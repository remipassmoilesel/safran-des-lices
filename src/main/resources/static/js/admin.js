var App = {

    checkProductAndSendForm: function(productId) {

        var form = $("#productForm" + productId);
        var errorArea = $(".productErrorArea");
        var qtty = form.find("input[name='quantity']").val().trim();
        var price = form.find("input[name='price']").val().trim();

        function showError(htmlError) {
            errorArea.css("opacity", 0);
            errorArea.html(htmlError);
            errorArea.animate({"opacity": 1}, 800);
        }

        showError("");

        if (qtty.match(/^[0-9]+(\.[0-9]+)?$/i) === null) {
            showError("Quantité invalide: " + qtty + " (produit: " + productId + ")");
            return;
        }

        if (price.match(/^[0-9]+(\.[0-9]+)?$/i) === null) {
            showError("Prix invalide: " + price + " (produit: " + productId + ")");
            return;
        }

        form.submit();
    },

}