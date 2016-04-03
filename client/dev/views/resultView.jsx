var React = require('react');
var d3 = require('d3');
import Paper from 'material-ui/lib/paper';

var ResultView = React.createClass({
  getInitialState: function() {
    return {
      graph: {
        nodes: {},
        links: []
      },
      currentHover: '',
      currentHoverStick: false,
      currentHoverNode: null
    };
  },

  //http://bl.ocks.org/mbostock/2706022
  renderGraph: function() {
    var nodes = {};
    var links = this.state.graph.links;
    var context = this;

    function mouseover() {
      d3.select(this).attr("cursor", "pointer");

      if (!context.state.currentHoverStick) {
        context.setState({currentHover: d3.select(this).select("text").text()});
        context.setState({currentHoverNode: this});
      }
    }

    function mouseout() {
      if (!context.state.currentHoverStick) {
        context.setState({currentHover: ''});
        context.setState({currentHoverNode: null});
      }
    }

    function click() {
      //If you clicked on another node while one is already clicked
      if (context.state.currentHoverNode != null && context.state.currentHoverNode != this) {
        //deflate currently hovered node
        d3.select(context.state.currentHoverNode).select("circle").transition()
          .duration(750)
          .attr("r", 6);

        //inflate the clicked one
        d3.select(this).select("circle").transition()
          .duration(750)
          .attr("r", 10);

        //And update the node info
        context.setState({currentHover: d3.select(this).select("text").text()});
        context.setState({currentHoverNode: this});
      } else if (context.state.currentHoverNode == this && !context.state.currentHoverStick) { //nothing clicked
        context.setState({currentHoverStick: true});

        d3.select(this).select("circle").transition()
          .duration(750)
          .attr("r", 10);
      } else if (context.state.currentHoverNode != null && context.state.currentHoverNode == this && context.state.currentHoverStick) { //toggle
        context.setState({currentHoverStick: false});

        d3.select(this).select("circle").transition()
          .duration(750)
          .attr("r", 6);
      }
    }

    // Compute the distinct nodes from the links.
    links.forEach(function(link) {
      link.source = nodes[link.source] || (nodes[link.source] = {name: link.source});
      link.target = nodes[link.target] || (nodes[link.target] = {name: link.target});
    });

    for (var i = 0; i < this.state.graph.nodeValues.length; i++) {
      nodes[i].name = this.state.graph.nodeValues[i];
    }

    var width = 960, height = 500;

    var force = d3.layout.force()
      .nodes(d3.values(nodes))
      .links(links)
      .size([width, height])
      .charge(-200)
      .on("tick", tick)
      .start();

    var svg = d3.select("#graph").append("svg")
      .attr("width", width)
      .attr("height", height);

    var link = svg.selectAll(".link")
      .data(force.links())
      .enter().append("line")
      .attr("class", "link");

    var node = svg.selectAll(".node")
      .data(force.nodes())
      .enter().append("g")
      .attr("class", "node")
      .on("mouseover", mouseover)
      .on("mouseout", mouseout)
      .on("click", click)
      .call(force.drag);

    node.append("circle")
      .attr("r", 6);

    node.append("text")
      .attr("x", 10)
      .attr("dy", ".35em")
      .text(function(d) { return d.name; });

    function tick() {
      link
        .attr("x1", function(d) { return d.source.x; })
        .attr("y1", function(d) { return d.source.y; })
        .attr("x2", function(d) { return d.target.x; })
        .attr("y2", function(d) { return d.target.y; });

      node
        .attr("transform", function(d) { return "translate(" + d.x + "," + d.y + ")"; });
    }

  },

  //This is where you apply activeViewState, if its defined.
  componentWillMount: function() {
    if (this.props.activeViewState != null) {
      this.setState(this.props.activeViewState);
    }
  },

  componentDidMount: function() {
    this.renderGraph();
  },

  render: function() {
    const graphStyle = {
      height: '75%',
      width: '75%',
      padding: 10,
      margin: 20
    }

    const infoStyle = {
      height: '75%',
      width: '25%',
      padding: 10,
      margin: 20
    }

    return (
      <div className="resultView">
        <Paper style={graphStyle} zDepth={1}>
          <div id="graph" className="resultView"></div>
        </Paper>
        <Paper style={infoStyle} zDepth={1}>
          <div>Node info: {this.state.currentHover}</div>
        </Paper>
      </div>
    );
  }
});

module.exports = ResultView;
