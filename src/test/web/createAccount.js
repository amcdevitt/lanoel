//document.getElementById("btn_create").onclick = createAccount();
window.addEventListener('load', function()
		{
			alert("test");
			document.getElementById("btn_create").addEventListener("click", createAccount());
		})

function getAccountInfo()
{
	var userAccount;
	
	userAccount.userName = document.getElementById("email");
	userAccount.password = document.getElementById("password");
	userAccount.firstName = document.getElementById("firstName");
	userAccount.lastName = document.getElementById("lastName");
	userAccount.addressLine1 = document.getElementById("addressLine1");
	userAccount.addressLine2 = document.getElementById("addressLine2");
	userAccount.addressLine3 = document.getElementById("addressLine3");
	userAccount.addressLine4 = document.getElementById("addressLine4");
	userAccount.city = document.getElementById("city");
	userAccount.state = document.getElementById("state");
	userAccount.postalCode = document.getElementById("postalCode");
	userAccount.email = document.getElementById("email");
	
	return JSON.parse(userAccount);
	
}

function createAccount (getAccountInfo()) {

     jQuery.ajax({
         type: "POST",
         url: "http://ec2-52-0-109-161.compute-1.amazonaws.com:9200/accounts/useraccount",
         data: accountInfo.toJsonString(),
         contentType: "application/json; charset=utf-8",
         dataType: "json",
         success: function (data, status, jqXHR) {
              // do something
        	 alert("success!");
         },
     
         error: function (jqXHR, status) {            
              // error handler
        	 alert("failure! " + status);
         }

     });
}



/*
<font face="Open Sans" text-transform="uppercase">
<form action="demo_form.asp">
<div align="left">
Email Address: <input type="text" name="userName"><br>
Password: <input type="text" name="password"><br>
First Name: <input type="text" name="firstName"><br>
Last Name: <input type="text" name="lastName"><br>
Address Line 1: <input type="text" name="addressLine1"><br>
Address Line 2: <input type="text" name="addressLine2"><br>
Address Line 3: <input type="text" name="addressLine3"><br>
Address Line 4: <input type="text" name="addressLine4"><br>
City: <input type="text" name="city"><br>
State: <input type="text" name="state"><br>
Zip Code: <input type="text" name="postalCode"><br>
</div>
</form>
</font>


function scripts() {
if ( !is_admin() ) { // this if statement will insure the following code only gets added to your wp site and not the admin page cause your code has no business in the admin page right unless that's your intentions
	// jquery
		wp_deregister_script('jquery'); // this deregisters the current jquery included in wordpress
		wp_register_script('jquery', ("http://ajax.googleapis.com/ajax/libs/jquery/1.4.2/jquery.min.js"), false); // this registers the replacement jquery
		wp_enqueue_script('jquery'); // you can either let wp insert this for you or just delete this and add it directly to your template
	// your own script
		wp_register_script('yourscript', ( get_bloginfo('template_url') . '/yourscript.js'), false); //first register your custom script
		wp_enqueue_script('swfobject'); // then let wp insert it for you or just delete this and add it directly to your template
        // just in case your also interested
		wp_register_script('yourJqueryScript', ( get_bloginfo('template_url') . '/yourJquery.js'), array('jquery')); // this last part-( array('jquery') )is added in case your script needs to be included after jquery
		wp_enqueue_script('yourJqueryScript'); // then print. it will be added after jquery is added
	}
}
add_action( 'wp_print_scripts', 'scripts'); // now just run the function
*/