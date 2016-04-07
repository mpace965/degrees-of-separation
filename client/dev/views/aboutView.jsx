var React = require('react');
import Paper from 'material-ui/lib/paper';

var AboutView = React.createClass({
  render: function() {
    const style = {
      height: '75%',
      width: '75%',
      padding: 10,
      margin: 20
    }

    return (
      <div className="aboutView">
        <Paper style={style} zDepth={1}>
          <div>
            <center><h2>About Our Algorithm</h2></center>
          </div>
          <div>
            
          </div>
        </Paper>
      </div>
    );
  }
});


module.exports = AboutView;
