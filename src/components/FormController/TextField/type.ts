// import type { UseControllerOptions, UseControllerProps } from 'react-hook-form'
import type { AppTextFieldProps } from "@/components/App/TextField/type"

export interface ControlledTextFieldProps
  extends Omit<AppTextFieldProps, "name" | "variant">,
    Omit<any, "onFocus"> {
  transformValue?(value): any
  limitText?: number
  variant?: "filled" | "outlined" | "standard"
  control: any
}
