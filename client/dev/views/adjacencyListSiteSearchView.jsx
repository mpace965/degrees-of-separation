var React = require('react');

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
      return;
    }

    //add handler to send to server
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
