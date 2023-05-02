const newman = require('newman');

newman.run({
   collection: require('./collections/lollipopSDKTest.postman_collection.json'),
   environment: './env/lollipopEnvironmentVariables.postman_environment.json',
   reporters: ['cli', 'htmlextra'],
   bail: true
}, function (err, summary) {
  if (err) {  throw err; }
  if(summary?.run?.error){ throw 'collection run encountered an error.';}

  if(summary?.run?.failures?.length > 0){
    const errors = summary.run.failures;
    throw `following collection tests failed: ${errors.map((er) => `\n${er.source.name}`)}`;
  }

  console.info('collection run completed.');
});