
name := "scala-stm"

organization := "org.scala-stm"

version := "0.8-SNAPSHOT"

scalaVersion := "2.11.2"

crossScalaVersions := Seq("2.11.2", "2.10.4", "2.9.3")

libraryDependencies += ("org.scalatest" %% "scalatest" % "[1.5,)" % "test")

libraryDependencies += ("junit" % "junit" % "4.5" % "test")

// skip exhaustive tests
testOptions += Tests.Argument("-l", "slow")

// test of TxnExecutor.transformDefault must be run by itself
parallelExecution in Test := false

////////////////////
// publishing

pomExtra :=
  <url>http://nbronson.github.com/scala-stm/</url>
  <licenses>
    <license>
      <name>BSD</name>
      <url>https://github.com/nbronson/scala-stm/blob/master/LICENSE.txt</url>
      <distribution>repo</distribution>
    </license>
  </licenses>
  <scm>
    <connection>scm:git:git@github.com:nbronson/scala-stm.git</connection>
    <url>git@github.com:nbronson/scala-stm.git</url>
  </scm>
  <developers>
    <developer>
      <id>nbronson</id>
      <name>Nathan Bronson</name>
      <email>ngbronson@gmail.com</email>
    </developer>
  </developers>

publishMavenStyle := true

publishTo <<= (version) { v: String =>
    val base = "https://oss.sonatype.org/"
    if (v.trim.endsWith("SNAPSHOT"))
      Some("snapshots" at base + "content/repositories/snapshots/")
    else
      Some("releases" at base + "service/local/staging/deploy/maven2/")
  }

// exclude scalatest from the Maven POM
pomPostProcess := { xi: scala.xml.Node =>
    import scala.xml._
    val badDeps = (xi \\ "dependency") filter {
      x => (x \ "artifactId").text != "scala-library"
    } toSet
    def filt(root: Node): Node = root match {
      case x: Elem => {
        val ch = x.child filter { !badDeps(_) } map { filt(_) }
        Elem(x.prefix, x.label, x.attributes, x.scope, ch: _*)
      }
      case x => x
    }
    filt(xi)
  }

credentials += Credentials(Path.userHome / ".ivy2" / ".credentials")
