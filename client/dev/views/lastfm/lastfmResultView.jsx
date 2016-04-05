var React = require('react');
var d3 = require('d3');
import Paper from 'material-ui/lib/paper';
import RaisedButton from 'material-ui/lib/raised-button';

var LastfmResultView = React.createClass({
  render: function() {
    const style = {
      height: '75%',
      width: '75%',
      padding: 10,
      margin: 20
    }

    return (
      <div className="resultView">
        <Paper style={style} zDepth={1}>
          Lastfm resultView.
        </Paper>
      </div>
    );
  }
});

module.exports = LastfmResultView;
