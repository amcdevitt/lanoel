$(document).ready(function () {

        $('#loginButton').click(function () {
            jQuery.support.cors = true;
            $.ajax(
                {
                    type: 'POST',
                    //url: 'http://ec2-52-0-109-161.compute-1.amazonaws.com:9200/accounts/login',
                    url: 'http://localhost:9200/accounts/login',
                    data: "{}",
                    contentType: "application/json; charset=utf-8",
                    dataType: "jsonp",
                    success: function (data) {
                        alert('success');
                    },
                    error: function (data) {
                        alert('failure');
                    },
                    beforeSend: setHeader,
                    xhrFields: {
                        // The 'xhrFields' property sets additional fields on the XMLHttpRequest.
                        // This can be used to set the 'withCredentials' property.
                        // Set the value to 'true' if you'd like to pass cookies to the server.
                        // If this is enabled, your server must respond with the header
                        // 'Access-Control-Allow-Credentials: true'.
                        withCredentials: false
                      },

                      headers: {
                        // Set any custom headers here.
                        // If you set any non-simple headers, your server must include these
                        // headers in the 'Access-Control-Allow-Headers' response header.
                    	  'username':'Aaron',
                    	  'password':'password1'
                      }
                });
            //alert('button click');
        });
    });

function setHeader(xhr) {
	alert("hey");
    xhr.setRequestHeader('username', 'Aaron');
    xhr.setRequestHeader('password', 'password1');
  }

/*
$(document).on('click', 'LoginButton', function () {
	alert("clicked!");
		var username = $(this).attr('username');
		var password = $(this).attr('password');
	     jQuery.ajax({
	         type: "POST",
	         url: "http://ec2-52-0-109-161.compute-1.amazonaws.com:9200/accounts/login",
	         contentType: "application/json; charset=utf-8",
	         headers: {'userName' : 'Aaron', 'password' : 'password1'},
	         dataType: "json",
	         success: function (data) {
	             // do something
	        	 alert("success");
	         },
	     
	         error: function (status) {
	             // error handler
	        	 alert("failure: " + status);
	         }     
	     });  
});
*/