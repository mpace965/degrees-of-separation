import { FacebookButton, FacebookCount } from "react-social";
var React = require('react');


FB.login(function(response) {
    if (response.authResponse) {
        var access_token =   FB.getAuthResponse()['accessToken'];
        FB.api('/me/photos?access_token='+access_token, 'post', { url: IMAGE_SRC, access_token: access_token }, function(response) {
            if (!response || response.error) {
                //alert('Error occured: ' + JSON.stringify(response.error));
            } else {
                //alert('Post ID: ' + response);
            }
        });
    } else {
        //console.log('User cancelled login or did not fully authorize.');
    }
}, {scope: 'publish_stream'});


var StatsPageView = React.createClass({
   
   

   
   
   
   
   
  render: function() {
  let url = "https://degreeofconnection.com";
    return (
    <div>
   	 <div>
    	<div>
    		<center> <font size="10"> Statistic Page </font></center>
    		
    	</div>
    	
    	
    	
    	</div>
      <FacebookButton url={url}>
        {" Share us on Facebook"}
      </FacebookButton>
      </div>
    );
  }
  
});

module.exports = StatsPageView;