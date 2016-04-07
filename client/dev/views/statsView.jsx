var React = require('react');
var $ = require('jquery');
import Paper from 'material-ui/lib/paper';
import Snackbar from 'material-ui/lib/snackbar';
import { FacebookButton, FacebookCount } from 'react-social';
import {
  ShareButtons,
  ShareCounts,
  generateShareIcon
} from 'react-share';

const {
  FacebookShareButton,
  GooglePlusShareButton,
  LinkedinShareButton,
  TwitterShareButton
} = ShareButtons;

const {
  FacebookShareCount,
  GooglePlusShareCount,
  LinkedinShareCount
} = ShareCounts;

const FacebookIcon = generateShareIcon('facebook');
const TwitterIcon = generateShareIcon('twitter');
const GooglePlusIcon = generateShareIcon('google');
const LinkedinIcon = generateShareIcon('linkedin');


var StatsView = React.createClass({


  getInitialState: function() {
    return {
      stats: [],
      snackbarOpen: false,
      snackbarMessage: ''
    };
  },

  getStatsFromServer: function() {
    $.ajax({
        url: '/api/getStatistics',
        dataType: 'json',
        cache: false,
        success: function(data) {
          this.setState({stats: data});
        }.bind(this),
        error: function(xhr, status, err) {
          this.setState({snackbarOpen: true, snackbarMessage: xhr.responseText});
          console.error('/api/getStatistics', status, err.toString());
        }.bind(this),
        timeout: 60000
    });
  },

  handleRequestClose: function() {
    this.setState({snackbarOpen: false, snackbarMessage: ''});
  },

  componentDidMount: function() {
    this.getStatsFromServer();
  },

  render: function() {
    const style = {
      height: '75%',
      width: '75%',
      padding: 10,
      margin: 20
    }

    const shareUrl = 'http://degreeofconnection.com';
    const title = 'View Your Connections At';
    
    return (
      <div className="statsView">
        <Paper style={style} zDepth={1}>
          <div>
            <center><h2>Statistics</h2></center>
          </div>
          <div>
            <p>{this.state.stats[0]}</p>
          </div>
        </Paper>
   
        <div className="socialButtons">
        
         <FacebookShareButton
            url={shareUrl}
            title={title}
            className="Demo__some-network__share-button">
            <FacebookIcon
              size={32}
              round={true} />
          </FacebookShareButton>
     <sp>     
        <TwitterShareButton
            url={shareUrl}
            title={title}
            className="Demo__some-network__share-button">
            <TwitterIcon
              size={32}
              round={true} />
          </TwitterShareButton>
          </sp>
          
          <GooglePlusShareButton
            url={shareUrl}
            className="Demo__some-network__share-button">
            <GooglePlusIcon
              size={32}
              round={true} />
          </GooglePlusShareButton>
        </div>
        
        <Snackbar
          open={this.state.snackbarOpen}
          message={this.state.snackbarMessage}
          autoHideDuration={7000}
          onRequestClose={this.handleRequestClose} />
      </div>
    );
  }

});

module.exports = StatsView;
