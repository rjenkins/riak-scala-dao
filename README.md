riak-scala-dao
--------------

*An implementation of the GenericDAO pattern for use with <a href="http://basho.com/">Basho's Riak</a>*

Requirements
------------

* Scala 2.8.2 or 2.9.1
* riak-java-client 1.0.5

Using RiakJSONEntityDAO
-----------------------
```scala
case class Skateboard(id: String, brand: String, model: String, year: Int)
  extends PersistentEntity { def getKey=id }

val riakClient: IRiakClient = RiakFactory.pbcClient("localhost", 8087)
val skateboardDao = new RiakJSONEntityDAO[String, Skateboard]
  (new RiakDriver[Skateboard]("skateboards", riakClient))
skateboardDao.addStringIndex("brand")

val skateboard = new Skateboard("1", "Real", "Justin Brock", 2012)
skateboardDao.persist(skateboard.id, skateboard)
```

Extending AbstractRiakEntityDAO
-------------------------------

```scala
case class Guitar(var id: String, make: String, model: String, year: Int) {}

class GuitarDAO(bucket: String, storageDriver: RiakStorageDriver[String, Guitar])
  extends AbstractRiakEntityDAO[String, Guitar]("guitars", storageDriver) with Converter[Guitar] {

  def fromDomain(guitar: Guitar, vClock: VClock): IRiakObject = {
    val dataAsString = generate(guitar)
    val data = (dataAsString).map(_.toChar).toCharArray.map(_.toByte)

    val iRiakObject = RiakObjectBuilder.newBuilder("guitars", guitar.id).withVClock(vClock)
      .withContentType(Constants.CTYPE_JSON)
      .withValue(data).build()
    iRiakObject.addIndex("make", guitar.make)

  }

  def toDomain(riakObject: IRiakObject) = {
    val data = riakObject.getValueAsString
    parse[Guitar](data)
  }
}
```

Examples
--------

```scala
val riakClient: IRiakClient = RiakFactory.pbcClient("localhost", 8087)
val guitarDao = new GuitarDAO(new RiakDriver[Guitar]("guitars", riakClient))
```

Saving
------
```scala
val jazzMaster = new Guitar("1", "fender", "JazzMaster", 1963)
guitarDao.persist(jazzMaster.id, jazzMaster)
```

Retreiving
----------
```scala
val jazzMaster = new Guitar("1", "fender", "JazzMaster", 1963)
guitarDao.persist(jazzMaster.id, jazzMaster)
guitarDao.getByKey("1")
```

Deleting
--------
```scala
val jazzMaster = new Guitar("1", "fender", "JazzMaster", 1963)
guitarDao.persist(jazzMaster.id, jazzMaster)
guitarDao.deleteByKey("1")
```

Secondary Indexing
------------------

```scala
val jazzMaster = new Guitar("1", "fender", "JazzMaster", 1963)
guitarDao.persist(jazzMaster.id, jazzMaster)
guitarDao.findFor2i("make", "fender")
```

License
-------

Copyright (c) 2012 Ray Jenkins

Published under The Apache License, Version 2.0, see LICENSE