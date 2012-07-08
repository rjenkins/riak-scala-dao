package com.aceevo.riak.dao.impl

import com.basho.riak.client.{RiakFactory, IRiakClient}
import com.aceevo.riak.driver.impl.RiakDriver
import org.junit.Test
import com.codahale.simplespec.Spec
import com.codahale.logula.Logging
import com.aceevo.riak.model.PersistentEntity

/**
 * Created with IntelliJ IDEA.
 * User: rjenkins
 * Date: 7/6/12
 * Time: 4:57 PM
 * To change this template use File | Settings | File Templates.
 */

case class Skateboard(id: String, brand: String, model: String, year: Int) extends PersistentEntity { def getKey=id }

class RiakJSONEntityDAOSpec extends Spec with Logging {

  val riakClient: IRiakClient = RiakFactory.pbcClient("localhost", 8087)
  val skateboardDao = new RiakJSONEntityDAO[String, Skateboard](new RiakDriver[Skateboard]("skateboards", riakClient))

  skateboardDao.addStringIndex("brand")

  class `riakJSONEntityDAOSpecTest` {
    @Test def `can persist entity`() {

      val skateboard = new Skateboard("1", "Real", "Justin Brock", 2012)
      skateboardDao.persist(skateboard.id, skateboard)
    }

    @Test def `can retrieve by key`() {

      val skateboard = new Skateboard("1", "Real", "Justin Brock", 2012)
      skateboardDao.persist(skateboard.id, skateboard)
      skateboardDao.getByKey("1").get must be(skateboard)
    }

    @Test def `can delete by key`() {

      val skateboard = new Skateboard("1", "Real", "Justin Brock", 2012)
      skateboardDao.persist(skateboard.id, skateboard)
      skateboardDao.deleteByKey("1")
      skateboardDao.getByKey(skateboard.id) must be(None)
    }

    @Test def `can find for 2iString`() {

      val skateboard = new Skateboard("1", "Real", "Justin Brock", 2012)
      skateboardDao.persist(skateboard.id, skateboard)
      skateboardDao.findFor2iString("brand", "Real").size must be(1)
    }

    @Test def `can delete for 2iString`() {

      val skateboard = new Skateboard("1", "Real", "Justin Brock", 2012)
      skateboardDao.persist(skateboard.id, skateboard)
      skateboardDao.findFor2iString("brand", "Real").size must be(1)
      skateboardDao.deleteFor2iString("brand", "Real")
      skateboardDao.findFor2iString("brand", "Real").size must be(0)
    }

  }
}


