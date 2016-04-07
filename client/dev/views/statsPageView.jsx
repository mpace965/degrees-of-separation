import { FacebookButton, FacebookCount } from "react-social";
var React = require('react');
import Paper from 'material-ui/lib/paper';

var StatsPageView = React.createClass({

  render: function() {
    const style = {
      height: '75%',
      width: '75%',
      padding: 10,
      margin: 20
    }

    return (
      <div className="aboutPage">
        <Paper style={style} zDepth={1}>
          <div>
            <center><h2>Statistics</h2></center>
          </div>
          <div>
            <p>PLACE HOLDER TEXT</p>
          </div>
        </Paper>
        <FacebookButton url="https://degreeofconnection.com">Share us on Facebook</FacebookButton>
      </div>
    );
  }

});

module.exports = StatsPageView;
