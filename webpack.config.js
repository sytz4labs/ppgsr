var path = require('path');

module.exports = {
	entry : {
		index: './src/main/js/index/index.js',
		budget: './src/main/js/budget/budget.js',
		cam: './src/main/js/cam/cam.js',
		camlive: './src/main/js/camlive/camlive.js',
		clock: './src/main/js/clock/index.js',
		config: './src/main/js/config/config.js',
		ewiki: './src/main/js/ewiki/wiki.js',
		fu: './src/main/js/fu/fu.js',
		linkfarm: './src/main/js/linkfarm/linkfarm.js',
		tasks: './src/main/js/tasks/tasks.js',
		qrs: './src/main/js/qr/qrs.js',
		wiki: './src/main/js/wiki/wiki.js'
	},
  output: {
    path: __dirname,
		path : path.resolve( __dirname, 'src/main/resources/static/built/' ),
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