function piechartlegend() {
		return '<span style="color: ' + this.color + '">'
				+ this.percentage.toFixed(2) + '%</span> '+ this.name + " ["+this.val+"]";
	}

function plainlegend() {
	return this.name;
}

function drawDonutPieChart(divName,dataArr,imageName,toolTipDesc, legendwidth, percentageLegend){
		
		var result=false;
		for(i=0;i<dataArr.length;i++) {
			if(dataArr[i].y>0){
				result=true;
				break;
			}
		}
		if(result){
			var piebbsentoptions = {
				chart : {
					type : 'pie',
					renderTo: divName,
					width : parseInt($("#"+divName).width(),0),
					backgroundColor:'rgba(255, 255, 255, 0.1)'
				},
				title : {
					text : null
				},
				credits : {
					enabled : false
				},
				plotOptions : {
					pie : {
						size : '100%',
						allowPointSelect : true,
						cursor : 'pointer',
						dataLabels : {
							enabled : false
						},
						showInLegend : true
					}
				},
				tooltip : {
					pointFormat : '{series.name}: <b>{point.percentage:.1f}%</b>'
				},
				legend : {
					layout : 'vertical',
					align : 'right',
					verticalAlign : 'middle',
					itemWidth : legendwidth,
					useHTML : true,
					x : 0,
					y : 0,
					borderWidth : 0,
					labelFormatter : percentageLegend?piechartlegend:plainlegend
				},
				series : [ {
					type : 'pie',
					name : toolTipDesc+'%',
					innerSize : '70%',
					data : dataArr
					
				} ]
			};
		
		var piebbsentchart = new Highcharts.Chart(
				piebbsentoptions,
				function(chart) { // on complete
					if(imageName!="")//Image resolution should be 50 x 50 pixels
					{
						var span = '<span style="position:absolute; text-align:center;';
						span += 'left: 21%; top: 38%; z-index: -1;">';									
						span += '<img src="images/'+imageName+'" width="25" height="30"/>';
						span += '</span>';
						var parent = document.getElementById(divName);
						var child = parent.childNodes[0];
						
						$("#"+child.id).append(span);
					}
				});
		}else{
			$("#"+divName).html('');
			var span = '<span style="position:relative; text-align:center;';
			span += 'left: 0px; top: 25%; z-index: 0;">';
			if(imageName!="")//Image resolution should be 50 x 50 pixels
			{
				span += '<img src="images/'+imageName+'"/>';
			}
			span += '<h3 align="center">Data not Available</h3>';
			span += '</span>';
			$("#"+divName).append(
					span);
		}
	}

