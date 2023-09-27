import { forwardRef } from "react"

import { AppTextField } from "@/components"

import type { AppNumberFieldProps } from "./type"
import { NumberFormatBase } from 'react-number-format'

const AppNumberField: React.FC<AppNumberFieldProps> = forwardRef(
  (props, ref) => {
    const { onChange, ...numberFieldProps } = props

    return (
      <NumberFormatBase
        {...(numberFieldProps as any)}
        customInput={AppTextField}
        onValueChange={onChange}
        ref={ref as any}
      />
    )
  }
)

AppNumberField.defaultProps = {
  thousandSeparator: " ",
  allowNegative: false,
  decimalScale: 2,
  fixedDecimalScale: 2,
}

export * from "./type"
export { AppNumberField }
