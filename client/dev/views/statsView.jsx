var React = require('react');
var $ = require('jquery');
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

    
    
    return (
      <div className="statsView">
        <Paper style={style} zDepth={1}>
          <div>
            <center><h2>Statistics</h2></center>
          </div>
          <div>
            <p>{this.state.stats[0]}</p>
          </div>
        </Paper>
   

        
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
