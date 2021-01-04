# Update node modules

```
rmdir node* /q/s
del package-lock.json
npm init
npm i -s -D @babel/core @babel/preset-env @babel/preset-react babel-loader css-loader style-loader webpack webpack-cli
npm i -s axios react react-dom react-modal react-router-dom
```