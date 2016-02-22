var React = require('react');

var A = require('./a');
var B = require('./b');

var Header = React.createClass({
  handleClick: function(param) {
    this.props.setActiveView(param);
  },

  render: function() {
    return (
      <div className="header">
        <button onClick={this.handleClick.bind(null, A)} >Set A as active content</button>
        <button onClick={this.handleClick.bind(null, B)} >Set B as active content</button>
      </div>
    );
  }
});

module.exports = Header;
