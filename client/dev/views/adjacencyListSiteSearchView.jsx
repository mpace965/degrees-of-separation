var React = require('react');

var ResultView = require('./resultView');

var AdjacencyListSiteSearchView = React.createClass({
  getInitialState: function() {
    return {
      connectionBegin: '',
      connectionEnd: ''
    };
  },

  handleConnectionBeginChange: function(e) {
    this.setState({connectionBegin: e.target.value});
  },

  handleConnectionEndChange: function(e) {
    this.setState({connectionEnd: e.target.value});
  },

  handleSubmit: function(e) {
    e.preventDefault();
    var connectionBegin = this.state.connectionBegin.trim();
    var connectionEnd = this.state.connectionEnd.trim();

    if (!connectionBegin || !connectionEnd) {
      //add error message
      return;
    }

    //add handler to send to server
    this.props.setActiveView(ResultView, {graph: {
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
      }});
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
