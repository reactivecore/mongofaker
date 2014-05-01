package net.reactivecore.mongofaker

import java.io.{IOException, File}
import org.apache.commons.io.FileUtils
import java.nio.file.Files

private[mongofaker] class ManagedDatabaseDirException (msg : String, cause : Throwable = null) extends MongoFakerException (msg, cause)

/**
 * Managed Database Directory.
 */
private[mongofaker] class ManagedDatabaseDir extends DatabaseDir{

  val tmpDir = FileUtils.getTempDirectory()
  if (!tmpDir.exists()) {
    throw new ManagedDatabaseDirException(s"TMP directory ${tmpDir} does not exist")
  }

  val subDir = try {
    Files.createTempDirectory(tmpDir.toPath(), "mongofaker_")
  } catch {
    case e : Throwable => throw new ManagedDatabaseDirException(s"Could not create database directory ${e.getMessage()}", e)
  }

  def directory : File = {
    subDir.toFile
  }

  def uninit() : Unit = {
    try {
      FileUtils.deleteDirectory(directory)
    } catch {
      case ioError : IOException =>
        throw new ManagedDatabaseDirException(s"Could not delete database directory ${directory.getAbsolutePath}", ioError)
    }
  }

}
