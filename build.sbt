name := "view-attach-instances"

version := "0.1"

scalaVersion := "2.12.9"
val awsSDKVersion = "1.11.623"

// https://mvnrepository.com/artifact/org.typelevel/cats-effect
libraryDependencies += "org.typelevel" %% "cats-effect" % "1.4.0"

// https://mvnrepository.com/artifact/com.amazonaws/aws-java-sdk
// https://mvnrepository.com/artifact/com.amazonaws/aws-java-sdk-support
libraryDependencies += "com.amazonaws" % "aws-java-sdk-support" % s"$awsSDKVersion"
libraryDependencies += "com.amazonaws" % "aws-java-sdk-ec2" % s"$awsSDKVersion"


// https://mvnrepository.com/artifact/org.typelevel/cats-tagless-macros
libraryDependencies += "org.typelevel" %% "cats-tagless-macros" % "0.9"

addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)
addCompilerPlugin("org.typelevel" %% "kind-projector" % "0.10.3")

wartremoverErrors ++= Warts.unsafe
