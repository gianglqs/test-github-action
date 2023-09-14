import React from "react"

import type { ButtonProps, DialogProps } from "@mui/material"
import { SUBMIT, CLOSE, DELETE } from "@/utils/constant"

export interface ExtraButton extends Omit<ButtonProps, "hidden"> {
  label: string
  hide?: boolean
  buttonType?: typeof SUBMIT | typeof CLOSE | typeof DELETE
}

export interface AppDialogProps extends Omit<DialogProps, "title"> {
  title?: React.ReactNode
  onOk?: React.MouseEventHandler<HTMLButtonElement>
  description?: string
  children?: React.ReactNode
  extraButtons?: ExtraButton | ExtraButton[]
  leftButtons?: ExtraButton | ExtraButton[]
  closeText?: string
  okText?: string
  closable?: boolean
  height?: number
  loading?: boolean
  loadingSuccess?: boolean
  loadingError?: boolean
  enterToOk?: boolean
  okButtonProps?: ButtonProps
  closeButtonProps?: ButtonProps
  bodyStyles?: React.CSSProperties
  hideOkButton?: boolean
  hideIconButton?: boolean
  hideIconUpdateButton?: boolean
  hideCloseButton?: boolean
  hideButtonsAction?: boolean
  type?: "normal" | "success" | "error" | "info" | "warning"
  draggable?: boolean
  wikiPage?: string
  okTextHelper?: string | null
}
