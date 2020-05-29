var path = require('path');

module.exports = {
  watch: true,
	entry : {
		index: './src/main/js/index/index.js',
		budget: './src/main/js/budget/budget.js',
		cam: './src/main/js/cam/cam.js',
		fu: './src/main/js/fu/fu.js',
		config: './src/main/js/config/config.js',
		linkfarm: './src/main/js/linkfarm/linkfarm.js',
		ewiki: './src/main/js/ewiki/wiki.js',
		wiki: './src/main/js/wiki/wiki.js'
	},
  output: {
    path: __dirname,
		path : path.resolve( __dirname, 'bin/static/built/' ),
		filename : '[name].bundle.js'
  },
  module: {
    rules: [
      {
        test: /\.(js|jsx)$/,
        exclude: /node_modules/,
        use: ['babel-loader']
      },
      {
        test: /\.css$/,
        use: [
          {
            loader: "style-loader"
          },
          {
            loader: "css-loader"
          }
        ]
      }
    ]
  },
  resolve: {
    extensions: ['*', '.js', '.jsx']
  }
};