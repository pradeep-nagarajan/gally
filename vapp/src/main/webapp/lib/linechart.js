function drawLineChart(divName,xaxis, yaxis, chartType, stackFlag){
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
            categories: xaxis
        },
        yAxis: {
        	stackLabels: {
                enabled: true,
                style: {
                    fontWeight: 'bold',
                    color: (Highcharts.theme && Highcharts.theme.textColor) || 'gray'
                }
            }
        },
        credits: {
            enabled: false
        },
        plotOptions: {
            column: {
                stacking: stackFlag?'normal':''/*,
                dataLabels: {
                    enabled: stackFlag,
                    color: (Highcharts.theme && Highcharts.theme.dataLabelsColor) || 'white',
                    style: {
                        textShadow: '0 0 3px black, 0 0 3px black'
                    }
                }*/
            }
        },
        series: yaxis
    });
}