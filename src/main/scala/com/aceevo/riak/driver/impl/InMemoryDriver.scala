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

package com.aceevo.riak.driver.impl

import com.basho.riak.client.convert.Converter
import collection.mutable.{ListBuffer, HashMap}
import com.aceevo.riak.driver.RiakStorageDriver
import com.codahale.logula.Logging

/**
 * Created with IntelliJ IDEA.
 * User: rjenkins
 * Date: 5/18/12
 * Time: 9:38 AM
 * To change this template use File | Settings | File Templates.
 */

class InMemoryDriver[T] extends RiakStorageDriver[String, T] with Logging {

  val map = new HashMap[String, T]()

  def getByKey(key: String, converter: Converter[T]): Option[T] = {
    map.get(key)
  }

  def persist(key: String, t: T, converter: Converter[T]) = {
    map.put(key, t)
    t
  }

  def delete(t: T) {
    for (key <- map.keySet) {
      if (map.get(key) == t) {
        map.remove(key)
      }
    }
  }

  def deleteByKey(key: String) {
    map.remove(key)
  }

  def findFor2i(index: (String, String), converter: Converter[T]) = {
    val items = new ListBuffer[T]
    for (value <- map.values) {
      if (value.toString.contains(index._2))
        items.prepend(value)
    }

    items.toList
  }

  def deleteFor2i(index: (String, String)) = {

    val keys = new ListBuffer[String]
    for (key <- map.keySet) {
      if (map.get(key).get.toString.contains(index._2))
        keys.prepend(key)
    }

    for (key <- keys) {
      map.remove(key)
    }

  }
}
