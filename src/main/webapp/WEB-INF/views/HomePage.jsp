<html lang="en">
  <head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <!-- The above 3 meta tags *must* come first in the head; any other head content must come *after* these tags -->
    <title>Health Body</title>
<link rel="icon" href="https://d13yacurqjgara.cloudfront.net/users/678458/screenshots/1856046/h-icon.png">
    <!-- Bootstrap -->
    <link href="resources/css/bootstrap.min.css" rel="stylesheet">

     <link href="resources/css/full-slider.css" rel="stylesheet">

    <!-- HTML5 shim and Respond.js for IE8 support of HTML5 elements and media queries -->
    <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
    <!--[if lt IE 9]>
      <script src="https://oss.maxcdn.com/html5shiv/3.7.2/html5shiv.min.js"></script>
      <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
    <![endif]-->
  </head>
  <body>
    <nav class="navbar navbar-inverse navbar-fixed-top" role="navigation">
      <div class="container">
        <div class="navbar-header">
          <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar" aria-expanded="false" aria-controls="navbar">
            <span class="sr-only">Toggle navigation</span>
            <span class="icon-bar">1</span>
            <span class="icon-bar">2</span>
            <span class="icon-bar">3</span>
          </button>
          <a class="navbar-brand" href="HomePage.html">Health Body</a>
				<ul class="nav navbar-nav">
					<li><a href="usercabinet.html">My Cabinet</a></li>
					<li><a href="#">Services</a></li>
					<li><a href="#">Contact</a></li>
					<li><a href="userlist.html">User List</a></li>
				</ul>
			</div>

        <div id="navbar" class="navbar-collapse collapse">
          <form class="navbar-form navbar-right" role="form">
            <a class="btn btn-success" type="submit" href="Login.html">Sign in</a>
          </form>
        </div><!--/.navbar-collapse -->
      </div>
    </nav>
      <div id="carousel-home" class="carousel slide" data-ride="carousel">
  <!-- Indicators -->
  <ol class="carousel-indicators">
    <li data-target="#carousel-home" data-slide-to="0" class="active"></li>
    <li data-target="#carousel-home" data-slide-to="1"></li>
   <!--  <li data-target="#carousel-home" data-slide-to="2"></li> -->
  </ol>

  <!-- Wrapper for slides -->
    <div class="carousel-inner">
    <div class="item active">
      		<div class="fill" style="background-image:url('resources/img/homepage_1.jpg');">
      		</div>
    	</div>
    	<!-- <div class="item">
      		<div class="fill" style="background-image:url('https://pbs.twimg.com/media/CkskUoRXAAAYiiL.png');">
      		</div>
    	</div> -->
    	<div class="item">
      		<div class="fill" style="background-image:url('resources/img/homepage_2.jpg');">
      		</div>
    	</div>
  	</div>
  <!-- Controls -->
        <a class="left carousel-control" href="#carousel-home" data-slide="prev">
            <span class="icon-prev"></span>
        </a>
        <a class="right carousel-control" href="#carousel-home" data-slide="next">
            <span class="icon-next"></span>
        </a>
              <footer>
        <p class="site-info">&copy; 2016 softserve IT Academy, Inc.</p>
      </footer>
    </div>


		<div id="signupbox" style="display: none; margin-top: 50px"
			class="mainbox col-md-6 col-md-offset-3 col-sm-8 col-sm-offset-2">
			<hr>
			<div class="panel panel-info">
				<div class="panel-heading">
					<div class="panel-title">Sign Up</div>
					<div
						style="float: right; font-size: 85%; position: relative; top: -10px">
						<a id="signinlink" href="#"
							onclick="$('#signupbox').hide(); $('#loginbox').show()">Sign
							In</a>
					</div>
				</div>
				<div class="panel-body">
					<form id="signupform" class="form-horizontal" role="form">
						<div id="signupalert" style="display: none"
							class="alert alert-danger">
							<p>Error:</p>
							<span></span>
						</div>
						<div class="form-group">
							<label for="email" class="col-md-3 control-label">Email</label>
							<div class="col-md-9">
								<input type="text" class="form-control" name="email"
									placeholder="Email Address">
							</div>
						</div>
						<div class="form-group">
							<label for="firstname" class="col-md-3 control-label">First
								Name</label>
							<div class="col-md-9">
								<input type="text" class="form-control" name="firstname"
									placeholder="First Name">
							</div>
						</div>
						<div class="form-group">
							<label for="lastname" class="col-md-3 control-label">Last
								Name</label>
							<div class="col-md-9">
								<input type="text" class="form-control" name="lastname"
									placeholder="Last Name">
							</div>
						</div>
						<div class="form-group">
							<label for="password" class="col-md-3 control-label">Password</label>
							<div class="col-md-9">
								<input type="password" class="form-control" name="passwd"
									placeholder="Password">
							</div>
						</div>
						<div class="form-group">
							<!-- Button -->
							<div class="col-md-offset-3 col-md-9">
								<button id="btn-signup" type="button" class="btn btn-info">
									<i class="fa fa-hand-o-right" aria-hidden="true"></i>&nbsp;Sign
									Up
								</button>
								<span style="margin-left: 8px;">or</span>
							</div>
						</div>
						<div style="border-top: 1px solid #999; padding-top: 20px"
							class="form-group">
							<div class="col-md-offset-3 col-md-9">
								<button id="btn-gsignup" type="button" class="btn btn-primary">
									<i class="fa fa-google"></i>&nbsp;Sign Up with Google
								</button>
							</div>
						</div>
					</form>
				</div>
			</div>
			      <footer>
        <p class="site-info">&copy; 2016 softserve IT Academy, Inc.</p>
      </footer>
		</div>
     <!-- /container -->
      <!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
    <!-- Include all compiled plugins (below), or include individual files as needed -->
    <script src="resources/js/bootstrap.min.js"></script>

  </body>
</html>