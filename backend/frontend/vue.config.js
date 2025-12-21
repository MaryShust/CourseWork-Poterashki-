module.exports = {
  publicPath: '/',
  outputDir: '../static',
  configureWebpack: {
    resolve: {
      alias: {
        '@': require('path').resolve(__dirname, 'src')
      }
    }
  }
}