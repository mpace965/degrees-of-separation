var React = require('react');
var $ = require('jquery');
import Paper from 'material-ui/lib/paper';
import LinearProgress from 'material-ui/lib/linear-progress';
import DefaultTheme from 'material-ui/lib/styles/baseThemes/lightBaseTheme'
import LastfmTheme from '../lastfm/lastfmTheme';

import ConnectLink from './connectLink'
import LastfmResultView from '../lastfm/lastfmResultView';
import AdjacencyListResultView from '../adjacencyList/adjacencyListResultView'

var RecentlySearchedView = React.createClass({
  getInitialState: function() {
    return {
      timeout: {},
      recentChain: [],
      startIndex: 0,
      apiLoading: false
    };
  },

  getDefaultProps: function() {
    return {
      length: 5
    };
  },

  getRecentsFromServer: function() {
    $.ajax({
        url: '/api/recentConnections',
        dataType: 'json',
        cache: false,
        success: function(data) {
          this.setState({recentChain: data});

          if (this.state.recentChain.length > this.props.length) {
            this.incrementStart();
            this.setState({timeout: setInterval(this.incrementStart, 4000)});
          }
        }.bind(this),
        error: function(xhr, status, err) {
          console.error('/api/recentConnections', status, err.toString());
        }.bind(this)
    });
  },

  componentDidMount: function() {
    this.getRecentsFromServer();
  },

  componentWillUnmount: function() {
    clearInterval(this.state.timeout);
  },

  incrementStart: function() {
    if (this.state.startIndex < this.state.recentChain.length - 1) {
      this.setState({startIndex: this.state.startIndex + 1});
    } else {
      this.setState({startIndex: 0});
    }
  },

  getInfoFromName: function(name) {
    switch(name) {
      case 'Adjacency List':
        return {
          url: '/api/connectAdjacency',
          view: AdjacencyListResultView,
          theme: DefaultTheme
        };
        break;
      case 'Last.fm':
        return {
          url: '/api/connectLastfm',
          view: LastfmResultView,
          theme: LastfmTheme
        };
        break;
    }
  },

  setLoading: function(set) {
    this.setState({apiLoading: set});
  },

  generateRecents: function() {
    var recents = [];
    var end = 0;

    if (this.state.recentChain.length < this.props.length) {
      end = this.state.recentChain.length;
    } else {
      end = this.props.length;
    }

    //Appends the first n elements to the end of the array, to make it easy to get the elements before the index resets to 0.
    var wrapArray = this.state.recentChain.concat(this.state.recentChain.slice(0, this.props.length - 1));

    for (var i = 0; i < end; i++) {
      var responseObject = wrapArray[this.state.startIndex + i];
      var info = this.getInfoFromName(responseObject.site);

      recents.push(
        <div key={i} className="recentItem">
          <ConnectLink
            begin={responseObject.begin}
            end={responseObject.end}
            url={info.url}
            setLoading={this.setLoading}
            setActiveView={this.props.setActiveView}
            setActiveTheme={this.props.setActiveTheme}
            linkView={info.view}
            linkTheme={info.theme} >
            {responseObject.site}: {responseObject.begin}→{responseObject.end}
          </ConnectLink>
        </div>
      );
    }

    return recents;
  },

  render: function() {
    const style = {
      height: '75%',
      width: '25%',
      fontSize: 'large',
      padding: '5px',
      margin: '20px',
    };

    var recents = this.generateRecents();
    var linearProgress;

    if (this.state.apiLoading) {
      linearProgress = <LinearProgress mode="indeterminate" />
    }

    return (
      <Paper style={style} zDepth={1}>
        {linearProgress}
        <h3 className="recentItem">Recently Connected</h3>
        <div>{recents}</div>
      </Paper>
    );
  }
});

module.exports = RecentlySearchedView;
