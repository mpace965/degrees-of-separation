var React = require('react');
var $ = require('jquery');
var RecentlySearchedView = require('./components/recentlySearchedView');
import Paper from 'material-ui/lib/paper';
import Snackbar from 'material-ui/lib/snackbar';

var StatsView = React.createClass({
  getInitialState: function() {
    return {
      stats: [],
      snackbarOpen: false,
      snackbarMessage: ''
    };
  },

  getStatsFromServer: function() {
    $.ajax({
        url: '/api/getStatistics',
        dataType: 'json',
        cache: false,
        success: function(data) {
          this.setState({stats: data});
        }.bind(this),
        error: function(xhr, status, err) {
          this.setState({snackbarOpen: true, snackbarMessage: xhr.responseText});
          console.error('/api/getStatistics', status, err.toString());
        }.bind(this),
        timeout: 60000
    });
  },

  handleRequestClose: function() {
    this.setState({snackbarOpen: false, snackbarMessage: ''});
  },

  componentDidMount: function() {
    this.getStatsFromServer();
  },

  render: function() {
    const style = {
      height: '75%',
      width: '75%',
      padding: 10,
      margin: 20
    }

    var stats = this.state.stats.map(function(stat) {
      return (
        <div key={stat}>{stat}</div>
      );
    });

    return (
      <div className="statsView">
        <Paper style={style} zDepth={1}>
          <div>
            <center><h2>Statistics</h2></center>
          </div>
          <div>
            {stats}
          </div>
        </Paper>
        <RecentlySearchedView />
        <Snackbar
          open={this.state.snackbarOpen}
          message={this.state.snackbarMessage}
          autoHideDuration={7000}
          onRequestClose={this.handleRequestClose} />
      </div>
    );
  }

});

module.exports = StatsView;
