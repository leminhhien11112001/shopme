$(document).ready(function() {
	$(".linkMinus").on("click", function(evt) {
		evt.preventDefault();
		productId = $(this).attr("pid");
		quantityInput = $("#quantity" + productId);
		newQuantity = parseInt(quantityInput.val()) - 1;

		if (newQuantity > 0) {
			quantityInput.val(newQuantity);
		} else {
			showWarningModal('Minimum quantity is 1');
		}
	});

	$(".linkPlus").on("click", function(evt) {
		evt.preventDefault();
		productId = $(this).attr("pid");
		quantityInput = $("#quantity" + productId);
		newQuantity = parseInt(quantityInput.val()) + 1;
		
		url = contextPath + "get_quantity";
		params = {productId: productId};
		
		$.ajax({
			type: "GET",
			url: url,
			beforeSend: function(xhr) {
				xhr.setRequestHeader(csrfHeaderName, csrfValue);
			},
			data: params	
		}).done(function(quantity) {
			if (newQuantity <= quantity) {
				quantityInput.val(newQuantity);
			} else {
				showWarningModal('Maximum quantity is ' + quantity);
			}
		}).fail(function() {
			showErrorModal("Error while adding product to shopping cart.");
		});
	});	
});