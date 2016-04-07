var React = require('react');
var d3 = require('d3');
var Colors = require('material-ui/lib/styles/colors');
var ResultView = require('../resultView');
import Paper from 'material-ui/lib/paper';
import RaisedButton from 'material-ui/lib/raised-button';

var AdjacencyListResultView = React.createClass({
  getInitialState: function() {
    return {
      currentHover: '',
      currentHoverStick: false,
      currentHoverNode: null
    };
  },

  mouseover: function(node, currentHoverName) {
    if (!this.state.currentHoverStick) {
      this.setState({currentHover: currentHoverName});
      this.setState({currentHoverNode: node});
    }
  },

  mouseout: function() {
    if (!this.state.currentHoverStick) {
      this.setState({currentHover: ''});
      this.setState({currentHoverNode: null});
    }
  },

  click: function(node, currentHoverName) {
    //If you clicked on another node while one is already clicked
    if (this.state.currentHoverNode != null && this.state.currentHoverNode != node) {
      //deflate currently hovered node
      d3.select(this.state.currentHoverNode).select("circle").transition()
        .duration(750)
        .attr("r", 15);

      //inflate the clicked one
      d3.select(node).select("circle").transition()
        .duration(750)
        .attr("r", 19);

      //And update the node info
      this.setState({currentHover: currentHoverName});
      this.setState({currentHoverNode: node});
    } else if (this.state.currentHoverNode == node && !this.state.currentHoverStick) { //nothing clicked
      this.setState({currentHoverStick: true});

      d3.select(node).select("circle").transition()
        .duration(750)
        .attr("r", 19);
    } else if (this.state.currentHoverNode != null && this.state.currentHoverNode == node && this.state.currentHoverStick) { //toggle
      this.setState({currentHoverStick: false});

      d3.select(node).select("circle").transition()
        .duration(750)
        .attr("r", 15);
    }
  },

  render: function() {
    const infoStyle = {
      height: '75%',
      width: '75%',
      padding: 10,
      margin: 20
    }

    return (
      <div className="resultView">
        <ResultView
          mouseover={this.mouseover}
          mouseout={this.mouseout}
          click={this.click}
          color={Colors.cyan500}
          activeViewState={this.props.activeViewState}
          nodeNames={this.props.activeViewState.graph.nodeValues} />
        <Paper style={infoStyle} zDepth={1}>
          <div>Node info: {this.state.currentHover}</div>
        </Paper>
      </div>
    );
  }
});

module.exports = AdjacencyListResultView;
