var React = require('react');

var ResultView = require('./resultView');

var AdjacencyListSiteSearchView = React.createClass({
  getInitialState: function() {
    return {
      connectionBegin: '',
      connectionEnd: '',
      apiResponse: {}
    };
  },

  //Make the api request to the server
  loadChainFromServer: function() {
    $.ajax({
        url: '/api/connect',
        dataType: 'json',
        cache: false,
        data: {begin: this.state.connectionBegin, end: this.state.connectionEnd},
        success: function(data) {
          this.setState({apiResponse: data}, function() {
            var newGraph = this.processApiResponse();
            this.props.setActiveView(ResultView, {graph: newGraph});
          });
        }.bind(this),
        error: function(xhr, status, err) {
          console.error('/api/connect', status, err.toString());
        }.bind(this)
    });
  },

  processApiResponse: function() {
    var graph = {
      nodes: d3.range(this.state.apiResponse.nodeCount).map(Object),
      links: this.state.apiResponse.edgeList
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
      </div>
    );
  }
});

module.exports = AdjacencyListSiteSearchView;
