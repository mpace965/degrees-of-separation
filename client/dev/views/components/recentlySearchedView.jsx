var React = require('react');
var $ = require('jquery');
import Paper from 'material-ui/lib/paper';

var RecentlySearchedView = React.createClass({
  getInitialState: function() {
    return {
      timeout: {},
      recentChain: [],
      recentConnection: ''
    };
  },

  getRecentsFromServer: function() {
    $.ajax({
        url: '/api/recentConnections',
        dataType: 'json',
        cache: false,
        success: function(data) {
          this.setState({recentChain: data});
          this.setRandomRecent();
        }.bind(this),
        error: function(xhr, status, err) {
          console.error('/api/recentConnections', status, err.toString());
        }.bind(this)
    });
  },

  componentDidMount: function() {
    this.getRecentsFromServer();
    this.setState({timeout: setInterval(this.setRandomRecent, 4000)});
  },

  componentWillUnmount: function() {
    clearInterval(this.state.timeout);
  },

  setRandomRecent: function() {
    var randomIndex = Math.floor(Math.random() * this.state.recentChain.length);
    this.setState({recentConnection: this.state.recentChain[randomIndex]});
  },

  render: function() {
    const style = {
      height: '75%',
      width: '25%',
      fontSize: '16px',
      padding: 5,
      margin: 20,
    };

    return (
      <Paper style={style} zDepth={1}>
        <p><strong>Recently Connected:</strong></p>
        <p>{this.state.recentConnection}</p>
      </Paper>
    );
  }
});

module.exports = RecentlySearchedView;
