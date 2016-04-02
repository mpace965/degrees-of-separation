var React = require('react');
var d3 = require('d3');

var ResultView = React.createClass({
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
      .charge(-200)
      .on("tick", tick)
      .start();

    var svg = d3.select(".resultView").append("svg")
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
      .call(force.drag);

    node.append("circle")
      .attr("r", 4.5);

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
    return (
      <div className="resultView">
      </div>
    );
  }
});

module.exports = ResultView;
