[![Build Status](http://img.shields.io/travis/rholder/esthree.svg)](https://travis-ci.org/rholder/esthree) [![Latest Version](http://img.shields.io/badge/latest-0.2.4-brightgreen.svg)](https://github.com/rholder/esthree/releases/tag/v0.2.4) [![License](http://img.shields.io/badge/license-apache%202-brightgreen.svg)](https://github.com/rholder/esthree/blob/master/LICENSE)


##What is this?
`esthree` is an S3 commandline client that just works. It isn't meant to be feature complete across the entire S3 API.
It's meant to do a few core things and do them well. Namely, AWS doesn't promise 100% uptime and you shouldn't expect
anything that's running in the cloud to always return your data consistently all the time, especially when you're doing
things like asking for too much of it at one time or playing around on public wifi. To that end, `esthree` makes a
conscious effort to retry and validate whatever you're sending/receiving to/from S3. If `esthree` fails at its mission,
it should raise a non-zero exit code on termination.

##Features
* Single minified binary install, all you need is a JVM on your path
* Multi-part download a massive file in small chunks instead the whole thing in one request
* Multi-part upload a massive file in small chunks
* List bucket contents
* Upload/Download progress bars
* Returns non-zero when bad things happen

##Installation
`esthree` is just a single binary that you can drop anywhere you feel like on a *nix based system (sorry Windows, maybe
it works with Cygwin...). As long as you have a JVM installed that's at least 1.6 or above, you can install it with:
```
sudo curl -o /usr/local/bin/esthree -L "https://github.com/rholder/esthree/releases/download/v0.2.4/esthree" && \
sudo chmod +x /usr/local/bin/esthree
```

##Quickstart
Make sure you have your AWS keys in one of the places defined by the
[default AWS credentials provider chain](http://docs.aws.amazon.com/AWSJavaSDK/latest/javadoc/com/amazonaws/auth/DefaultAWSCredentialsProviderChain.html),
 which is in this order:
* Environment Variables - AWS_ACCESS_KEY_ID and AWS_SECRET_KEY
* Java System Properties - aws.accessKeyId and aws.secretKey
* Credential profiles file at the default location (~/.aws/credentials) shared by all AWS SDKs and the AWS CLI
* Instance profile credentials delivered through the Amazon EC2 metadata service

You can pass any number of Java System properties (as well as any other JVM specific arguments) to `esthree`
through `JAVA_OPTS`, such as:
```
export JAVA_OPTS="-Daws.accessKeyId=SOME_ACCESS_KEY -Daws.secretKey=SOME_SECRET_KEY"
```
If you don't provide them in one of these locations, you can also specify your credentials as pure commandline arguments
which will override the default AWS credentials provider chain.

Here are some things you can do:
```bash
esthree get s3://some-bucket/somefile.wow
esthree put some-local-file.wow s3://some-bucket/foo.wow
esthree get-multi some-giant-file.wow s3://some-bucket/giant-file.wow
```

##Documentation
Javadoc can be found [here](http://rholder.github.io/esthree/javadoc/0.2.4).

##License
The esthree project is released under version 2.0 of the
[Apache License](http://www.apache.org/licenses/LICENSE-2.0).

##Contribute
1. Check for open issues or open a fresh issue to start a discussion around a feature idea or a bug.
1. Fork the repository on GitHub to start making your changes to the **master** branch (or branch off of it).
1. Write a test which shows that the bug was fixed or that the feature works as expected.
1. Send a pull request and bug the maintainer until it gets merged and published. :)

##References
* http://aws.amazon.com/s3/
* http://s3tools.org/s3cmd
* http://www.beaconhill.com/solutions/opensource/s3cp.html

##Contributors
* Jason Dunkelberger (dirkraft)
