var path = require('path');

module.exports = {
  watch: true,
	entry : {
		index: './src/main/js/index/index.js',
		config: './src/main/js/config/config.js',
		linkfarm: './src/main/js/linkfarm/linkfarm.js',
		budget: './src/main/js/budget/budget.js'
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