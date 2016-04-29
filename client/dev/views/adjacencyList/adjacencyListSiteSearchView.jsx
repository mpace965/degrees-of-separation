var React = require('react');
var $ = require('jquery');
var d3 = require('d3');
import Paper from 'material-ui/lib/paper';
import CircularProgress from 'material-ui/lib/circular-progress';
import TextField from 'material-ui/lib/text-field';
import RaisedButton from 'material-ui/lib/raised-button';
import Snackbar from 'material-ui/lib/snackbar';

var AdjacencyListResultView = require('./adjacencyListResultView');

var AdjacencyListSiteSearchView = React.createClass({
  getInitialState: function() {
    return {
      connectionBegin: '',
      connectionEnd: '',
      apiRequest: {},
      apiResponse: {},
      apiLoading: false,
      snackbarOpen: false,
      snackbarMessage: ''
    };
  },

  //Make the api request to the server
  loadChainFromServer: function() {
    var request = $.ajax({
        url: '/api/connectAdjacency',
        dataType: 'json',
        cache: false,
        timeout: 300000,
        data: {begin: this.state.connectionBegin, end: this.state.connectionEnd},
        success: function(data) {
          this.setState({apiResponse: data}, function() {
            this.setState({apiLoading: false});
            var newGraph = this.processApiResponse();
            this.props.setActiveView(AdjacencyListResultView, {graph: newGraph});
          });
        }.bind(this),
        error: function(xhr, status, err) {
          this.setState({apiLoading: false});
          this.setState({snackbarOpen: true, snackbarMessage: xhr.responseText});
          console.error('/api/connectAdjacency', status, err.toString());
        }.bind(this)
    });

    this.setState({apiLoading: true, apiRequest: request});
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

  handleCancel: function() {
    this.state.apiRequest.abort();
    this.replaceState(this.getInitialState());
  },

  handleRequestClose: function() {
    this.setState({snackbarOpen: false, snackbarMessage: ''});
  },

  render: function() {
    var circularProgress;
    var cancelButton;

    if (this.state.apiLoading) {
      circularProgress = <CircularProgress />;
      cancelButton = <RaisedButton label="Cancel" onMouseUp={this.handleCancel} />;
    }

    const style = {
      height: '75%',
      width: '75%',
      padding: 30,
      margin: 20
    }

    return (
      <div className="siteSearchView">
        <Paper className="siteSearchView" style={style} zDepth={1}>
          <img src="res/snap-logo.png"/>

          <br/>
          <br/>

          <div className="flexRowItem">
            <TextField
              hintText="Connect one person..."
              value={this.state.connectionBegin}
              onChange={this.handleConnectionBeginChange}
              onKeyDown={this.handleKeyDown} />
            <TextField
              hintText="...to another"
              value={this.state.connectionEnd}
              onChange={this.handleConnectionEndChange}
              onKeyDown={this.handleKeyDown} />
            <RaisedButton label="Submit" onMouseUp={this.handleSubmit} />
          </div>
          <div className="flexRowItem">{circularProgress}</div>
          <div className="flexRowItem">{cancelButton}</div>

          <br/>

          <p>
            SNAP is the <a href="https://snap.stanford.edu/index.html">Stanford Network Analysis Project</a>.
            SNAP has made available a dataset which is composed of anonymized social circles on Facebook.
            Each number in the dataset represents a person, and every edge in the graph represents a friendship.
            Input a number in the range [0, 4039] in each of the input boxes above to see how they are connected.
            The resulting chain represents how one person knows another in the form of "friend of a friend" statements.
            You can read more about the dataset used <a href="https://snap.stanford.edu/data/egonets-Facebook.html">here</a>.
          </p>

          <br/>

            <form method="post" enctype="multipart/form-data">
        choose a file
        <input name="file" type="file" size="50" maxlength="100000" />
        <button type="submit">upload</button>
    </form>

        </Paper>
        <Snackbar
          open={this.state.snackbarOpen}
          message={this.state.snackbarMessage}
          autoHideDuration={7000}
          onRequestClose={this.handleRequestClose} />
      </div>
    );
  }
});

module.exports = AdjacencyListSiteSearchView;
