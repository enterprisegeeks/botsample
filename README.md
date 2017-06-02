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

## cognitive serviceの準備

https://azure.microsoft.com/ja-jp/try/cognitive-services/
あたりで、Azure アカウントを作成して、サブスクリプションキーを作成する。
サブスクリプションキーを application.propertiesに書いておく。

API document は [ここ](https://docs.microsoft.com/ja-jp/azure/cognitive-services/)を見る。

## LUIS 

[ここ](https://www.luis.ai/) からスタート。
適当にアプリを作成し、 Intentをいくつか作っておく。
Intent 作成後、学習を行いアプリを公開して準備完了。
実行には、キーとapp-id が必要なので控えておく。

## LICENSE

ASL2


