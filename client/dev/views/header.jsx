var React = require('react');

var LandingPage = require('./landingPage');
var ResultView = require('./resultView');

var Header = React.createClass({
  handleClick: function(param) {
    this.props.setActiveView(param);
  },

  render: function() {
    return (
      <div className="header">
        <h2 className="text-button" onClick={this.handleClick.bind(null, LandingPage)}>Degrees of Separation</h2>
        <h3 className="text-button">About</h3>
        <h3 className="text-button" onClick={this.handleClick.bind(null, ResultView)}>Connect</h3>
      </div>
    );
  }
});

module.exports = Header;
