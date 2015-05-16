#!/usr/bin/env bash

curl -XPOST 'localhost:9200/collection1/_close?pretty' -d '{}'

sleep 3

curl -XPUT 'localhost:9200/collection1/_settings?pretty' -d '{
    "settings" : {
        "analysis" : {
            "analyzer" : {
                "text_ja" : {
                    "tokenizer" : "japanese_tokenizer",
                    "filter" : [ "kuromoji_baseform_filter", "kuromoji_part_of_speech_filter", "ja_stop_filter", "kuromoji_stemmer_filter", "lowercase_filter" ]
                },
                "text_cjk": {
                    "tokenizer":  "standard",
                    "filter": [ "lowercase", "cjk_bigram" ]
                }
            },
            "tokenizer" : {
                "japanese_tokenizer" : {
                    "type" : "kuromoji_tokenizer", "mode" : "search"
                }
            },
            "filter" : {
                "kuromoji_baseform_filter" : {
                    "type" : "kuromoji_baseform"
                },
                "kuromoji_part_of_speech_filter" : {
                    "type" : "kuromoji_part_of_speech", "stoptags" : [ "助詞-格助詞-一般", "助詞-終助詞" ]
                },
                "ja_stop_filter" : {
                    "type" : "ja_stop"
                },
                "kuromoji_stemmer_filter" : {
                    "type" : "kuromoji_stemmer", "minimum_length" : 4
                },
                "lowercase_filter" : {
                    "type" : "lowercase"
                }
            }
        }
    }
}'

curl -XPUT 'localhost:9200/collection1/_mapping/type1?pretty' -d '{
    "type1" : {
        "dynamic": "strict",
        "_all" : { "enabled" : false },
        "properties": {
            "tags" : { 
                "type" : "string", "store" : true, "index" : "not_analyzed"  
            },
            "url" : { 
                "type" : "string", "store" : true, "index" : "not_analyzed"  
            },
            "title" : {
                "type" : "string", "store" : true, "index" : "analyzed",
                "index_analyzer" : "text_cjk", "search_analyzer" : "text_cjk",
                "position_offset_gap" : 100,
                "term_vector" : "with_positions_offsets",
                "copy_to" : [ "title_ja" ]
            },
            "title_ja" : {
                "type" : "string", "store" : false, "index" : "analyzed",
                "index_analyzer" : "text_ja", "search_analyzer" : "text_ja",
                "position_offset_gap" : 100
            },
            "content" : {
                "type" : "string", "store" : true, "index" : "analyzed",
                "index_analyzer" : "text_cjk", "search_analyzer" : "text_cjk",
                "position_offset_gap" : 100,
                "term_vector" : "with_positions_offsets",
                "copy_to" : [ "content_ja" ]
            },
            "content_ja" : {
                "type" : "string", "store" : false, "index" : "analyzed",
                "index_analyzer" : "text_ja", "search_analyzer" : "text_ja",
                "position_offset_gap" : 100
            }
        }
    }
}'

sleep 3

curl -XPOST 'localhost:9200/collection1/_open?pretty' -d '{}'
