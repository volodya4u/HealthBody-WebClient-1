<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<!-- <html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>usercabinet</title>
</head> -->
<html lang="en">
<head>
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<!-- The above 3 meta tags *must* come first in the head; any other head content must come *after* these tags -->
<title>Cabinet</title>
<link rel="icon"
	href="https://d13yacurqjgara.cloudfront.net/users/678458/screenshots/1856046/h-icon.png">
<!-- Bootstrap -->
<link href="resources/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
	<nav class="navbar navbar-inverse navbar-fixed-top">
	<div class="container">
		<div class="navbar-header">
			<button type="button" class="navbar-toggle collapsed"
				data-toggle="collapse" data-target="#navbar" aria-expanded="false"
				aria-controls="navbar">
				<span class="sr-only">Toggle navigation</span> <span
					class="icon-bar">1</span> <span class="icon-bar">2</span> <span
					class="icon-bar">3</span>
			</button>
			<a class="navbar-brand" href="main.html">Health Body</a>
			<ul class="nav navbar-nav">
				<li><a href="usercabinet.html">${user.login}</a></li>
				<li><a href="userlist.html">Users</a></li>
				<li><a href="listGroups.html">Groups</a></li>
				<li><a href="listCompetitions.html">Competitions</a></li>
			</ul>
		</div>
		<div id="navbar" class="navbar-collapse collapse">
			<form class="navbar-form navbar-right" role="form">
				<a class="btn btn-success" type="submit" href="Logout.html">Sign
					out</a>
			</form>
		</div>
	</div>
	</nav>
	<br>
	<br>
	<div class="container">
		<img src="${user.photoURL}" alt="Cinque Terre" width="334"
			height="336">
		<table class="table">
			<tr class="info">
				<th>Login :</th>
				<th>Name :</th>
				<th>Surname :</th>
				<th>Age :</th>
				<th>Weight :</th>
				<th>Gender :</th>
				<th>Health :</th>
				<th>Role :</th>
			</tr>
			<tr class="info">
				<td>${user.login}</td>
				<td>${user.firstname}</td>
				<td>${user.lastname}</td>
				<td>${user.age}</td>
				<td>${user.weight}</td>
				<td>${user.gender}</td>
				<td>${user.health}</td>
				<td>${user.roleName}</td>
				<td><a href="editUser.html?userLogin=${user.login}">Edit</a></td>
			</tr>
		</table>
		<table class="table">
			<tr class="info">
				<th>Groups :</th>
			</tr>
			<tr class="info">
				<c:forEach items="${user.groups}" var="p">
					<td><a
						href="group.html?nameGroup=${p.name}&userLogin=${user.login}">${p.name}</a></td>
				</c:forEach>
			</tr>
		</table>
	</div>
	<!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->
	<script
		src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
	<!-- Include all compiled plugins (below), or include individual files as needed -->
	<script src="resources/js/bootstrap.min.js"></script>
</body>
</html>