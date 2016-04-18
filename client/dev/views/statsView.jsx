var React = require('react');
var $ = require('jquery');
var RecentlySearchedView = require('./components/recentlySearchedView');
import Paper from 'material-ui/lib/paper';
import Snackbar from 'material-ui/lib/snackbar';
import RaisedButton from 'material-ui/lib/raised-button';

var StatsView = React.createClass({
  getInitialState: function() {
    return {
      stats: {},
      snackbarOpen: false,
      snackbarMessage: '',
      lastRefresh: 0
    };
  },

  getStatsFromServer: function() {
    $.ajax({
        url: '/api/getStatistics',
        dataType: 'json',
        cache: false,
        success: function(data) {
          this.setState({stats: data});
          this.setState({lastRefresh: Date.now()});
        }.bind(this),
        error: function(xhr, status, err) {
          this.setState({snackbarOpen: true, snackbarMessage: xhr.responseText});
          console.error('/api/getStatistics', status, err.toString());
        }.bind(this),
        timeout: 300000
    });
  },

  throttleRefresh: function() {
    if (Date.now() - this.state.lastRefresh > 5000) {
      this.getStatsFromServer();
    } else {
      console.log("You've been throttled.");
    }
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

    return (
      <div className="statsView">
        <Paper style={style} zDepth={1}>
          <div>
            <center><h2>Last.fm Statistics</h2></center>
          </div>
          <br/>
          <div className="statCols">
            <Paper className="statColItem" zDepth={2}>
              <h3>General Statistics</h3>
              <p>Total Connections Chains Made: <strong>{this.state.stats.totalConnectionChains}</strong></p>
              <p>Total Connections Made: <strong>{this.state.stats.totalConnections}</strong></p>
              <p>Total Nodes in the Database: <strong>{this.state.stats.totalDBNodes}</strong></p>
            </Paper>

            <div>
              <Paper className="statColItem" zDepth={2}>
                <h3>Chain Length Statistics</h3>
                <p>Average Connection Chain Length: <strong>{parseFloat(this.state.stats.averageChainLength).toFixed(3)}</strong></p>
                <p>Longest Connection Chain Length: <strong>{this.state.stats.longestChainLength}</strong></p>
                <p>Shortest Connection Chain Length: <strong>{this.state.stats.shortestChainLength}</strong></p>
                <p>Total Connection Chain Length: <strong>{this.state.stats.totalChainLength}</strong></p>
              </Paper>

              <Paper className="statColItem" zDepth={2}>
                <h3>Computation Time Statistics</h3>
                <p>Average Computation Time: <strong>{parseFloat(this.state.stats.averageComputationTime).toFixed(3)}</strong></p>
                <p>Longest Computation Time: <strong>{parseFloat(this.state.stats.longestComputationTime).toFixed(3)}</strong></p>
                <p>Shortest Computation Time: <strong>{parseFloat(this.state.stats.shortestComputationTime).toFixed(3)}</strong></p>
                <p>Total Computation Time: <strong>{parseFloat(this.state.stats.totalComputationTime).toFixed(3)}</strong></p>
              </Paper>
            </div>
          </div>

          <RaisedButton label="Refresh" onMouseUp={this.throttleRefresh} />
        </Paper>
        <RecentlySearchedView setActiveView={this.props.setActiveView} setActiveTheme={this.props.setActiveTheme} length={7} />
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
