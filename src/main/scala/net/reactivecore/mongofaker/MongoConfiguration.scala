package net.reactivecore.mongofaker

/**
 * Configuration for running MongoDB.
 * @param port port to listen on, if set to none a free port will be used.
 * @param bindIp ip to listen on, if set to none 127.0.0.1 will be used
 * @param dataDir database directory. If set to none a temporary directory will be used.
 * @param executable 'mongod' executable. If set to none the one from the path will be used.
 * @param extraArguments a list of extra arguments to be added to mongod. Default is "--nojournal" as this drastically reduces startup time on 64bit systems.
 */
case class MongoConfiguration (
  port : Option[Int] = None,
  bindIp : Option[String] = None,
  dataDir : Option[String] = None,
  executable : Option[String] = None,
  extraArguments : List[String] = List("--nojournal")
)
