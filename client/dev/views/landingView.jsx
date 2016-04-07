var React = require('react');
var RecentlySearchedView = require('./recentlySearchedView');
import Paper from 'material-ui/lib/paper';

var LandingView = React.createClass({
  render: function() {
    const style = {
      height: '75%',
      width: '75%',
      padding: 10,
      margin: 20
    }

    return (
      <div className="landingView">
        <Paper style={style} zDepth={1}>
          <center>
              <p>Welcome to the Degrees of Separation website</p>
              <p>Click connect to see how two objects are related!</p>
          </center>
        </Paper>
        <RecentlySearchedView />
      </div>
    );
  }
});

module.exports = LandingView;
