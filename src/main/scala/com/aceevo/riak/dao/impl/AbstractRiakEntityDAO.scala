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

import com.codahale.logula.Logging
import com.aceevo.riak.driver.RiakStorageDriver
import com.aceevo.riak.dao.RiakEntityDAO
import com.basho.riak.client.convert.Converter
import scala.Option
import collection.mutable.ListBuffer


/**
 * Created with IntelliJ IDEA.
 * User: rjenkins
 * Date: 5/16/12
 * Time: 10:45 AM
 * To change this template use File | Settings | File Templates.
 */


abstract class AbstractRiakEntityDAO[K, T](storageDriver: RiakStorageDriver[K,
  T]) extends RiakEntityDAO[K, T]
with Logging with Converter[T] {

  val stringIndexes = new ListBuffer[String]
  val integerIndexes = new ListBuffer[String]

  // Retrieve Entity by Key
  def getByKey(key: K): Option[T] = {
    storageDriver.getByKey(key, this)
  }

  // For a given key and type persist entity
  def persist(key: K, t: T): T = {
    storageDriver.persist(key, t, this)
  }

  def findFor2iString(index: (String, String)): List[T] = {
    storageDriver.findFor2iString(index, this)
  }

  def findFor2iInt(index: (String, Int)): List[T] = {
    storageDriver.findFor2iInt(index, this)
  }

  def delete(t: T) {
    storageDriver.delete(t)
  }

  def deleteByKey(key: K) {
    storageDriver.deleteByKey(key)
  }

  def deleteFor2iString(index: (String, String)) {
    storageDriver.deleteFor2iString(index)
  }

  def deleteFor2iInt(index: (String, Int)) {
    storageDriver.deleteFor2iInt(index)
  }

  def addStringIndex(index: String) {
    stringIndexes.prepend(index)
  }

  def addIntegerIndex(index: String) {
    integerIndexes.prepend(index)
  }

  def removeStringIndex(index: String): String = {
    stringIndexes.remove(stringIndexes.indexOf(index))
  }

  def removeIntegerIndex(index: String): String = {
    integerIndexes.remove(integerIndexes.indexOf(index))
  }
}





