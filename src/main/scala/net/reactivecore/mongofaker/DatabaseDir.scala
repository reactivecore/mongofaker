package net.reactivecore.mongofaker

import java.io.File

/**
 * Provides a Database Directory.
 */
private [mongofaker] trait DatabaseDir {

  /**
   * Returns the directory
    *
    * @return
    */
  def directory : File

  /**
   * Uninitializes the directory.
   */
  def uninit
}

/**
 * Unmanaged directory
 * @param directory
 */
private[mongofaker] class UnmanagedDatabaseDir (val directory : File) extends DatabaseDir {
  if (!directory.exists()) {
    throw new MongoFakerException(s"Database directory s{directory} does not exist")
  }
  /** Empty uninit. */
  def uninit = {}
}