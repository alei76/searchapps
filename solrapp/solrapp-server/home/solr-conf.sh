#!/usr/bin/env bash

curl -XPOST http://localhost:8983/solr/collection1/schema -H 'Content-type:application/json' -d '{
  "add-field":{ "name":"tags", "type":"string", "indexed":true, "stored":true, "required":false, "multiValued":true, "default": "nonTag" },
  "add-field":{ "name":"url", "type":"string", "indexed":true, "stored":true, "required":false, "multiValued":false },
  "add-field":{ "name":"title_ja", "type":"text_ja", "indexed":true, "stored":false, "multiValued":false },
  "add-field":{ "name":"title", "type":"text_cjk", "indexed":true, "stored":true, "multiValued":false, "termVectors":true, "termOffsets":true, "termPositions":true },
  "add-field":{ "name":"content_ja", "type":"text_ja", "indexed":true, "stored":false,"multiValued":false, "omitNorms":true },
  "add-field":{ "name":"content", "type":"text_cjk", "indexed":true, "stored":true, "multiValued":false, "termVectors":true, "termOffsets":true, "termPositions":true, "omitNorms":true },
  "add-field":{ "name":"locations", "type":"string", "indexed":true, "stored":true, "required":false, "multiValued":true },
  "add-field":{ "name":"persons", "type":"string", "indexed":true, "stored":true, "required":false, "multiValued":true },
  "add-field":{ "name":"events", "type":"string", "indexed":true, "stored":true, "required":false, "multiValued":true },
  "add-copy-field":{ "source":"title", "dest":["title_ja"] },
  "add-copy-field":{ "source":"content", "dest":["content_ja"] },
  "add-dynamic-field":{ "name":"*", "type":"ignored", "multiValued":true }
}'

curl http://localhost:8983/solr/collection1/config -H 'Content-type:application/json' -d '{
  "add-requesthandler" :
  { "name": "/select", "class":"solr.SearchHandler",
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
    "facet.field":"tags",
    "f.tags.facet.limit":"20",
    "f.tags.facet.mincount":"2",
    "facet.field":"locations",
    "f.locations.facet.limit":"5",
    "f.locations.facet.mincount":"2",
    "facet.field":"persons",
    "f.persons.facet.limit":"5",
    "f.persons.facet.mincount":"2",
    "facet.field":"foods",
    "f.foods.facet.limit":"5",
    "f.foods.facet.mincount":"2",
    "facet.field":"events",
    "f.events.facet.limit":"5",
    "f.events.facet.mincount":"2",

    "hl":"on",
    "hl.useFastVectorHighlighter":"true",
    "hl.tag.pre":"<![CDATA[<strong style=\"background:yellow\">]]>",
    "hl.tag.post":"<![CDATA[</strong>]]>",
    "hl.fl":"title,content",
    "hl.requireFieldMatch":"true",
    "f.title.hl.alternateField":"title",
    "f.content.hl.snippets":"3",
    "f.content.hl.alternateField":"content",
    "f.content.hl.maxAlternateFieldLength":"100"

    }
  }
}'

