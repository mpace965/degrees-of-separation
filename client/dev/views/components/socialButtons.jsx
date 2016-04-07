var React = require('react');
import { ShareButtons, ShareCounts, generateShareIcon } from 'react-share';
import { FacebookButton, FacebookCount } from 'react-social';

var SocialButtons = React.createClass({
  render: function() {
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

    const shareUrl = 'http://degreeofconnection.com';
    const title = 'View Your Connections At';

    return (
      <div className="socialButtons">
        <FacebookShareButton url={shareUrl} title={title} style={{cursor: "pointer"}}>
           <FacebookIcon size={32} round={true} />
        </FacebookShareButton>
    <sp></sp>
       <TwitterShareButton url={shareUrl} title={title} style={{cursor: "pointer"}}>
           <TwitterIcon size={32} round={true} />
        </TwitterShareButton>
    <sp></sp>
        <GooglePlusShareButton url={shareUrl} style={{cursor: "pointer"}}>
             <GooglePlusIcon size={32} round={true} />
        </GooglePlusShareButton>
      </div>
    );
  }
});

module.exports = SocialButtons;
