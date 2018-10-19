# Dictanova BIME Connector

## Configuration file

To get started, create a configuration file to customize data which will be exposed.

```
connector.port=8080
connector.bearer=customizethisbearer
client.id=DICTANOVA_CLIENT_ID
client.secret=DICTANOVA_CLIENT_SECRET
dataset.id=DICTANOVA_DATASET_ID
aggregation.field=TOPICS
aggregation.type=COUNT
aggregation.dimensions.fields=TOPICS,TERMS_POLARITY
aggregation.dimensions.groups=DISTINCT,DISTINCT
```

In this version, you can customize aggregation field and type and add some dimensions. As properties files do not support arrays, keys `aggregation.dimensions.fields` and `aggregation.dimensions.groups` are used to describe dimensions.

Please refer to [aggregation documentation](https://docs.dictanova.io/docs/aggregate-documents) to build your own aggregation.

## Build and start server

Clone the repository and built it.

```
mvn clean package
```

Start it

```
java -jar target/bime-1.0-SNAPSHOT.jar /path/to/conf.properties
```

Your server is now exposed on the port you've set up in your configuration file.


## Configure BIME

1. In your BIME account, choose the REST Connector.
2. Give it a name like "Dictanova API Connector"
3. Choose GET and fill the URL field with the url were your connector is exposed. Format must be left to JSON
4. In the path to values field, type "$" (for root)
5. Choose parameters and add a Header named `Authorization` with value `Bearer VALUE_OF_connector.bearer_FROM_YOUR_CONF`

Test your connection and see your values.

Enjoy !


