var React = require('react');
import Paper from 'material-ui/lib/paper';

var AboutPage = React.createClass({
  render: function() {
    const style = {
      height: '75%',
      width: '75%',
      padding: 10,
      margin: 20
    }

    return (
      <div className="aboutPage">
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
      </div>
    );
  }
});


module.exports = AboutPage;
