// Sales Report by Date
var data;
var chartOptions;
var totalGrossSales;
var totalOrders;

$(document).ready(function() {
	$(".button-sales-by-date").on("click", function() {
		
		$(".button-sales-by-date").each(function(e) {
			$(this).removeClass('btn-primary').addClass('btn-light');
		});
		
		$(this).removeClass('btn-light').addClass('btn-primary');
		
		period = $(this).attr("period");		
		loadSalesReportByDate(period);
	});
});
	
function loadSalesReportByDate(period) {
	requestURL = contextPath + "reports/sales_by_date/" + period;
	
	$.get(requestURL, function(responseJSON) {
		prepareChartData(responseJSON);
		customizeChart(period);
		drawChart(period);
	});
}

function prepareChartData(responseJSON) {
  data = new google.visualization.DataTable();
  data.addColumn('string', 'Date');
  data.addColumn('number', 'Gross Sales');
  data.addColumn('number', 'Orders');

  totalGrossSales = 0.0;
  totalOrders = 0;

  $.each(responseJSON, function(index, reportItem) {
    data.addRows([[reportItem.identifier, reportItem.grossSales, reportItem.ordersCount]]);
    totalGrossSales += parseFloat(reportItem.grossSales);
    totalOrders += parseInt(reportItem.ordersCount);
  });
}


function customizeChart(period) {
  chartOptions = {
    title: getChartTitle(period),
    'height': 360,
    legend: {position: 'top'},

    series: {
      0: {targetAxisIndex: 0},
      1: {targetAxisIndex: 1},
    },

    vAxes: {
      0: {title: 'Gross Sales', format: 'currency'},
      1: {title: 'Number of Orders'}
    }
  };
}


function drawChart(period) {
  var salesChart = new google.visualization.ColumnChart(document.getElementById('chart_sales_by_date'));
  salesChart.draw(data, chartOptions);

  $("#textTotalGrossSales").text("$" + $.number(totalGrossSales, 2));
  $("#textTotalOrders").text(totalOrders);  
}


function getChartTitle(period) {
	if (period == "last_7_days") return "Sales in Last 7 Days";
	if (period == "last_30_days") return "Sales in Last 30 Days";
	if (period == "last_6_months") return "Sales in Last 6 Months";
	
	return "";
}

