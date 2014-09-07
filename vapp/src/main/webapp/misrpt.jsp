<!doctype html>
<html lang="en">
<head>
<meta charset="utf-8">
<title>Bootstrap Admin</title>
<meta content="IE=edge,chrome=1" http-equiv="X-UA-Compatible">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<meta name="description" content="">
<meta name="author" content="">


<link rel="stylesheet" type="text/css"
	href="lib/bootstrap/css/bootstrap.css">
<link rel="stylesheet" href="lib/font-awesome/css/font-awesome.css">
<link href="stylesheets/jquery-ui.css" rel="stylesheet" />
<script src="lib/jquery-1.11.1.min.js" type="text/javascript"></script>
<script src="lib/jquery-ui.js" type="text/javascript"></script>
<link rel="stylesheet" type="text/css" href="stylesheets/theme.css">
<link rel="stylesheet" type="text/css" href="stylesheets/vapp.css">
<script src="lib/ajaxcall.js" type="text/javascript"></script>

</head>
<body class=" theme-blue">

	<!-- Demo page code -->

	<script type="text/javascript">
		$(window).resize(function() {
			$("#content").css('min-height', ($(window).height() - 111));
			$("#content").css('min-width', ($(window).width() - 150));
		});
		getJson('/vapp/gettempdata', 'temp');

		function tempDataPush(data) {
			if (data.length > 0)
				window.location.href = 'missing.html';
		}
		
		function genMISRpt(){
			var url='/vapp/genmisrpt?fromDate='+$('#fromDate').val()+'&toDate='+$('#toDate').val();
			return url;
		}
		
		function RedirectURL()
		{
		    window.location= genMISRpt();
		}
		
	</script>
	<style type="text/css">
.navbar-default .navbar-brand,.navbar-default .navbar-brand:hover {
	color: #fff;
}
</style>

	<!-- Le HTML5 shim, for IE6-8 support of HTML5 elements -->
	<!--[if lt IE 9]>
      <script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
    <![endif]-->

	<!--[if lt IE 7 ]> <body class="ie ie6"> <![endif]-->
	<!--[if IE 7 ]> <body class="ie ie7 "> <![endif]-->
	<!--[if IE 8 ]> <body class="ie ie8 "> <![endif]-->
	<!--[if IE 9 ]> <body class="ie ie9 "> <![endif]-->
	<!--[if (gt IE 9)|!(IE)]><!-->

	<!--<![endif]-->

	<div class="navbar navbar-default" role="navigation">
		<div class="navbar-header">
			<button type="button" class="navbar-toggle collapsed"
				data-toggle="collapse" data-target=".navbar-collapse">
				<span class="sr-only">Toggle navigation</span> <span
					class="icon-bar"></span> <span class="icon-bar"></span> <span
					class="icon-bar"></span>
			</button>
			<a class="" href="index.html"><span class="navbar-brand"><span
					class="fa fa-paper-plane"></span> Aircraft</span></a>
		</div>

		<div class="navbar-collapse collapse" style="height: 1px;">
			<ul id="main-menu" class="nav navbar-nav navbar-right">
				<li class="dropdown hidden-xs"><a href="#"
					class="dropdown-toggle" data-toggle="dropdown"> <span
						class="glyphicon glyphicon-user padding-right-small"
						style="position: relative; top: 3px;"></span> N Pradeep <i
						class="fa fa-caret-down"></i>
				</a>

					<ul class="dropdown-menu">
						<li><a href="./">My Account</a></li>
						<li class="divider"></li>
						<li class="dropdown-header">Admin Panel</li>
						<li><a href="./">Users</a></li>
						<li><a href="./">Security</a></li>
						<li><a tabindex="-1" href="./">Payments</a></li>
						<li class="divider"></li>
						<li><a tabindex="-1" href="sign-in.html">Logout</a></li>
					</ul></li>
			</ul>

		</div>
	</div>


	<div class="sidebar-nav">
		<ul>
			<li><a href="#" data-target=".dashboard-menu" class="nav-header"
				data-toggle="collapse"><i class="fa fa-fw fa-dashboard"></i>
					Inputs<i class="fa fa-collapse"></i></a></li>
			<li><ul class="dashboard-menu nav nav-list collapse">
					<li><a href="index.html"><span class="fa fa-caret-right"></span>
							Main</a></li>
					<li><a href="ignore.html"><span class="fa fa-caret-right"></span>
							Ignore Ledger List</a></li>
					<li><a href="user.html"><span class="fa fa-caret-right"></span>
							User Profile</a></li>
					<li><a href="data-entry.html"><span
							class="fa fa-caret-right"></span> Data Entry</a></li>
					<li><a href="uploader.html"><span
							class="fa fa-caret-right"></span> Upload Data</a></li>
				</ul></li>

			<li data-popover="true"
				data-content="Reporting features are available here" rel="popover"
				data-placement="right"><a href="#" data-target=".premium-menu"
				class="nav-header collapsed" data-toggle="collapse"><i
					class="fa fa-fw fa-fighter-jet"></i> Reporting<i
					class="fa fa-collapse"></i></a></li>
			<li><ul class="premium-menu nav nav-list collapse in">
					<li class="visible-xs visible-sm"><a href="#">- Reporting
							features are available here -</a></span>
					<li class="active"><a href="misrpt.jsp"><span
							class="fa fa-caret-right"></span> MIS Report</a></li>
				</ul></li>

			<li><a href="#" data-target=".accounts-menu"
				class="nav-header collapsed" data-toggle="collapse"><i
					class="fa fa-fw fa-briefcase"></i> Account <span
					class="label label-info">+3</span></a></li>
			<li><ul class="accounts-menu nav nav-list collapse">
					<li><a href="sign-in.html"><span class="fa fa-caret-right"></span>
							Sign In</a></li>
					<li><a href="sign-up.html"><span class="fa fa-caret-right"></span>
							Sign Up</a></li>
					<li><a href="reset-password.html"><span
							class="fa fa-caret-right"></span> Reset Password</a></li>
				</ul></li>


			<li><a href="help.html" class="nav-header"><i
					class="fa fa-fw fa-question-circle"></i> Help</a></li>
		</ul>
	</div>

	<div class="content" id="content">
		<div class="header">

			<h1 class="page-title">MIS Report</h1>
			<ul class="breadcrumb">
				<li><a href="index.html">Home</a></li>
				<li class="active">MIS Report</li>
			</ul>

		</div>
		<div class="main-content">

			<div class="faq-content">
				<div class="row">
					<div class="col-sm-9 col-md-12">
						<div class="panel panel-default">
							<p class="panel-heading">Generate MIS Report</p>
							<div class="panel-body">
								Transaction Date From: <input type="text" name="fromDate"
									id="fromDate" style="margin-right: 5px">&nbsp;&nbsp;&nbsp;To&nbsp;&nbsp;
								<input type="text" name="toDate" id="toDate"
									style="margin-right: 5px"> <br />
								<br /> Report Type: <select id="reportId">
									<option>Net TB</option>
									<option>Profit and Loss</option>
									<option>MIS</option>
								</select> <br />
								<br />
								<a  class="btn btn-primary" href="#" onclick="RedirectURL();return false;">Generate Report</a>
							</div>
						</div>

					</div>
				</div>
			</div>

		</div>
	</div>
	<div class="footer">
		<hr>
		<p class="pull-right">A product of VAPP</p>
		<p>© 2014 VAPP</p>
	</div>

	<script src="lib/bootstrap/js/bootstrap.js"></script>
	<script type="text/javascript">
		$(function() {
			$("#fromDate").datepicker({
				showOn : "button",
				dateFormat : 'dd-mm-yy',
				buttonImageOnly : true,
				buttonImage : 'images/calendar.gif'
			});
			$("#toDate").datepicker({
				showOn : "button",
				dateFormat : 'dd-mm-yy',
				buttonImageOnly : true,
				buttonImage : 'images/calendar.gif'
			});
		});
	</script>

</body>
</html>
