## whose-volume

Trusted Advisorの[「耐障害性 - Amazon EBS Snapshots」](https://aws.amazon.com/jp/premiumsupport/trustedadvisor/best-practices/)で検出されたボリュームと当該ボリュームがアタッチされたEC2 インスタンス ID の一覧を出力します。


## ビルドと実行方法

```bash
sbt assembly
java -jar ./target/scala-2.12/whose-volume-assembly-0.1.jar
```

## クレデンシャルの指定方法

[DefaultAWSCredentialsProviderChain](https://docs.aws.amazon.com/AWSJavaSDK/latest/javadoc/index.html?com/amazonaws/auth/DefaultAWSCredentialsProviderChain.html)
を使用しています。

[ドキュメント](https://docs.aws.amazon.com/ja_jp/sdk-for-java/v1/developer-guide/credentials.html) 
に記載されている各方法でクレデンシャルを指定できます。

### AssumeRoleするプロファイルを使う場合

[assume-role](https://github.com/remind101/assume-role) を使うことでAssumeRoleするプロファイルを使って実行できます。

```
assume-role your-profile java -jar ./target/scala-2.12/whose-volume-assembly-0.1.jar
```
