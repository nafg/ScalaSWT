package org.consultar.scala.swtdsl

import org.eclipse.swt._
import org.eclipse.swt.graphics.Image
import org.eclipse.swt.layout._
import org.eclipse.swt.widgets._
import org.eclipse.swt.events._
import org.eclipse.core.databinding.observable.Realm
import org.eclipse.core.databinding.DataBindingContext
import org.eclipse.jface.databinding.swt.SWTObservables
import java.io.InputStream

abstract class SWTBuilder extends Layouts with Styles with Binding {
  private[this] val display: Display = new Display
  private[this] var currentParent: Composite = _
  
  def this(composite: Composite) {
    this()
    currentParent = composite
  }
  
  def withParent(parent: Composite)(code: =>Any) = {
    val oldParent = currentParent
    currentParent = parent
    code
    currentParent = oldParent
  }

  def run() {
    val shell = currentParent.getShell
    shell.pack()
    shell.open()
    Realm.runWithDefault(SWTObservables.getRealm(display), new Runnable {
                           override def run() {
                             setupBindings()
                             while(!shell.isDisposed)
                               if(!display.readAndDispatch()) display.sleep()
                           }
                         })
    display.dispose()
  }
  
  def shell(setups: (Shell => Unit)*)(block: => Unit): Shell = {
    currentParent = new Shell(display)
    currentParent.setLayout(new FillLayout)
    val shell = currentParent.asInstanceOf[Shell]
    setups.foreach(_(shell))
    block
    shell
  }

  def shell(style: ShellStyle)(setups: (Shell => Unit)*)(block: => Unit): Shell = {
    val shell = new Shell(display, style.value)
    shell.setLayout(new FillLayout)
    currentParent = shell
    setups.foreach(_(shell))
    block
    shell
  }

  def title(t: String)(titled: {def setText(t: String)}) = titled.setText(t)
  def icon(image: Image)(target: {def setImage(image: Image)}) = target.setImage(image)
  def icon(image: String)(target: {def setImage(image: Image)}) = target.setImage(new Image(display, image))
  def icon(image: InputStream)(target: {def setImage(image: Image)}) = target.setImage(new Image(display, image))

  def composite(setups: (Composite => Unit)*)(block: => Unit): Composite = {
    currentParent = new Composite(currentParent, SWT.NONE)
    currentParent.setLayout(new FillLayout)
    setups.foreach(_(currentParent))
    block
    currentParent = currentParent.getParent
    currentParent
  }
  
  def group(setups: (Group => Unit)*)(block: => Unit): Group = {
    currentParent = new Group(currentParent, SWT.NONE)
    currentParent.setLayout(new FillLayout)
    val group = currentParent.asInstanceOf[Group]
    setups.foreach(_(group))
    block
    currentParent = currentParent.getParent
    group
  }

  def label(t: String, setups: Label => Unit*): Label = {
		  val label = new Label(currentParent, SWT.NONE)
		  label.setText(t)
		  setups.foreach(_(label))
		  label
  }

  protected class DefaultEditValue(val value: String)
  protected implicit val editValue = new DefaultEditValue("")
  protected implicit def stringToDefaultEditValue(s: String) = new DefaultEditValue(s)

  def edit(binding: Control => DataBindingContext, setups: Text => Unit*)(implicit content: DefaultEditValue): Text = {
    val text = new Text(currentParent, SWT.BORDER)
    text.setText(content.value)
    setups.foreach(_(text))
    bindings += text -> binding
    text
  }

  def radioButton(binding: Control => DataBindingContext, setups: Button => Unit*): Button = {
    val button = new Button(currentParent, SWT.RADIO)
    setups.foreach(_(button))
    bindings += button -> binding
    button
  }
  
  def checkBox(binding: Control => DataBindingContext, setups: Button => Unit*): Button = {
    val button = new Button(currentParent, SWT.CHECK)
    setups.foreach(_(button))
    bindings += button -> binding
    button
  }
  
  def button(setups: Button => Unit*)(handler: SelectionEvent => Unit*): Button = {
    val button = new Button(currentParent, SWT.NONE)
    setups.foreach(_(button))
    if (handler.size > 0)
      button.addSelectionListener(
        new SelectionAdapter {
          override def widgetSelected(e: SelectionEvent) = handler(0)(e)
        }
      )
    button
  }
  
  def spinner(binding: Control => DataBindingContext, setups: Spinner => Unit*) = {
    val spinner = new Spinner(currentParent, SWT.NONE)
    setups.foreach(_(spinner))
    bindings += spinner -> binding
    spinner
  }
  
  def selectedIndex(i: Int)(s: Spinner) = s.setSelection(i)
  
  def selected(widget: {def setSelection(b: Boolean)}) = widget.setSelection(true)
}