var React = require('react');
import Paper from 'material-ui/lib/paper';
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


var AboutView = React.createClass({
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
      <div className="aboutView">
        <Paper style={style} zDepth={1}>
          <div>
            <center><h2>About</h2></center>
          </div>
          <div>
          	 	<h3>What our App Does</h3>
          		<p>Our application is built around the concept of the six degrees of separation.
                 We are using this concept to show that any two items in a given data set can be linked together.</p>

          		<h3>What is the Six Degrees of Separation?</h3>
          		<p>The theory of six degrees of separation is that everyone and everything is six or fewer steps away by way of association.
                 That is to say that a chain of "friends of friends" can be formed to connect any two people or things.</p>

          		<h3>About the Developers</h3>
          		<p>We team a group of students at Purdue University studying Computer Science.
                 This website is a result of a team effort for our Software Engineering class, <a href="https://www.cs.purdue.edu/homes/bxd/307/">CS 307</a>.
                 You can view the full source of this website, and view a full history of our development on our <a href="https://github.com/mpace965/degrees-of-separation">GitHub repo.</a></p>
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
      </div>
    );
  }
});


module.exports = AboutView;
