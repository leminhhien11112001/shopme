$(document).ready(function() {
	formatRatingNumber();
});

$(".product-detail-rating-star").rating({
	displayOnly: true,
	hoverOnClera: false,
	showCaption: false,
	theme: 'krajee-svg'
});

function formatRatingNumber() {
	ratingText = $("#ratingNumber").text();
	formattedRating = $.number(ratingText, 2, '.', ',');
	$("#ratingNumber").text(formattedRating);
}