var React = require('react');

var Header = require('./header');
var LandingPage = require('./landingPage');

var Wrapper = React.createClass({
  getInitialState: function() {
    return {
      activeView: LandingPage,
      activeViewState: {}
    };
  },

  setActiveView: function(activeView, state) {
    this.setState({activeView: activeView});

    if (state != null) {
      this.setState({activeViewState: state});
    }
  },

  render: function() {
    var ActiveView = this.state.activeView;

    return (
      <div>
        <Header setActiveView={this.setActiveView} />
        <ActiveView setActiveView={this.setActiveView} activeViewState={this.state.activeViewState}/>
      </div>
    );
  }
});

ReactDOM.render(
  <Wrapper />,
  document.getElementById('wrapper')
)
