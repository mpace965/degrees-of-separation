var React = require('react');
import Paper from 'material-ui/lib/paper';

var AboutView = React.createClass({
  render: function() {
    const style = {
      height: '75%',
      width: '75%',
      padding: 10,
      margin: 20
    }

    return (
      <div className="aboutView">
        <Paper style={style} zDepth={1}>
          <div>
            <center><h2>About Our Algorithm</h2></center>
          </div>
          <div>
          	Lorem ipsum dolor sit amet, consectetur adipiscing elit. Curabitur eget nulla sit amet ligula eleifend scelerisque. Sed id nisl mi. Fusce ac quam lectus. Vestibulum pellentesque egestas nulla, non condimentum velit tincidunt vitae. Praesent dictum lectus nisi, nec cursus ipsum commodo a. Praesent leo est, bibendum vitae consectetur at, aliquet sed justo. Cras est tellus, vestibulum ac risus sit amet, cursus sodales tortor. Suspendisse rutrum lectus non orci feugiat accumsan. Nam nec neque consectetur, ornare elit eget, gravida augue. Vivamus nec tincidunt tortor. Vestibulum mattis est in tortor dictum ultrices. Duis finibus ex eget quam sodales posuere. Ut rutrum ut sem at porta. Suspendisse venenatis justo in aliquam viverra. 	
          </div>
        </Paper>
      </div>
    );
  }
});


module.exports = AboutView;
