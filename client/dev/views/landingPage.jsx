var React = require('react');
import Paper from 'material-ui/lib/paper';

var LandingPage = React.createClass({
  render: function() {
    const style = {
      height: '75%',
      width: '75%',
      padding: 10,
      margin: 20
    }

    return (
      <div className="landingPage">
        <Paper style={style} zDepth={1}>
          <center> 
              <font size="5"> Welcome to the Degrees of Separation website <br> Click connect to see how two objects are related! </br></font>
          </center>
        </Paper>
        <Paper style={style} zDepth={1}>
          <center> 
              <img src="http://cdn.zmescience.com/wp-content/uploads/2016/02/Six_degrees_of_separation_01.png" height="75%" width="75%"></img>
          </center>
        </Paper>
      </div>
    );
  }
});

module.exports = LandingPage;
