name := "whose-volume"

version := "0.1"

scalaVersion := "2.12.9"
val awsSDKVersion = "1.11.623"
val airframeLoggerVersion = "19.9.0"

libraryDependencies += "org.typelevel" %% "cats-effect" % "1.4.0"
libraryDependencies += "org.typelevel" %% "cats-tagless-macros" % "0.9"

libraryDependencies += "com.amazonaws" % "aws-java-sdk-support" % s"$awsSDKVersion"
libraryDependencies += "com.amazonaws" % "aws-java-sdk-ec2" % s"$awsSDKVersion"
libraryDependencies += "org.wvlet.airframe" %% "airframe-log" % s"$airframeLoggerVersion"

addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)
addCompilerPlugin("org.typelevel" %% "kind-projector" % "0.10.3")

wartremoverErrors ++= Warts.unsafe
