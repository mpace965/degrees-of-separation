var React = require('react');
var d3 = require('d3');
var Colors = require('material-ui/lib/styles/colors');
import Paper from 'material-ui/lib/paper';
import RaisedButton from 'material-ui/lib/raised-button';
import Avatar from 'material-ui/lib/avatar';
import Card from 'material-ui/lib/card/card';
import CardHeader from 'material-ui/lib/card/card-header';
import CardText from 'material-ui/lib/card/card-text';

var Tag = React.createClass({
  render: function() {
    return (
      <span><a href={this.props.url}>{this.props.name}</a> </span>
    );
  }
});

var TagList = React.createClass({
  render: function() {
    if (this.props.tags == null) {
      return (<div><strong>Tags: </strong> </div>);
    }

    var tags = this.props.tags.map(function(t){
      return (
        <Tag key={t.name} url={t.url} name={t.name} />
      );
    });

    return (
      <div>
        <strong>Tags: </strong> {tags}
      </div>
    );
  }
});

var LastfmResultView = React.createClass({
  getInitialState: function() {
    return {
      graph: {
        nodes: {},
        links: [],
        currentHoverName: '',
        currentHoverImageUrl: '',
        currentHoverListeners: '',
        currentHoverPlaycount: '',
        currentHoverTags: [],
        currentHoverBio: '',
        currentHoverStick: false,
        currentHoverNode: null
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

      if (!context.state.currentHoverStick) {
        var index = parseInt(d3.select(this).select("index").text());
        var currentNodeInfo = context.state.graph.nodeValues[index];

        context.setState({currentHoverName: currentNodeInfo.name,
                          currentHoverImageUrl: currentNodeInfo.image,
                          currentHoverListeners: currentNodeInfo.listeners,
                          currentHoverPlaycount: currentNodeInfo.playcount,
                          currentHoverTags: currentNodeInfo.tags,
                          currentHoverBio: currentNodeInfo.bio});
        context.setState({currentHoverNode: this});
      }
    }

    function mouseout() {
      if (!context.state.currentHoverStick) {
        var index = parseInt(d3.select(this).select("index").text());
        var currentNodeInfo = context.state.graph.nodeValues[index];

        context.setState({currentHoverName: '',
                          currentHoverImageUrl: '',
                          currentHoverListeners: '',
                          currentHoverPlaycount: '',
                          currentHoverTags: [],
                          currentHoverBio: ''});
        context.setState({currentHoverNode: null});
      }
    }

    function click() {
      var index = parseInt(d3.select(this).select("index").text());
      var currentNodeInfo = context.state.graph.nodeValues[index];

      //If you clicked on another node while one is already clicked
      if (context.state.currentHoverNode != null && context.state.currentHoverNode != this) {
        //deflate currently hovered node
        d3.select(context.state.currentHoverNode).select("circle").transition()
          .duration(750)
          .attr("r", 15);

        //inflate the clicked one
        d3.select(this).select("circle").transition()
          .duration(750)
          .attr("r", 19);

        //And update the node info
        context.setState({currentHoverName: currentNodeInfo.name,
                          currentHoverImageUrl: currentNodeInfo.image,
                          currentHoverListeners: currentNodeInfo.listeners,
                          currentHoverPlaycount: currentNodeInfo.playcount,
                          currentHoverTags: currentNodeInfo.tags,
                          currentHoverBio: currentNodeInfo.bio});
        context.setState({currentHoverNode: this});
      } else if (context.state.currentHoverNode == this && !context.state.currentHoverStick) { //nothing clicked
        context.setState({currentHoverStick: true});

        d3.select(this).select("circle").transition()
          .duration(750)
          .attr("r", 19);
      } else if (context.state.currentHoverNode != null && context.state.currentHoverNode == this && context.state.currentHoverStick) { //toggle
        context.setState({currentHoverStick: false});

        d3.select(this).select("circle").transition()
          .duration(750)
          .attr("r", 15);
      }
    }

    // Compute the distinct nodes from the links.
    links.forEach(function(link) {
      link.source = nodes[link.source] || (nodes[link.source] = {name: link.source});
      link.target = nodes[link.target] || (nodes[link.target] = {name: link.target});
    });

    for (var i = 0; i < this.state.graph.nodeValues.length; i++) {
      nodes[i].name = this.state.graph.nodeValues[i].name;
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
      .attr("fill", Colors.red500)
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

    const infoStyle = {
     height: '75%',
     width: '25%',
     padding: 10,
     margin: 20
   }

   var currentAvatar = <Avatar src={this.state.currentHoverImageUrl}
                               size={60} />;

    return (
      <div className="resultView">
        <Paper style={style} zDepth={1}>
          <div id="graph" className="resultView"></div>
          <RaisedButton label="Save" onMouseUp={this.saveSvg} />
        </Paper>
        <Card style={infoStyle}>
          <CardHeader title={this.state.currentHoverName} avatar={currentAvatar} />
          <CardText>
            <p><strong>Listeners:</strong> {this.state.currentHoverListeners}</p>
            <p><strong>Playcount:</strong> {this.state.currentHoverPlaycount}</p>
            <TagList tags={this.state.currentHoverTags} />
            <p><strong>Bio:</strong> <span dangerouslySetInnerHTML={this.createBioMarkup()}/></p>
          </CardText>
        </Card>
      </div>
    );
  }
});

module.exports = LastfmResultView;
