var React = require('react');
var d3 = require('d3');
var Colors = require('material-ui/lib/styles/colors');
var ResultView = require('../resultView');
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
      currentHoverName: '',
      currentHoverImageUrl: '',
      currentHoverListeners: '',
      currentHoverPlaycount: '',
      currentHoverTags: [],
      currentHoverBio: '',
      currentHoverStick: false,
      currentHoverNode: null
    };
  },

  mouseover: function(node, currentNodeInfo) {
    if (!this.state.currentHoverStick) {
      this.setState({currentHoverName: currentNodeInfo.name,
                        currentHoverImageUrl: currentNodeInfo.image,
                        currentHoverListeners: currentNodeInfo.listeners,
                        currentHoverPlaycount: currentNodeInfo.playcount,
                        currentHoverTags: currentNodeInfo.tags,
                        currentHoverBio: currentNodeInfo.bio});
      this.setState({currentHoverNode: node});
    }
  },

  mouseout: function() {
    if (!this.state.currentHoverStick) {
      this.setState({currentHoverName: '',
                        currentHoverImageUrl: '',
                        currentHoverListeners: '',
                        currentHoverPlaycount: '',
                        currentHoverTags: [],
                        currentHoverBio: ''});
      this.setState({currentHoverNode: null});
    }
  },

  click: function(node, currentNodeInfo) {
    //If you clicked on another node while one is already clicked
    if (this.state.currentHoverNode != null && this.state.currentHoverNode != null) {
      //deflate currently hovered node
      d3.select(this.state.currentHoverNode).select("circle").transition()
        .duration(750)
        .attr("r", 15);

      //inflate the clicked one
      d3.select(node).select("circle").transition()
        .duration(750)
        .attr("r", 19);

      //And update the node info
      this.setState({currentHoverName: currentNodeInfo.name,
                        currentHoverImageUrl: currentNodeInfo.image,
                        currentHoverListeners: currentNodeInfo.listeners,
                        currentHoverPlaycount: currentNodeInfo.playcount,
                        currentHoverTags: currentNodeInfo.tags,
                        currentHoverBio: currentNodeInfo.bio});
      this.setState({currentHoverNode: node});
    } else if (this.state.currentHoverNode == this && !this.state.currentHoverStick) { //nothing clicked
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

  createBioMarkup: function() {
    return {
      __html: this.state.currentHoverBio
    };
  },

  getNodeNames: function() {
    return this.props.activeViewState.graph.nodeValues.map(function(lastfmArtist) {
      return lastfmArtist.name;
    });
  },

  render: function() {
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
        <ResultView
          mouseover={this.mouseover}
          mouseout={this.mouseout}
          click={this.click}
          color={Colors.red500}
          activeViewState={this.props.activeViewState}
          nodeNames={this.getNodeNames()} />
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
