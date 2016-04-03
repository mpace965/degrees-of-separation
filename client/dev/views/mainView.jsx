var React = require('react');
var ReactDOM = require('react-dom');
import injectTapEventPlugin from 'react-tap-event-plugin';

var Header = require('./header');
var LandingPage = require('./landingPage');

injectTapEventPlugin();

var Wrapper = React.createClass({
  getInitialState: function() {
    return {
      activeView: LandingPage,
      activeViewState: {}
    };
  },

  /* Handles navigation from all child views.
  Optionally, you can pass in a state that will get applied to the activeView.
  This is not automatic, you need to apply it in the view's componentWillMount.
  See resultView.jsx for an example */
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
