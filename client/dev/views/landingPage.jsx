var React = require('react');

var LandingPage = React.createClass({
  render: function() {
    return (
      <div className="landingPage">
        Welcome to the Degrees of Separation website! Click connect to see how two objects of your choice are related.
      </div>
    );
  }
});

module.exports = LandingPage;
