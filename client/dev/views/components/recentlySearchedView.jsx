var React = require('react');
var $ = require('jquery');
import Paper from 'material-ui/lib/paper';

var RecentlySearchedView = React.createClass({
  getInitialState: function() {
    return {
      timeout: {},
      recentChain: [],
      startIndex: 0
    };
  },

  getDefaultProps: function() {
    return {
      length: 5
    };
  },

  getRecentsFromServer: function() {
    $.ajax({
        url: '/api/recentConnections',
        dataType: 'json',
        cache: false,
        success: function(data) {
          this.setState({recentChain: data});

          if (this.state.recentChain.length > this.props.length) {
            this.incrementStart();
            this.setState({timeout: setInterval(this.incrementStart, 4000)});
          }
        }.bind(this),
        error: function(xhr, status, err) {
          console.error('/api/recentConnections', status, err.toString());
        }.bind(this)
    });
  },

  componentDidMount: function() {
    this.getRecentsFromServer();
  },

  componentWillUnmount: function() {
    clearInterval(this.state.timeout);
  },

  incrementStart: function() {
    if (this.state.startIndex < this.state.recentChain.length - 1) {
      this.setState({startIndex: this.state.startIndex + 1});
    } else {
      this.setState({startIndex: 0});
    }
  },

  render: function() {
    const style = {
      height: '75%',
      width: '25%',
      fontSize: '16px',
      padding: 5,
      margin: 20,
    };

    var recents = [];
    var end = 0;

    if (this.state.recentChain.length < this.props.length) {
      end = this.state.recentChain.length;
    } else {
      end = this.props.length;
    }

    //Appends the first n elements to the end of the array, to make it easy to get the elements before the index resets to 0.
    var wrapArray = this.state.recentChain.concat(this.state.recentChain.slice(0, this.props.length - 1));

    for (var i = 0; i < end; i++) {
      var responseObject = wrapArray[this.state.startIndex + i];

      recents.push(
        <div key={i}>
          {responseObject.site}: {responseObject.begin} connected to {responseObject.end}
        </div>
      );
    }

    return (
      <Paper style={style} zDepth={1}>
        <p><strong>Recently Connected:</strong></p>
        <div>{recents}</div>
      </Paper>
    );
  }
});

module.exports = RecentlySearchedView;
