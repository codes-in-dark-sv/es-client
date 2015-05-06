
curl -X DELETE localhost:9200/santix



curl -X PUT "http://localhost:9200/santix" -d '
{
    "settings": {
        "index": {
            "analysis": {
                "analyzer": {
                    "autocomplete": {
                        "tokenizer": "whitespace",
                        "filter": [
                            "lowercase",
                            "engram"
                        ]
                    }
                },
                "filter": {
                    "engram": {
                        "type": "edgeNGram",
                        "min_gram": 1,
                        "max_gram": 10
                    }
                }
            }
        }
    },
    "mappings": {
        "items": {
            "properties": {
                "name": {
                    "type": "multi_field",
                    "fields": {
                        "name": {
                            "type": "string",
                            "index": "not_analyzed",
                            "store": "yes"
                        },
                        "autocomplete": {
                            "type": "string",
                            "index_analyzer": "autocomplete",
                            "index": "analyzed",
                            "search_analyzer": "standard"
                        }
                    }
                },
                "slug" : { "type" : "string", "store":"yes", "index":"not_analyzed" },
                "description" : { "type" : "string", "store":"yes", "index":"analyzed" },
                "username" : { "type" : "string", "store":"yes", "index":"analyzed" }
            }
        }
    }
}
'

#http://localhost:9200/santix/items/_mapping?pretty

curl -X PUT "http://localhost:9200/santix/items/1" -d '
  {
    "name" : "Lightweight Django",
    "slug" : "lightweight-django",
    "description" : "How can you take advantage of the Django framework to integrate complex client-side interactions and real-time features into your web applications?",
    "username" : "santo"
  }
'

curl -X PUT "http://localhost:9200/santix/items/2" -d '
  {
    "name" : "Hands-On Django",
    "slug" : "hands-on-django",
    "description" : "Ready to jump into web application development? This practical guide shows you how to build high-performing, elegant applications quickly—with less code—using the Django open source framework. If you know the basics of Python, HTML, CSS, and JavaScript, ",
    "username" : "santo"
  }
'

curl -X PUT "http://localhost:9200/santix/items/3" -d '
  {
    "name" : "Web Development with Django Cookbook",
    "slug" : "web-development-with-django-cookbook",
    "description" : "Django is easy to learn and solves all types of web development problems and questions, providing Python developers an easy solution to web-application development. ",
    "username" : "santo"
  }
'

curl -X GET "http://localhost:9200/santix/items/_search?pretty" -d '
{
    "query": {
        "term": {
            "name.autocomplete": "m"
        }
    },
    "facets": {
        "name": {
            "terms": {
                 "field": "name"
            }
        }
    }
}
'


