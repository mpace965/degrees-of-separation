import { FacebookButton, FacebookCount } from "react-social";
var React = require('react');

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