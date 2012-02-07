<html>
<head>
<script type="text/javascript">

function register()
{
 function getHttpObject(){
        var xmlhttp = null;
        if (window.XMLHttpRequest){xmlhttp=new XMLHttpRequest();}
        else if (window.ActiveXObject){xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");}
        else {alert("Your browser does not support XMLHTTP!");}
        return xmlhttp;
    }  
    
    function sendReq(str){
    		alert("Hi");
        xmlhttp = getHttpObject();
        xmlhttp.onreadystatechange=function() {
        		alert("1");
            if(xmlhttp.readyState==4) {
            		alert("2");
                alert(xmlhttp.responseText);
            }
        }
        xmlhttp.open("GET","iam/KeyUtilitiesServlet?city="+str,true);
        xmlhttp.send(null);
    }

</script>
</head>
<body>
<h1>IAM Key Reporting Tool</h1>
<h2>Private keys found:</h2>
<form name="ajxfrm">
    <table>
        <tr>
            <td>City</td><td><input type="text" name="city" value=""/></td>
        </tr>
        <tr>
            <td>State</td><td><input type="text" name="state" value="" onfocus="sendReq(ajxfrm.city.value);"/></td>
        </tr>
    </table>

</form>
<!--
<h1>IAM Services test</h1>
<h2>Test ProjectRoleServlet</h2>
<form action="ProjectRoleServlet" METHOD="POST">
Name: <input type="text" name="isOwner" /><br />
<input type="submit" value="Submit" />
</form>

<h2>Test ReceivePost</h2>
<form action="ReceivePost" METHOD="POST">
Name: <input type="text" name="testName" /><br />
Password: <input type="password" name="testPassword" /><br />
<input type="submit" value="Submit" />
</form>
-->
</body>
</html>
