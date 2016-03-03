var React = require('react');

var LandingPage = require('./landingPage');
var AdjacencyListSiteSearchView = require('./adjacencyListSiteSearchView');

var Header = React.createClass({
  handleClick: function(param) {
    this.props.setActiveView(param);
  },

  //Simple navigation. Null must be bound as the first parameter, something weird with React.
  render: function() {
    return (
      <div className="header">
        <h2 className="text-button" onClick={this.handleClick.bind(null, LandingPage)}>Degrees of Separation</h2>
        <h3 className="text-button">About</h3>
        <h3 className="text-button" onClick={this.handleClick.bind(null, AdjacencyListSiteSearchView)}>Connect</h3>
      </div>
    );
  }
});

module.exports = Header;
