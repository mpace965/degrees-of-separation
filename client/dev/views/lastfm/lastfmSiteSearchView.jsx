var React = require('react');
var $ = require('jquery');
var d3 = require('d3');
import Paper from 'material-ui/lib/paper';
import CircularProgress from 'material-ui/lib/circular-progress';
import TextField from 'material-ui/lib/text-field';
import RaisedButton from 'material-ui/lib/raised-button';
import Snackbar from 'material-ui/lib/snackbar';

var LastfmResultView = require('./lastfmResultView');

var LastfmSiteSearchView = React.createClass({
  getInitialState: function() {
    return {
      connectionBegin: '',
      connectionEnd: '',
      apiResponse: {},
      apiRequest: {},
      apiLoading: false,
      snackbarOpen: false,
      snackbarMessage: ''
    };
  },

  //Make the api request to the server
  loadChainFromServer: function() {
    var request = $.ajax({
        url: '/api/connectLastfm',
        dataType: 'json',
        cache: false,
        timeout: 300000,
        data: {begin: this.state.connectionBegin, end: this.state.connectionEnd},
        success: function(data) {
          this.setState({apiResponse: data}, function() {
            this.setState({apiLoading: false});
            var newGraph = this.processApiResponse();
            this.props.setActiveView(LastfmResultView, {graph: newGraph});
          });
        }.bind(this),
        error: function(xhr, status, err) {
          this.setState({apiLoading: false});
          this.setState({snackbarOpen: true, snackbarMessage: xhr.responseText});
          console.error('/api/connectLastfm', status, err.toString());
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
      cancelButton = <RaisedButton label="Cancel" onMouseUp={this.handleCancel} />;
      circularProgress = <CircularProgress />;
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
          <img src="res/lastfm-logo-sm.png"/>

          <br/>
          <br/>

          <div className="flexRowItem">
            <TextField
              hintText="Connect one artist"
              value={this.state.connectionBegin}
              onChange={this.handleConnectionBeginChange}
              onKeyDown={this.handleKeyDown} />
            <TextField
              hintText="to another"
              value={this.state.connectionEnd}
              onChange={this.handleConnectionEndChange}
              onKeyDown={this.handleKeyDown} />
            <RaisedButton label="Submit" onMouseUp={this.handleSubmit} />
          </div>
          <div className="flexRowItem">{circularProgress}</div>
          <div className="flexRowItem">{cancelButton}</div>

          <br/>

          <p>
            Last.fm is an online service which allows its users to catalog all of the music they listen to.
            It uses this listening data to compile a massive database of artists from a wide variety of genres.
            This database, along with other user-sourced statistics such as tags and more, is used by Last.fm to calculate how similar one artist is to another.
            Our site harnesses this calculation of similarity to chain artists together, making sure each artist in a chain is significantly similar to the one next to it.
            Use the input boxes above to specify which two artists you'd like to see connected. The more unrelated they are, the more intermediate artists will be present.
          </p>

          <br/>

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

module.exports = LastfmSiteSearchView;
