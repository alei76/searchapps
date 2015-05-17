#!/usr/bin/env bash

curl -XPOST 'http://localhost:8983/solr/collection1/schema?updateTimeoutSecs=90' -H 'Content-type:application/json' -d '{
  "replace-field-type":{
    "name":"text_ja",
    "class":"solr.TextField",
    "autoGeneratePhraseQueries":"true",
    "positionIncrementGap":"100",
    "indexAnalyzer":{
      "tokenizer":{ "class":"solr.JapaneseTokenizerFactory", "mode":"normal" },
      "filters":[
        { "class":"solr.JapaneseBaseFormFilterFactory" },
        { "class":"solr.JapanesePartOfSpeechStopFilterFactory", "tags":"lang/stoptags_ja.txt" },
        { "class":"solr.CJKWidthFilterFactory" },
        { "class":"solr.StopFilterFactory", "words":"lang/stopwords_ja.txt", "ignoreCase":"true" },
        { "class":"solr.JapaneseKatakanaStemFilterFactory", "minimumLength":"4" },
        { "class":"solr.LowerCaseFilterFactory" }
      ]
    },
    "queryAnalyzer":{
      "tokenizer":{ "class":"solr.JapaneseTokenizerFactory", "mode":"normal" },
      "filters":[
        { "class":"solr.JapaneseBaseFormFilterFactory" },
        { "class":"solr.JapanesePartOfSpeechStopFilterFactory", "tags":"lang/stoptags_ja.txt" },
        { "class":"solr.CJKWidthFilterFactory" },
        { "class":"solr.StopFilterFactory", "words":"lang/stopwords_ja.txt", "ignoreCase":"true" },
        { "class":"solr.JapaneseKatakanaStemFilterFactory", "minimumLength":"4" },
        { "class":"solr.LowerCaseFilterFactory" }
      ]
    }
  },
  "replace-field-type":{
    "name":"text_cjk",
    "class":"solr.TextField",
    "autoGeneratePhraseQueries":"true",
    "positionIncrementGap":"100",
    "indexAnalyzer":{
      "tokenizer":{ "class":"solr.NGramTokenizerFactory", "minGramSize":"2", "maxGramSize":"2" },
      "filters":[
        { "class":"solr.LowerCaseFilterFactory" }
      ]
    },
    "queryAnalyzer":{
      "tokenizer":{ "class":"solr.NGramTokenizerFactory", "minGramSize":"2", "maxGramSize":"2" },
      "filters":[
        { "class":"solr.LowerCaseFilterFactory" }
      ]
    }
  },
  "add-field":{ "name":"tags", "type":"string", "indexed":true, "stored":true, "required":false, "multiValued":true, "default": "nonTag" },
  "add-field":{ "name":"url", "type":"string", "indexed":true, "stored":true, "required":false, "multiValued":false },
  "add-field":{ "name":"title_ja", "type":"text_ja", "indexed":true, "stored":false, "multiValued":false },
  "add-field":{ "name":"title", "type":"text_cjk", "indexed":true, "stored":true, "multiValued":false, "termVectors":true, "termOffsets":true, "termPositions":true },
  "add-field":{ "name":"content_ja", "type":"text_ja", "indexed":true, "stored":false,"multiValued":false, "omitNorms":true },
  "add-field":{ "name":"content", "type":"text_cjk", "indexed":true, "stored":true, "multiValued":false, "termVectors":true, "termOffsets":true, "termPositions":true, "omitNorms":true },
  "add-field":{ "name":"locations", "type":"string", "indexed":true, "stored":false, "required":false, "multiValued":true },
  "add-field":{ "name":"persons", "type":"string", "indexed":true, "stored":false, "required":false, "multiValued":true },
  "add-field":{ "name":"foods", "type":"string", "indexed":true, "stored":false, "required":false, "multiValued":true },
  "add-field":{ "name":"events", "type":"string", "indexed":true, "stored":false, "required":false, "multiValued":true },
  "add-copy-field":{ "source":"title", "dest":["title_ja"] },
  "add-copy-field":{ "source":"content", "dest":["content_ja"] },
  "add-dynamic-field":{ "name":"*", "type":"ignored", "multiValued":true }
}'

curl 'http://localhost:8983/solr/collection1/config?updateTimeoutSecs=90' -H 'Content-type:application/json' -d '{
  "add-requesthandler" : { 
    "name":"/select",
    "class":"solr.SearchHandler",
    "useParams":"myParams",
    "defaults":{
      "echoParams":"explicit",
      "rows":"10",
      "defType":"edismax",
      "q.alt":"*:*",
      "qf":"title_ja^15 title^10 content_ja^5 content",
      "mm":"0%",
      "tie":"0.1",
      "fl":"id,tags,url",
      "facet":"on",
      "f.tags.facet.limit":"20",
      "f.tags.facet.mincount":"2",
      "f.locations.facet.limit":"5",
      "f.locations.facet.mincount":"2",
      "f.persons.facet.limit":"5",
      "f.persons.facet.mincount":"2",
      "f.foods.facet.limit":"5",
      "f.foods.facet.mincount":"2",
      "f.events.facet.limit":"5",
      "f.events.facet.mincount":"2",
      "hl":"on",
      "hl.useFastVectorHighlighter":"true",
      "hl.tag.pre":"<strong style=\"background:yellow\">",
      "hl.tag.post":"</strong>",
      "hl.fl":"title,content",
      "hl.requireFieldMatch":"true",
      "f.title.hl.alternateField":"title",
      "f.content.hl.snippets":"3",
      "f.content.hl.alternateField":"content",
      "f.content.hl.maxAlternateFieldLength":"100"
    }
  }
}'

curl 'http://localhost:8983/solr/collection1/config/params?updateTimeoutSecs=90' -H 'Content-type:application/json' -d '{
  "set" : { 
    "myParams":{
      "facet.field":["tags","locations","persons","foods","events"]
    }
  }
}'




