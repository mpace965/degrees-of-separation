var React = require('react');

function renderGraph() {
  //http://bl.ocks.org/mbostock/3311124
  var graph = {
  nodes: d3.range(13).map(Object),
  links: [
    {source:  0, target:  1},
    {source:  1, target:  2},
    {source:  2, target:  0},
    {source:  1, target:  3},
    {source:  3, target:  2},
    {source:  3, target:  4},
    {source:  4, target:  5},
    {source:  5, target:  6},
    {source:  5, target:  7},
    {source:  6, target:  7},
    {source:  6, target:  8},
    {source:  7, target:  8},
    {source:  9, target:  4},
    {source:  9, target: 11},
    {source:  9, target: 10},
    {source: 10, target: 11},
    {source: 11, target: 12},
    {source: 12, target: 10}
  ]
};

var width = 960,
    height = 500;

var svg = d3.select(".resultView").append("svg")
    .attr("class", "graph")
    .attr("width", width)
    .attr("height", height);

var force = d3.layout.force()
    .nodes(graph.nodes)
    .links(graph.links)
    .size([width, height])
    .charge(-200)
    .on("tick", tick)
    .start();

var link = svg.selectAll(".link")
   .data(graph.links)
 .enter().append("line")
   .attr("class", "link");

var node = svg.selectAll(".node")
   .data(graph.nodes)
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
}

var ResultView = React.createClass({
  getInitialState: function() {
    return {
      nodes: []
    }
  },

  componentDidMount: function() {
    renderGraph();
  },

  render: function() {
    return (
      <div className="resultView">
      </div>
    );
  }
});

module.exports = ResultView;
