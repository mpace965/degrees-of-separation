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
          Welcome to the Degrees of Separation website! Click connect to see how two objects of your choice are related.
        </Paper>
      </div>
    );
  }
});

module.exports = LandingPage;
