/*
 * Copyright (c) 2014 Contributor. All rights reserved.
 */
package dotty.tools
package dotc.classpath

import java.io.{File => JFile, FileFilter}
import java.net.URL
import dotty.tools.io.AbstractFile

/**
 * Common methods related to Java files and abstract files used in the context of classpath
 */
object FileUtils {
  implicit class AbstractFileOps(val file: AbstractFile) extends AnyVal {
    def isPackage: Boolean = file.isDirectory && mayBeValidPackage(file.name)

    def isClass: Boolean = !file.isDirectory && file.hasExtension("class") && !file.name.endsWith("$class.class")
      // FIXME: drop last condition when we stop being compatible with Scala 2.11

    def isScalaOrJavaSource: Boolean = !file.isDirectory && (file.hasExtension("scala") || file.hasExtension("java"))

    // TODO do we need to check also other files using ZipMagicNumber like in scala.tools.nsc.io.Jar.isJarOrZip?
    def isJarOrZip: Boolean = file.hasExtension("jar") || file.hasExtension("zip")

    /**
     * Safe method returning a sequence containing one URL representing this file, when underlying file exists,
     * and returning given default value in other case
     */
    def toURLs(default: => Seq[URL] = Seq.empty): Seq[URL] = if (file.file == null) default else Seq(file.toURL)
  }

  implicit class FileOps(val file: JFile) extends AnyVal {
    def isPackage: Boolean = file.isDirectory && mayBeValidPackage(file.getName)

    def isClass: Boolean = file.isFile && file.getName.endsWith(".class") && !file.getName.endsWith("$class.class")
      // FIXME: drop last condition when we stop being compatible with Scala 2.11
  }

  def stripSourceExtension(fileName: String): String =
    if (endsScala(fileName)) stripClassExtension(fileName)
    else if (endsJava(fileName)) stripJavaExtension(fileName)
    else throw new FatalError("Unexpected source file ending: " + fileName)

  def dirPath(forPackage: String): String = forPackage.replace('.', JFile.separatorChar)

  def endsClass(fileName: String): Boolean =
    fileName.length > 6 && fileName.substring(fileName.length - 6) == ".class"

  def endsScalaOrJava(fileName: String): Boolean =
    endsScala(fileName) || endsJava(fileName)

  def endsJava(fileName: String): Boolean =
    fileName.length > 5 && fileName.substring(fileName.length - 5) == ".java"

  def endsScala(fileName: String): Boolean =
    fileName.length > 6 && fileName.substring(fileName.length - 6) == ".scala"

  def stripClassExtension(fileName: String): String =
    fileName.substring(0, fileName.length - 6) // equivalent of fileName.length - ".class".length

  def stripJavaExtension(fileName: String): String =
    fileName.substring(0, fileName.length - 5)

  // probably it should match a pattern like [a-z_]{1}[a-z0-9_]* but it cannot be changed
  // because then some tests in partest don't pass
  def mayBeValidPackage(dirName: String): Boolean =
    (dirName != "META-INF") && (dirName != "") && (dirName.charAt(0) != '.')

  def mkFileFilter(f: JFile => Boolean): FileFilter = new FileFilter {
    def accept(pathname: JFile): Boolean = f(pathname)
  }
}
