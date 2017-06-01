slackbot + cognative
--------------------

## 概要

[jBot](https://github.com/ramswaroop/jbot) のサンプルを参考に、cognativeと連携するサンプルです。

フリーテキストでは LUIS と連携してそのIntent を返却します。
画像をアップロードした場合、compute vision を使用して画像内のテキストを読み込み、その結果を返します。

## 事前準備

+ Slack bot の作成と、tokenの取得
+ LUIS アプリの作成、URLなどを application.properties に書き込む。
+ compute vision アプリを作成し、以下同様

## 起動

+ 通常の spring boot と同様。



