var React = require('react');
var ReactDOM = require('react-dom');
import injectTapEventPlugin from 'react-tap-event-plugin';
import getMuiTheme from 'material-ui/lib/styles/getMuiTheme';
import DefaultTheme from 'material-ui/lib/styles/baseThemes/lightBaseTheme'

var Header = require('./header');
var LandingPage = require('./landingPage');

injectTapEventPlugin();

var Wrapper = React.createClass({
  getInitialState: function() {
    return {
      activeView: LandingPage,
      activeViewState: {},
      activeTheme: DefaultTheme
    };
  },

  childContextTypes: {
   muiTheme: React.PropTypes.object,
  },

  getChildContext: function() {
    return {
      muiTheme: getMuiTheme(this.state.activeTheme)
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

  setActiveTheme: function(activeTheme) {
    this.setState({activeTheme: activeTheme});
  },

  render: function() {
    var ActiveView = this.state.activeView;

    return (
      <div>
        <Header setActiveView={this.setActiveView} setActiveTheme={this.setActiveTheme} />
        <ActiveView setActiveView={this.setActiveView} activeViewState={this.state.activeViewState}/>
      </div>
    );
  }
});

ReactDOM.render(
  <Wrapper />,
  document.getElementById('wrapper')
)
