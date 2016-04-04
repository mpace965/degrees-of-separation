import { FacebookButton, FacebookCount } from "react-social";
var React = require('react');
import Paper from 'material-ui/lib/paper';

var StatsPageView = React.createClass({
   
  render: function() {
  
  const style = {
      height: '75%',
      width: '75%',
      padding: 10,
      margin: 2
    }
    
  
    return (
      <div className="aboutPage">
        <Paper style={style} zDepth={1}>
          <div className="Title">
          		<center> 
          			<font size ="6"> Stats Page </font> 
          		</center>
          </div>
        </Paper>
        <Paper style={style} zDepth={1}>
          <div className="Does">
          	 	<font size = "3"> PLACE HOLDER TEXT </font>        		
          </div>
        </Paper>
      		<FacebookButton url="https://degreeofconnection.com">
      			{" Share us on Facebook"}
      		</FacebookButton>
      </div>
    );
  }
  
});

module.exports = StatsPageView;