function drawLineChart(divName,dataArr, chartType){
	$("#"+divName).empty();
	$("#"+divName).highcharts({
		chart: {
            type: chartType
        },
        credits : {
			enabled : false
		},
        title: {
            text: null
        },
        xAxis: {
            categories: dataArr.xaxis
        },
        credits: {
            enabled: false
        },
        series: [ {
            name: 'Revenue',
            data: dataArr.yaxis
        }]
    });
}