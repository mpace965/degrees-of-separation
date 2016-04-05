var React = require('react');
import Paper from 'material-ui/lib/paper';
import CircularProgress from 'material-ui/lib/circular-progress';
import TextField from 'material-ui/lib/text-field';
import RaisedButton from 'material-ui/lib/raised-button';

var LastfmResultView = require('./lastfmResultView');

var LastfmSiteSearchView = React.createClass({

  handleSubmit: function() {
    this.props.setActiveView(LastfmResultView);
  },

  render: function() {
    const style = {
      height: '75%',
      width: '75%',
      padding: 10,
      margin: 20
    }

    return (
      <div className="siteSearchView">
        <Paper style={style} zDepth={1}>
          <h2 className="flexRowItem">Connect two artists on Last.fm</h2>
          <div className="flexRowItem">
            <TextField
              hintText="Connect one artist" />
            <TextField
              hintText="to another" />
            <RaisedButton label="Submit" onMouseUp={this.handleSubmit} />
          </div>
        </Paper>
      </div>
    );
  }
});

module.exports = LastfmSiteSearchView;
