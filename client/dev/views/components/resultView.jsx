var React = require('react');
var d3 = require('d3');
var Colors = require('material-ui/lib/styles/colors');
import Paper from 'material-ui/lib/paper';
import RaisedButton from 'material-ui/lib/raised-button';

var ResultView = React.createClass({
  getInitialState: function() {
    return {
      graph: {
        nodes: {},
        links: [],
        nodeValues: []
      }
    };
  },

  //http://bl.ocks.org/mbostock/2706022
  renderGraph: function() {
    var nodes = {};
    var links = this.state.graph.links;
    var context = this;

    function mouseover() {
      d3.select(this).attr("cursor", "pointer");
      var index = parseInt(d3.select(this).select("index").text());

      context.props.mouseover(this, context.state.graph.nodeValues[index]);
    }

    function mouseout() {
      context.props.mouseout();
    }

    function click() {
      var index = parseInt(d3.select(this).select("index").text());

      context.props.click(this, context.state.graph.nodeValues[index]);
    }

    // Compute the distinct nodes from the links.
    links.forEach(function(link) {
      link.source = nodes[link.source] || (nodes[link.source] = {name: link.source});
      link.target = nodes[link.target] || (nodes[link.target] = {name: link.target});
    });

    for (var i = 0; i < this.props.nodeNames.length; i++) {
      nodes[i].name = this.props.nodeNames[i];
      nodes[i].index = i;
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
      .attr("fill", this.props.color)
      .attr("stroke", "#000")
      .on("mouseover", mouseover)
      .on("mouseout", mouseout)
      .on("click", click)
      .call(force.drag);

    node.append("circle")
      .attr("r", 15);

    node.append("text")
      .attr("x", 20)
      .attr("dy", ".35em")
      .attr("fill", "#000")
      .text(function(d) { return d.name; });

    node.append("index")
      .text(function(d) { return d.index; });

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

  createBioMarkup: function() {
    return {
      __html: this.state.currentHoverBio
    };
  },

  render: function() {
    const style = {
      height: '75%',
      width: '75%',
      padding: 10,
      margin: 20
    }

    return (
      <Paper style={style} zDepth={1}>
        <div id="graph" className="resultView"></div>
        <RaisedButton label="Save" onMouseUp={this.saveSvg} />
      </Paper>
    );
  }
});

module.exports = ResultView;
