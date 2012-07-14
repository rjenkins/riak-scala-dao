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
    map.put(key, t).get
  }

  def delete(t: T) { 
    map.retain({(key,value) => value != t})
  }

  def deleteByKey(key: String) {
    map.remove(key)
  }

  def findFor2i(index: String, value: String, converter: Converter[T]) = {
    val items = new ListBuffer[T]
    map.values.foreach({ v => if(v.toString.contains(value)) items.prepend(v)})
    items.toList
  }

  def findFor2i(index: String, value: Int, converter: Converter[T]) = {
    val items = new ListBuffer[T]
    map.values.foreach(v => if (v.toString.contains(value)) items.prepend(v))
    items.toList
  }

  def deleteFor2i(index: String, value: String) {}

  def deleteFor2i(index: String, value: Int) {}

  def deleteFor2i(index: (String, String)) = {

    val keys = new ListBuffer[String]
    map.keySet.foreach(key => if (map.get(key).toString.contains(index._2)) keys.prepend(key))
    keys.foreach(map.remove(_))
  }

  def getBucket = { "" }
}
