package org.consultar.scala.swtdsl

import _root_.scala.collection.mutable.ListBuffer

import org.eclipse.swt.SWT
import org.eclipse.swt.widgets._
import org.eclipse.core.databinding.beans.PojoObservables
import org.eclipse.core.databinding.DataBindingContext
import org.eclipse.core.databinding.observable.{IChangeListener, IStaleListener, Realm}
import org.eclipse.core.databinding.observable.value.{IObservableValue, IValueChangeListener}
import org.eclipse.jface.databinding.swt.SWTObservables

trait Binding {

  private lazy val dbc: DataBindingContext = new DataBindingContext()
  
  protected def emptyBinding() = (c: Control) => dbc

  protected def bind[T <: Any](setter: T => Unit, getter: () => T)(target: Control): DataBindingContext = {
    val property = new Property[T](setter, getter)
    target match {
      case t: Text =>
        dbc.bindValue(SWTObservables.observeText(target, SWT.Modify), new ObservableProperty(property), null, null)
      case _ =>
        dbc.bindValue(SWTObservables.observeSelection(target), new ObservableProperty(property), null, null)
    }
    dbc
  }
  
  protected class Property [T](val setter: T => Unit, val getter: () => T)
  
  protected var bindings = Map[Control, Control => DataBindingContext]()

  protected def setupBindings() {
    bindings.foreach(binding => binding._2(binding._1))
  }
  
  private class ObservableProperty[T <: Any](val property: Property[T]) extends IObservableValue {

    private val delegate = 
      PojoObservables.observeValue(
        new Object {
          def setProperty(v: T) = property.setter(v)
          def getProperty() = property.getter()
        }, "property"
      )
    
    override def addValueChangeListener(listener: IValueChangeListener) = delegate.addValueChangeListener(listener)
    
    override def removeValueChangeListener(listener: IValueChangeListener) = delegate.removeValueChangeListener(listener)
    
    override def addChangeListener(listener: IChangeListener) = delegate.addChangeListener(listener)
    
    override def removeChangeListener(listener: IChangeListener) = delegate.removeChangeListener(listener)
    
    override def addStaleListener(listener: IStaleListener) = delegate.addStaleListener(listener)
    
    override def removeStaleListener(listener: IStaleListener) = removeStaleListener(listener)

    override def setValue(value: Object) = delegate.setValue(value)
    
    override def getValue(): Object = delegate.getValue

    override def getValueType: Object = delegate.getValueType
    
    override def getRealm(): Realm = delegate.getRealm
    
    override def dispose() = delegate.dispose()

    override def isStale() = delegate.isStale
  }
}
