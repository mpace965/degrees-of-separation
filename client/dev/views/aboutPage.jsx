var React = require('react');
import Paper from 'material-ui/lib/paper';



var AboutPage = React.createClass({
  render: function() {
  
  
    const style = {
      height: '75%',
      width: '75%',
      padding: 10,
      margin: 2
    }
    
	

    return (
    
    
      <div className="aboutPage">
        <Paper style={style} zDepth={1}>
          <div className="Title">
          		<center> 
          			<font size ="6"> About Page </font> 
          		</center>
          </div>
        </Paper>
        <Paper style={style} zDepth={1}>
          <div className="Does">
          	 	<font size = "5"> What our App Does: </font>
          		<font size ="3"> <br> <br> Our application is built around the concept of the six degrees of separation. We are using that concept to show that any two items can be linked together. </br> </br> </font> 	
          		<font size = "5"> What is the Six Degrees of Separation: </font>	
          		<font size = "3"> <br><br> The six degrees of separation is a theory that everyone and everything is six or fewer steps away by way of association. That is to say that a chain of "friends of friends" can be formed to connect any two people or things. </br></br> </font>
          		<font size = "5"> About Us: </font>
          		<font size ="3"> <br> <br> We are a group of students at Purdue University working on getting a degree in Computer Science. This website is our group project for CS 307 and if you want to see the development process it went though look at out github repo here <a href="https://github.com/mpace965/degrees-of-separation">GITHUB</a> </br> </br> </font> 	
          		
          </div>
        </Paper>
      </div>
    );
  }
});


module.exports = AboutPage;
