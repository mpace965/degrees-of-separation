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
              <p>Welcome to the Degrees of Separation website</p>
              <p>Click connect to see how two objects are related!</p>
              <img src="http://cdn.zmescience.com/wp-content/uploads/2016/02/Six_degrees_of_separation_01.png" height="75%" width="75%"></img>
          </center>
        </Paper>
      </div>
    );
  }
});

module.exports = LandingPage;
