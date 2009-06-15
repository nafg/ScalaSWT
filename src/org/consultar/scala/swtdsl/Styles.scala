package org.consultar.scala.swtdsl

import org.eclipse.swt._

trait Styles {
  abstract case class Style(value: Int)
  trait ShellStyle extends Style  {
    def |(other: ShellStyle): ShellStyle = new Style(this.value | other.value) with ShellStyle
  }
  case object Border extends Style(SWT.BORDER) with ShellStyle
  case object Close extends Style(SWT.CLOSE) with ShellStyle
  case object Min extends Style(SWT.MIN) with ShellStyle
  case object Max extends Style(SWT.MAX) with ShellStyle
  case object Resize extends Style(SWT.RESIZE) with ShellStyle
  case object Title extends Style(SWT.TITLE) with ShellStyle
  case object NoTrim extends Style(SWT.NO_TRIM) with ShellStyle
  case object ShellTrim extends Style(SWT.SHELL_TRIM) with ShellStyle
  case object DialogTrim extends Style(SWT.DIALOG_TRIM) with ShellStyle
  case object Modeless extends Style(SWT.MODELESS) with ShellStyle
  case object PrimaryModal extends Style(SWT.PRIMARY_MODAL) with ShellStyle
  case object ApplicationModal extends Style(SWT.APPLICATION_MODAL) with ShellStyle
  case object SystemModal extends Style(SWT.SYSTEM_MODAL) with ShellStyle
}
