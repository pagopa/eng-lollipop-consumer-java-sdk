const newman = require('newman');

newman.run({
   collection: require('./collections/LollipopSDKTest.postman_collection.json'),
   environment: './env/Lollipop environment variables.postman_environment.json',
   reporters: 'htmlextra'
}, function (err, summary) {
  if (err) { throw err; }
  console.info('collection run complete!');
});