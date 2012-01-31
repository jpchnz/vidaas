<html>
<body>
<h1>IAM Services test</h1>
<h2>Test ProjectRoleServlet</h2>
<form action="ProjectRoleServlet" METHOD="POST">
Name: <input type="text" name="name" /><br />
Password: <input type="password" name="password" />
<input type="submit" value="Submit" />
</form>

<h2>Test ReceivePost</h2>
<form action="ReceivePost" METHOD="POST">
Name: <input type="text" name="testName" /><br />
Password: <input type="password" name="testPassword" /><br />
<!-- Full path to private key: <input type="text" name="testKeyPath" /><br />-->
<input type="submit" value="Submit" />
</form>
</body>
</html>
