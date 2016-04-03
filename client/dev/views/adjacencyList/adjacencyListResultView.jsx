var React = require('react');
var d3 = require('d3');
import Paper from 'material-ui/lib/paper';
import RaisedButton from 'material-ui/lib/raised-button';

var AdjacencyListResultView = React.createClass({
  getInitialState: function() {
    return {
      graph: {
        nodes: {},
        links: []
      }
    };
  },

  //http://bl.ocks.org/mbostock/2706022
  renderGraph: function() {
    var nodes = {};
    var links = this.state.graph.links;

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
      .charge(-1000)
      .on("tick", tick)
      .start();

    var svg = d3.select("#graph").append("svg")
      .attr("width", width)
      .attr("height", height);

    var link = svg.selectAll(".link")
      .data(force.links())
      .enter().append("line")
      .attr("stroke", "#000")
      .attr("stroke-width", "2px");

    var node = svg.selectAll(".node")
      .data(force.nodes())
      .enter().append("g")
      .attr("fill", "#ccc")
      .attr("stroke", "#000")
      .call(force.drag);

    node.append("circle")
      .attr("r", 15);

    node.append("text")
      .attr("x", 20)
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

  saveSvg: function() {
    var link = document.createElement('a');
    link.download = "connections.svg";
    link.href = 'data:application/octet-stream;base64,' + btoa(d3.select('#graph').html());
    link.click();
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
    const style = {
      height: '75%',
      width: '75%',
      padding: 10,
      margin: 20
    }

    return (
      <div className="resultView">
        <Paper style={style} zDepth={1}>
          <div id="graph" className="resultView"></div>
          <RaisedButton label="Save" onMouseUp={this.saveSvg} />
        </Paper>
        <div id="svgdataurl"></div>
      </div>
    );
  }
});

module.exports = AdjacencyListResultView;
