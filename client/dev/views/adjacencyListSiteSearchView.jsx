var React = require('react');
var $ = require('jquery');

var ResultView = require('./resultView');

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
            this.props.setActiveView(ResultView, {graph: newGraph});
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
      nodeValues: d3.values(this.state.apiResponse.nodeValues)
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

  handleSubmit: function(e) {
    //don't do default form submit action
    e.preventDefault();

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
      messageString = this.state.loadingMessage;
    }
    if (this.state.errorMessage) {
      messageString = this.state.errorMessage;
    }

    return (
      <div className="adjacencyListSiteSearchView">
        <div className="flexRowItem">
          <img src="http://placehold.it/300?text=A"></img>
          <img src="http://placehold.it/300?text=B"></img>
        </div>
        <form className="adjListForm flexRowItem" onSubmit={this.handleSubmit}>
          <input type="text" placeholder="Connect node A..." value={this.state.connectionBegin} onChange={this.handleConnectionBeginChange} />
          <input type="text" placeholder="...to node B" value={this.state.connectionEnd} onChange={this.handleConnectionEndChange} />
          <input type="submit" value="Submit" />
        </form>
        <p>{messageString}</p>
      </div>
    );
  }
});

module.exports = AdjacencyListSiteSearchView;
