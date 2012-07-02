riak-scala-dao
--------------

*An implementation of the GenericDAO pattern for use with <a href="http://basho.com/">Basho's Riak</a>*

Requirements
------------

* Scala 2.8.2 or 2.9.1

Extending RiakEntityDAO
----------------------_

```
class GuitarDAO(storageDriver: RiakStorageDriver[String, Guitar])
  extends RiakEntityDAO[String, Guitar](storageDriver) with Converter[Guitar] {

  def fromDomain(guitar: Guitar, vClock: VClock): IRiakObject = {
    val dataAsString = generate(guitar)
    val data = (dataAsString).map(_.toChar).toCharArray.map(_.toByte)

    val iRiakObject = RiakObjectBuilder.newBuilder("guitars", guitar.id).withVClock(vClock)
      .withContentType(Constants.CTYPE_JSON)
      .withValue(data).build()
    iRiakObject.addIndex("make", guitar.make)

  }

  def toDomain(riakObject: IRiakObject) = {
    val data = riakObject.getValueAsString()
    parse[Guitar](data)
  }
}```

Examples
--------

```
val riakClient: IRiakClient = RiakFactory.pbcClient("localhost", 8087)
val guitarDao = new GuitarDAO(new RiakDriver[Guitar]("guitars", riakClient))

* Saving *

val jazzMaster = new Guitar("1", "fender", "JazzMaster", 1963)
guitarDao.persist(jazzMaster.id, jazzMaster)

* Retreiving *
val jazzMaster = new Guitar("1", "fender", "JazzMaster", 1963)
guitarDao.persist(jazzMaster.id, jazzMaster)
guitarDao.getByKey("1")

* Deleting *
val jazzMaster = new Guitar("1", "fender", "JazzMaster", 1963)
guitarDao.persist(jazzMaster.id, jazzMaster)
guitarDao.deleteByKey("1")

* Secondary Indexing *

val jazzMaster = new Guitar("1", "fender", "JazzMaster", 1963)
guitarDao.persist(jazzMaster.id, jazzMaster)
guitarDao.findFor2i("make", "fender")
```