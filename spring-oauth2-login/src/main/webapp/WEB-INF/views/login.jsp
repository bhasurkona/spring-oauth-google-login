<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>The Login Page</title>
</head>
<body style="margin-top: 10%; margin-left: 40%;">
	<form name="f" action="/login" method="POST">
		<table>
			<tbody>
				<tr>
					<td>Username:</td>
					<td><input type="text" name="username" required="required"
						autofocus="autofocus" autocomplete="on"></td>
				</tr>
				<tr>
					<td>Password:</td>
					<td><input type="password" name="password" required="required"></td>
				</tr>
				<tr>
					<td colspan="2"><input name="submit" type="submit"
						value="Login"></td>
				</tr>
			</tbody>
		</table>
	</form>
	<form name="googleLoginForm" method="post" action="/google_oauth2_login">
		<input type="submit" value="Google Login"/>
	</form>
</body>
</html>