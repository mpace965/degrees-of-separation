var React = require('react');

var ResultView = React.createClass({
  getInitialState: function() {
    return {
      graph: {
        nodes: {},
        links: []
      }
    };
  },

  renderGraph: function() {
    //http://bl.ocks.org/mbostock/3311124
    var width = 960,
        height = 500;

    var svg = d3.select(".resultView").append("svg")
        .attr("class", "graph")
        .attr("width", width)
        .attr("height", height);

    var force = d3.layout.force()
        .nodes(this.state.graph.nodes)
        .links(this.state.graph.links)
        .size([width, height])
        .charge(-200)
        .on("tick", tick)
        .start();

    var link = svg.selectAll(".link")
       .data(this.state.graph.links)
     .enter().append("line")
       .attr("class", "link");

    var node = svg.selectAll(".node")
       .data(this.state.graph.nodes)
     .enter().append("circle")
       .attr("class", "node")
       .attr("r", 4.5);

    function tick() {
      link.attr("x1", function(d) { return d.source.x; })
          .attr("y1", function(d) { return d.source.y; })
          .attr("x2", function(d) { return d.target.x; })
          .attr("y2", function(d) { return d.target.y; });

      node.attr("cx", function(d) { return d.x; })
          .attr("cy", function(d) { return d.y; });
    }
  },

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
