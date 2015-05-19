

Finagle Client in Scala Play

compile:

activator run

test:

curl -H "Content-Type: application/json" -X POST -d ' {"query":{"term":{"name.autocomplete":"d"}},"facets":{"name":{"terms":{"field":"name"}}}}' http://localhost:9000/term


microservice:

sh startserver.sh