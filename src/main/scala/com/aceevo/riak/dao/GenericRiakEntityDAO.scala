package com.aceevo.riak.dao


/**
 * Created with IntelliJ IDEA.
 * User: rjenkins
 * Date: 5/18/12
 * Time: 9:00 AM
 * To change this template use File | Settings | File Templates.
 */

trait GenericRiakEntityDAO[K, T] {

  def persist(key: K, t: T): T
  def deleteByKey(key: K)
  def delete(t: T)

}
