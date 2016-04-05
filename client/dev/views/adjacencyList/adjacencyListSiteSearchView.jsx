var React = require('react');
var $ = require('jquery');
var d3 = require('d3');
import Paper from 'material-ui/lib/paper';
import CircularProgress from 'material-ui/lib/circular-progress';
import TextField from 'material-ui/lib/text-field';
import RaisedButton from 'material-ui/lib/raised-button';

var AdjacencyListResultView = require('./adjacencyListResultView');

var AdjacencyListSiteSearchView = React.createClass({
  getInitialState: function() {
    return {
      connectionBegin: '',
      connectionEnd: '',
      apiResponse: {},
      loadingMessage: '',
      errorMessage: ''
    };
  },

  //Make the api request to the server
  loadChainFromServer: function() {
    this.setState({errorMessage: ''});

    $.ajax({
        url: '/api/connectAdjacency',
        dataType: 'json',
        cache: false,
        data: {begin: this.state.connectionBegin, end: this.state.connectionEnd},
        success: function(data) {
          this.setState({apiResponse: data}, function() {
            this.setState({loadingMessage: ''});
            var newGraph = this.processApiResponse();
            this.props.setActiveView(AdjacencyListResultView, {graph: newGraph});
          });
        }.bind(this),
        error: function(xhr, status, err) {
          this.setState({loadingMessage: ''});
          this.setState({errorMessage: xhr.responseText});
          console.error('/api/connectAdjacency', status, err.toString());
        }.bind(this)
    });

    this.setState({loadingMessage: 'Loading...'});
  },

  processApiResponse: function() {
    var graph = {
      nodes: d3.range(this.state.apiResponse.nodeCount).map(Object),
      links: this.state.apiResponse.edgeList,
      nodeValues: this.state.apiResponse.nodeValues
    };

    return graph;
  },

  //Handle input changes by updating the state.
  handleConnectionBeginChange: function(e) {
    this.setState({connectionBegin: e.target.value});
  },

  handleConnectionEndChange: function(e) {
    this.setState({connectionEnd: e.target.value});
  },

  handleKeyDown: function(e) {
    //Submit on newline
    if (e.keyCode == 13) {
      this.handleSubmit();
    }
  },

  handleSubmit: function() {
    //basic string sanitation
    var connectionBegin = this.state.connectionBegin.trim();
    var connectionEnd = this.state.connectionEnd.trim();

    if (!connectionBegin || !connectionEnd) {
      //add error message
      return;
    }

    this.loadChainFromServer();
    this.setState({connectionBegin: '', connectionEnd: ''});
  },

  render: function() {
    var messageString = '';

    if (this.state.loadingMessage) {
      messageString = <CircularProgress />;
    }
    if (this.state.errorMessage) {
      messageString = this.state.errorMessage;
    }

    const style = {
      height: '75%',
      width: '75%',
      padding: 10,
      margin: 20
    }

    return (
      <div className="siteSearchView">
        <Paper style={style} zDepth={1}>
          <div className="flexRowItem">
            <img src="http://placehold.it/300?text=A"></img>
            <img src="http://placehold.it/300?text=B"></img>
          </div>
          <div className="flexRowItem">
            <TextField
              hintText="Connect node A..."
              value={this.state.connectionBegin}
              onChange={this.handleConnectionBeginChange}
              onKeyDown={this.handleKeyDown} />
            <TextField
              hintText="...to node B"
              value={this.state.connectionEnd}
              onChange={this.handleConnectionEndChange}
              onKeyDown={this.handleKeyDown} />
            <RaisedButton label="Submit" onMouseUp={this.handleSubmit} />
          </div>
          <div className="flexRowItem">{messageString}</div>
        </Paper>
      </div>
    );
  }
});

module.exports = AdjacencyListSiteSearchView;
