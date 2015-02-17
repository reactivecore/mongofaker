mongofaker
==========

MongoFaker kicks up a temporary MongoDB instance for testing purposes.

It's written in Scala and used for some Integration Tests for Applications using the Play! Framework.

Starting one MongoDB fake server takes around 250ms, much faster than any other solution.

Usage
=====

MongoFaker is published at Maven Central, for Scala 2.10 and Scala 2.11

Add these lines to your Applications' build.sbt

    libraryDependencies += "net.reactivecore" %% "mongofaker" % "0.2.1"

Inside your Testcode you can spin up a MongoDB Server:

    import net.reactivecore.mongofaker.{MongoConfiguration, MongoInstance}
    
    val mongoInstance = MongoInstance.start(MongoConfiguration())
    // MongoDB is already up and running
    
    val port = mongoInstance.port
    val address = mongoInstance.address
   
    // Init your Application with port and address
    // Test your Application
    
    mongoInstance.stop()
    
    // MongoDB is ended and the temporary database directory is deleted.


Notes
=====

* The `mongod`executable must be somewhere in your Unix `$PATH`. You can also submit your own using `MongoConfiguration.executable`
* Mongofaker is tested in Linux and OSX. Windows won't work.
* See `MongoConfiguration` for other configuration options. Default values are optimized for fast startup times.
* The output of the Mongod process is logged into slf4j with tag mongod-PID. This should work out of the box with most frameworks as they implement some slf4j backend.


Alternatives
============

[Flapdoodle's Embed MongoDB](https://github.com/flapdoodle-oss/de.flapdoodle.embed.mongo) is a much more mature solution. It les you choose your MongoDB version and downloads and unpacks MongoDB on the fly. 

However it has a much higher startup time (due potential downloading and extracting).
