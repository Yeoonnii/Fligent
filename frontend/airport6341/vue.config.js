const { defineConfig } = require('@vue/cli-service')
module.exports = defineConfig({
  transpileDependencies: true,
  devServer : {
    proxy : {
      "/ROOT" : {
        target : 'http://127.0.0.1:8080/',
        changeOrigin : true,
        logLevel : 'debug'
      },

      // "/api" : {
      //   target : 'http://127.0.0.1:8080/ROOT/',
      //   changeOrigin : true,
      //   logLevel : 'debug'
      // },

    },
    port : 9090
  }
})
