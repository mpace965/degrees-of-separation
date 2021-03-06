var React = require('react');
import AppBar from 'material-ui/lib/app-bar';
import LeftNav from 'material-ui/lib/left-nav';
import List from 'material-ui/lib/lists/list';
import ListItem from 'material-ui/lib/lists/list-item';
import getMuiTheme from 'material-ui/lib/styles/getMuiTheme';
import LastfmTheme from './lastfm/lastfmTheme';
import SteamTheme from './steam/steamTheme';
import DefaultTheme from 'material-ui/lib/styles/baseThemes/lightBaseTheme'

var LandingView = require('./landingView');
var AdjacencyListSiteSearchView = require('./adjacencyList/adjacencyListSiteSearchView');
var LastfmSiteSearchView = require('./lastfm/lastfmSiteSearchView');
var StatsView = require('./statsView');
var AboutView = require('./aboutView');
var SteamSiteSearchView = require('./steam/steamSiteSearchView');


var Header = React.createClass({
  getInitialState: function() {
    return {
      open: false
    };
  },

  handleTitleTap: function() {
    this.props.setActiveView(LandingView);
    this.props.setActiveTheme(DefaultTheme);
    this.setState({open: false});
  },

  handleLeftTap: function() {
    this.setState({open: true});
  },

  handleAboutTap: function() {
    this.props.setActiveView(AboutView);
    this.setState({open: false});
  },

  handleAdjListTap: function() {
    this.props.setActiveView(AdjacencyListSiteSearchView);
    this.props.setActiveTheme(DefaultTheme);
    this.setState({open: false});
  },

  handleHomeTap: function() {
    this.props.setActiveView(LandingView);
    this.props.setActiveTheme(DefaultTheme);
    this.setState({open: false});
  },

    handleStatsTap: function() {
    this.props.setActiveView(StatsView);
    this.props.setActiveTheme(LastfmTheme);
    this.setState({open: false});
  },
  
    handleSteamTap: function () {
    this.props.setActiveView(SteamSiteSearchView);
    this.props.setActiveTheme(SteamTheme);
    this.setState({open: false});
  },

  handleLastfmTap: function () {
    this.props.setActiveView(LastfmSiteSearchView);
    this.props.setActiveTheme(LastfmTheme);
    this.setState({open: false});
  },

  render: function() {

    return (
      <div>
        <AppBar
          title="Degrees of Separation"
          titleStyle={{
            cursor: 'pointer'
          }}
          onTitleTouchTap={this.handleTitleTap}
          onLeftIconButtonTouchTap={this.handleLeftTap} />
        <LeftNav open={this.state.open} docked={false} onRequestChange={open => this.setState({open})}>
          <List>
            <ListItem primaryText="Home" onTouchTap={this.handleHomeTap}/>
            <ListItem primaryText="About" onTouchTap={this.handleAboutTap}/>
            <ListItem primaryText="Statistics" onTouchTap={this.handleStatsTap}/>
            <ListItem
              primaryText="Connect"
              initiallyOpen={false}
              primaryTogglesNestedList={true}
              nestedItems={[
                <ListItem key={1} primaryText="Adjacency List" onTouchTap={this.handleAdjListTap} />,
                <ListItem key={2} primaryText="Last.fm" onTouchTap={this.handleLastfmTap} />,
                <ListItem key={3} primaryText="Steam" onTouchTap={this.handleSteamTap} />
              ]} />
          </List>
        </LeftNav>
      </div>
    );
  }
});

module.exports = Header;
