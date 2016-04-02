//https://github.com/dreyescat/react-tutorial-webpack/blob/master/webpack.config.js

var webpack = require('webpack');

module.exports = {
  entry: './views/mainView.jsx',
  output: {
    // Output the bundled file.
    path: '../',
    // Use the name specified in the entry key as name for the bundle file.
    filename: 'bundle.js'
  },
  module: {
    loaders: [
      {
        // Test for js or jsx files.
        test: /\.jsx?$/,
        exclude: /node_modules/,
        loader: 'babel'
      }
    ]
  },
  resolve: {
    // Include empty string '' to resolve files by their explicit extension
    // (e.g. require('./somefile.ext')).
    // Include '.js', '.jsx' to resolve files by these implicit extensions
    // (e.g. require('underscore')).
    extensions: ['', '.js', '.jsx']
  }
};
