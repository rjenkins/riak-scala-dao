//
// Copyright 2012, Ray Jenkins
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//

package com.aceevo.riak.dao.impl

/**
 * Created with IntelliJ IDEA.
 * User: rjenkins
 * Date: 7/2/12
 * Time: 10:39 AM
 * To change this template use File | Settings | File Templates.
 */

import com.codahale.simplespec.Spec
import com.aceevo.riak.driver.impl.RiakDriver
import com.basho.riak.client.convert.Converter
import com.basho.riak.client.cap.VClock
import com.basho.riak.client.builders.RiakObjectBuilder
import com.aceevo.riak.driver.RiakStorageDriver
import com.codahale.jerkson.Json._
import com.basho.riak.client.{RiakFactory, IRiakClient, IRiakObject}
import org.junit.Test
import com.codahale.logula.Logging
import com.basho.riak.client.http.util.Constants

;

/**
 * Created with IntelliJ IDEA.
 * User: rjenkins
 * Date: 6/14/12
 * Time: 10:38 PM
 * To change this template use File | Settings | File Templates.
 */

class RiakEntityDAOSpec extends Spec with Logging {

  val riakClient: IRiakClient = RiakFactory.pbcClient("localhost", 8087)
  val guitarDao = new GuitarDAO(new RiakDriver[Guitar]("guitars", riakClient))

  class `riakEntityDAOSpecTest` {
    @Test def `can persist entity`() {

      val jazzMaster = new Guitar("1", "fender", "JazzMaster", 1963)
      guitarDao.persist(jazzMaster.id, jazzMaster)
    }

    @Test def `can retrieve by key`() {

      val jazzMaster = new Guitar("1", "fender", "JazzMaster", 1963)
      guitarDao.persist(jazzMaster.id, jazzMaster)
      guitarDao.getByKey("1").get must be(jazzMaster)
    }

    @Test def `can delete by key`() {

      val jazzMaster = new Guitar("1", "fender", "JazzMaster", 1963)
      guitarDao.persist(jazzMaster.id, jazzMaster)
      guitarDao.deleteByKey("1")
      guitarDao.getByKey(jazzMaster.id) must be(None)
    }

    @Test def `can find for 2i`() {

      val jazzMaster = new Guitar("1", "fender", "JazzMaster", 1963)
      guitarDao.persist(jazzMaster.id, jazzMaster)
      guitarDao.findFor2i("make", "fender").size must be(1)
    }

    @Test def `can delete for 2i`() {

      val jazzMaster = new Guitar("1", "fender", "JazzMaster", 1963)
      guitarDao.persist(jazzMaster.id, jazzMaster)
      guitarDao.deleteAllFor2i("make", "fender")
      guitarDao.findFor2i("make", "fender").size must be(0)
    }

  }

}

case class Guitar(var id: String, make: String, val model: String, val year: Int) {}

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
}
