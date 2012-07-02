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
