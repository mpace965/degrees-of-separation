var React = require('react');

var Header = require('./header');
var LandingPage = require('./landingPage');

var Wrapper = React.createClass({
  getInitialState: function() {
    return {
      activeView: LandingPage
    };
  },

  setActiveView: function(activeView) {
    this.setState({activeView: activeView})
  },

  render: function() {
    var ActiveView = this.state.activeView;

    return (
      <div>
        <Header setActiveView={this.setActiveView} />
        <ActiveView />
      </div>
    );
  }
});

ReactDOM.render(
  <Wrapper />,
  document.getElementById('wrapper')
)
