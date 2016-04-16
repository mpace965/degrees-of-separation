var React = require('react');
var $ = require('jquery');
import CircularProgress from 'material-ui/lib/circular-progress';

var ConnectLink = React.createClass({
  getInitialState: function() {
    return {
      apiResponse: {}
    };
  },

  //Make the api request to the server
  loadChainFromServer: function() {
    $.ajax({
        url: this.props.url,
        dataType: 'json',
        cache: false,
        timeout: 300000,
        data: {begin: this.props.begin, end: this.props.end},
        success: function(data) {
          this.setState({apiResponse: data}, function() {
            this.props.setLoading(false);
            var newGraph = this.processApiResponse();
            this.props.setActiveView(this.props.linkView, {graph: newGraph});
          });
        }.bind(this),
        error: function(xhr, status, err) {
          this.props.setLoading(false);
          console.error(this.props.url, status, err.toString());
        }.bind(this)
    });

    this.props.setLoading(true);
  },

  processApiResponse: function() {
    var graph = {
      nodes: d3.range(this.state.apiResponse.nodeCount).map(Object),
      links: this.state.apiResponse.edgeList,
      nodeValues: this.state.apiResponse.nodeValues
    };

    return graph;
  },

  render: function() {
    var flexStyle = {
      'display': 'flex',
      'flexDirection': 'row'
    };

    var linkStyle = {
      'color': 'blue',
      'textDecoration': 'underline',
      'cursor': 'pointer'
    };

    return (
      <div style={flexStyle}>
        <div style={linkStyle} onClick={this.loadChainFromServer}>{this.props.children}</div>
      </div>
    );
  }
});

module.exports = ConnectLink;
