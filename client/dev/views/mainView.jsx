var React = require('react');

var Header = require('./header');
var A = require('./a');

var Wrapper = React.createClass({
  getInitialState: function() {
    return {
      activeView: A
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
